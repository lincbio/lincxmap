/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * detector.c
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <log.h>
#include <colorspace.h>
#include <detector.h>

#ifndef __LINCXMAP_PRECISION__
#define __LINCXMAP_PRECISION__ 100000
#endif /* __LINCXMAP_PRECISION__ */

#define NLEVEL 256
#define NCHANNEL 4

typedef struct
{
	struct __detector super;
} detector_impl_t;

#ifndef NDEBUG
static FILE* lincxmap_detector_open_test_image(int w, int h)
{
	FILE *file = fopen("/sdcard/DCIM/Camera/test.pgm", "wb");
	fprintf(file,"P5\n%u %u 255\n", w, h);

	return file;
}
#endif /* !NDEBUG */

/**
 * Choose the best channel of the specified image
 * 
 * 1st: Red
 * 2nd: Green
 * 3rd: Blue
 * 4th: Gray
 */
static int lincxmap_detector_choose_channel(histogram_t *hist)
{
	int i, nth;

	for (i = nth = 0; i < (*hist)->nchannels; i++) {
		if ((*hist)->stddev[i] < (*hist)->stddev[nth])
			nth = i;
	}

	DEBUG("Choose channel [%d]\n", nth);

	return nth;
}

static struct sample* lincxmap_detector_auto(detector_t *self, image_t *image)
{
	// TODO detect automatically
	return NULL;
}

static struct sample* lincxmap_detector_manual(detector_t *self, image_t *image, struct selectors *sa)
{
	int nos; 							// number of sample
	int dx, dy;							// delta between outer square and inner square
	int w, h, x, y;						// bounds of inner square
	int x1, y1, x2, y2;					// valid bounds of inner square
	int dim[3];							// image dimension & stride
	int area[9];						// area for smooth
	double sum;							// sum of brightness
	double sqrt2;
	double radius;						// radius of circular selector
	struct rectangle *bounds;
	struct sample *smpa, **smp = &smpa;
	struct selectors *sa0;
	struct hsl hsl;
	struct rgbx rgbx;
	uint8_t *px, *pixels;				// pixels of image
	uint32_t nth;						// selected channel number
	uint32_t nchannels;					// the number of channels
	histogram_t hist;
	selector_t selector;

	memset(dim, 0, sizeof(dim));
	memset(area, 0, sizeof(area));

	sqrt2 = sqrt(2);
	dim[0] = (*image)->getwidth(image);
	dim[1] = (*image)->getheight(image);
	dim[2] = (*image)->getstride(image);
	pixels = (*image)->getpixels(image);
	hist = (*image)->gethistogram(image);
	nchannels = (*image)->getnchannels(image);
	nth = lincxmap_detector_choose_channel(&hist);

	DEBUG("Image size: %d x %d : %d [%d]\n", dim[0], dim[1], dim[2], nchannels);

#ifndef NDEBUG

	char *row = calloc(dim[0], sizeof(char));
	if (!row) {
		ERROR("Out of memory!\n");
		goto skip_debug;
	}

	FILE *fimg = lincxmap_detector_open_test_image(dim[0], dim[1]);
	if (!fimg)
		goto skip_debug;

	image_type_t type = (*image)->gettype(image);
	uint32_t offset = image_get_component_offset(type);
	uint8_t *off = (uint8_t*) &offset;

	DEBUG("ARGB[%08x]{ %d, %d, %d, %d }\n", offset, off[0], off[1], off[2], off[3]);

	for (y = 0; y < dim[1]; y++) {
		for (x = 0; x < dim[0]; x++) {
			px = pixels + y * dim[2] + x * nchannels;
			rgbx.b = px[off[0]];
			rgbx.g = px[off[1]];
			rgbx.r = px[off[2]];
			rgbx.x = px[off[3]];

#ifdef __CHANNEL_MODE__
	#if defined(__RED__)
			row[x] = rgbx.r;
	#elif defined(__GREEN__)
			row[x] = rgbx.g;
	#elif defined(__BLUE__)
			row[x] = rgbx.b;
	#else
			row[x] = rgbx.x;
	#endif
#else /* __CHANNEL_MODE__ */

			row[x] = rgbx.x;

	#ifdef __USER_MODE__
			for (sa0 = sa; sa0; sa0 = sa0->next) {
				selector = sa0->selector;

				if (selector->contains(&selector, x, y)) {
					row[x] = 0xff;
				}
			}
	#endif /* __USER_MODE__ */
#endif /* !__CHANNEL_MODE__ */
		}

		fwrite(row, sizeof(char), dim[0], fimg);
	}

	free(row);
	fclose(fimg);

skip_debug:
#endif /* !NDEBUG */

	// calculate valid boundary
	for (sa0 = sa; sa0; sa0 = sa0->next) {
		selector = sa0->selector;
		bounds = selector->getbounds(&selector);

		// calculate the bounds of the inner square of circular selector
		radius = bounds->width / 2.0f;
		dx = dy = radius - (radius / sqrt2);
		x = bounds->x + dx;
		y = bounds->y + dy;
		w = h = radius * sqrt2;
		x1 = MAX(0, x);
		y1 = MAX(0, y);
		x2 = MIN(dim[0], x + w);
		y2 = MIN(dim[1], y + h);

		*smp = calloc(1, sizeof(struct sample));
		if (!*smp)
			continue;

		nos = 0;
		sum = 0;

		// calculate the brightness of each selector
		for (y = y1; y < y2; y++) {
			for (x = x1; x < x2; x++) {
				nos++;
				px = pixels + y * dim[2] + x * nchannels;
				rgbx.r = rgbx.b = rgbx.g = rgbx.x = px[nth];
				sum += rgb2hsl((struct rgb*) &rgbx, &hsl)->l;
			}
		}

		(*smp)->sum = nos;
		(*smp)->bv = sum / nos;
		(*smp)->cv = (*smp)->bv * __LINCXMAP_PRECISION__;
		sprintf((*smp)->name, "%s", selector->getname(&selector));

		smp = &(*smp)->next;
	}

	hist->free(&hist);

	return smpa;
}

static struct sample* lincxmap_detector_detect(detector_t *self, image_t *image, struct selectors *sa)
{
	assert(self && *self);
	assert(image);

	if (sa) {
		return lincxmap_detector_manual(self, image, sa);
	} else {
		return lincxmap_detector_auto(self, image);
	}
}

static void lincxmap_detector_free(detector_t *self)
{
	assert(self && *self);

	free(*self);
	*self = NULL;
}

extern detector_t detector_new()
{
	const static struct __detector ks_detector = {
		detect : lincxmap_detector_detect,
		free   : lincxmap_detector_free,
	};

	detector_impl_t *impl = calloc(1, sizeof(detector_impl_t));

	if (!impl) {
		ERROR("Out of memory!\n");
		return NULL;
	}

	memcpy(&impl->super, &ks_detector, sizeof(struct __detector));

	return &impl->super;
}

