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
#include <fcntl.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <asm/byteorder.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <log.h>
#include <colorspace.h>
#include <detector.h>

#ifndef __LINCXMAP_PRECISION__
#define __LINCXMAP_PRECISION__ 100000
#endif /* __LINCXMAP_PRECISION__ */

#define TEST_PGM_FILE "/sdcard/DCIM/Camera/test.pgm"

typedef struct
{
	struct __detector super;
} detector_impl_t;

#ifndef NDEBUG
static int lincxmap_detector_write_to_pgm(image_t *image, int fd)
{
	TRACE();

	int i, nwrite;
	uint32_t w = (*image)->getwidth(image);
	uint32_t h = (*image)->getheight(image);
	uint32_t n = (*image)->getnchannels(image);
	uint8_t *row = calloc(w, n * sizeof(uint32_t));

	if (!row)
		return EOF;

	fdprintf(fd,"P5\n%u %u 255\n", w, h);

	for (i = nwrite = 0; i < h; i++) {
		(*image)->getpixels(image, &row, w, 0, i, w, 1);
		nwrite += write(fd, row, w * n * sizeof(uint8_t));
	}
	
	free(row);

	return nwrite;
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
	TRACE();

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
	TRACE();

	// TODO detect automatically
	return NULL;
}

static struct sample* lincxmap_detector_manual(detector_t *self, image_t *image, struct selectors *sa)
{
	TRACE();

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
	hist = (*image)->gethistogram(image);
	nchannels = (*image)->getnchannels(image);
	nth = lincxmap_detector_choose_channel(&hist);
	pixels = calloc(dim[0], sizeof(uint32_t));

	if (!pixels) {
		ERROR("Out of memory!");
		return NULL;
	}

	DEBUG("Image size: %d x %d : %d [%d]\n", dim[0], dim[1], dim[2], nchannels);

#if 0
	int fd = open(TEST_PGM_FILE, O_CREAT | O_RDWR);
	if (fd <= 0)
		goto skip_debug;

	image_t img = (*image)->getchannel(image, nth);
	image_writer_t pgm_writer = lincxmap_detector_write_to_pgm;
	img->write(&img, fd, &pgm_writer);
	img->free(&img);
	close(fd);

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
			(*image)->getpixels(image, &pixels, dim[2], 0, y, dim[0], 1);

			for (x = x1; x < x2; x++) {
				nos++;
				px = pixels + x * nchannels;
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

	free(pixels);
	hist->free(&hist);

	return smpa;
}

static struct sample* lincxmap_detector_detect(detector_t *self, image_t *image, struct selectors *sa)
{
	TRACE();

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
	TRACE();

	assert(self && *self);

	free(*self);
	*self = NULL;
}

extern detector_t detector_new()
{
	TRACE();

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

