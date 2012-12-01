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

typedef struct
{
	struct __detector super;
} detectorimpl_t;

#ifndef NDEBUG
static FILE* detector_open_test_image(int w, int h)
{
	FILE *file = fopen("/sdcard/DCIM/Camera/test.pgm", "wb");
	fprintf(file,"P5\n%u %u 255\n", w, h);

	return file;
}
#endif /* !NDEBUG */

static struct sample* detector_auto(detector_t *self, image_t image)
{
	// TODO detect automatically
	return NULL;
}

static struct sample* detector_manual(detector_t *self, image_t image,
		struct selectors *sa)
{
	int i;
	int px; 			// RGB value of a pixel
	int nos; 			// number of sample
	int width, height;	// image size
	int w, h, x, y;		// bounds of inner square
	int dx, dy;			// delta between outer square and inner square
	int x1, x2, y1, y2;	// valid bounds of inner square
	float radius;		// radius of circular selector
	float sqrt2;		// sqrtf(2.0f)
	float sum;			// sum of brightness
	rgb_t *rgb;
	selector_t selector;
	struct rectangle *bounds;
	struct sample *smpa, **smp = &smpa;
	struct selectors *sa0;

	sqrt2 = sqrtf(2.0f);
	width = image->getwidth(&image);
	height = image->getheight(&image);

#ifndef NDEBUG
	char *buf = calloc(width, sizeof(char));
	if (!buf)
		goto skip_debug;

	FILE *fimg = detector_open_test_image(width, height);
	if (!fimg)
		goto skip_debug;

	for (y = 0; y < height; y++) {
		for (x = 0; x < width; x++) {
#if defined(__EXTRACT_RED_CHANNEL__)
			buf[x] = i2rgb(image->getpixel(&image, x, y))->r;
#elif defined(__EXTRACT_GREEN_CHANNEL__)
			buf[x] = i2rgb(image->getpixel(&image, x, y))->g;
#elif defined(__EXTRACT_BLUE_CHANNEL__)
			buf[x] = i2rgb(image->getpixel(&image, x, y))->b;
#else
			buf[x] = i2gray(image->getpixel(&image, x, y));

			for (i = 0, sa0 = sa; sa0; sa0 = sa0->next, i++) {
				selector = sa0->selector;

				if (selector->contains(&selector, x, y)) {
					buf[x] = 0xff;
				}
			}
#endif
		}

		fwrite(buf, sizeof(char), width, fimg);
	}

	free(buf);
	fclose(fimg);

skip_debug:
#endif /* !NDEBUG */

	// calculate valid boundary
	for (i = 0, sa0 = sa; sa0; sa0 = sa0->next, i++) {
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
		x2 = MIN(width, x + w);
		y2 = MIN(height, y + h);

		*smp = calloc(1, sizeof(struct sample));
		if (!*smp)
			continue;

		nos = 0;
		sum = 0;

		for (y = y1; y < y2; y++) {
			for (x = x1; x < x2; x++) {
				nos++;
				px = image->getpixel(&image, x, y);
				rgb = i2rgb(px);
				rgb->r = rgb->b = rgb->g; // choose green channel
				sum += rgb2hsl(rgb)->l;
			}
		}

		(*smp)->sum = nos;
		(*smp)->bv = sum / nos;
		(*smp)->cv = (int) ((*smp)->bv * __LINCXMAP_PRECISION__);
		sprintf((*smp)->name, "%s", selector->getname(&selector));

		smp = &(*smp)->next;
	}

	return smpa;
}

static struct sample* detector_detect(detector_t *self, image_t image,
		struct selectors *sa)
{
	assert(self && *self);
	assert(image);

	if (sa) {
		return detector_manual(self, image, sa);
	} else {
		return detector_auto(self, image);
	}
}

static void detector_free(detector_t *self)
{
	assert(self && *self);

	free(*self);
	*self = NULL;
}

const static struct __detector gs_detector = {
	.detect = detector_detect,
	.free   = detector_free,
};

extern detector_t detector_new()
{
	detectorimpl_t *impl = calloc(1, sizeof(detectorimpl_t));

	assert(impl);
	memcpy(&impl->super, &gs_detector, sizeof(struct __detector));

	return &impl->super;
}
