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
static uint8_t lincxmap_detector_choose_channel(detector_t *self, image_t *image, uint32_t *pixels)
{
	const static int nchannel = 4;

	int i, j;
	int x, y;			// coordinate of pixel
	int avg[4];			// average of histogram
	int dim[2];			// image dimension
	int hist[4][256];	// histogram of 3 channels
	uint8_t channel;	// selected channel
	double sd[4];		// standard deviation of pixels;
	struct rgb rgb;

	bzero(avg, sizeof(avg));
	bzero(dim, sizeof(dim));
	bzero(hist, sizeof(hist));
	bzero(sd, sizeof(sd));

	channel = 0;
	dim[0] = (*image)->getwidth(image);
	dim[1] = (*image)->getheight(image);

	// calculate histogram
	for (y = 0; y < dim[1]; y++) {
		for (x = 0; x < dim[0]; x++) {
			i = y * dim[1] + x;
			i2rgb(pixels[i], &rgb);
			hist[0][rgb.r]++;
			hist[1][rgb.g]++;
			hist[2][rgb.b]++;
			hist[3][rgb2gray(&rgb)]++;
		}
	}

	// calculate the standard deviation of histogram
	for (i = 0; i < nchannel; i++) {
		for (j = 0; j < 256; j++) {
			avg[i] += hist[i][j];
		}

		avg[i] /= 256;

		for (j = 0; j < 256; j++) {
			sd[i] += pow(hist[i][j] - avg[i], 2);
		}

		sd[i] = sqrt(sd[i] / 256);

		if (sd[i] < sd[channel]) {
			channel = i;
		}

		INFO("The Standard Deviation of Channel [%d]: %lf\n", i, sd[i]);
	}

	return channel; 
}

static struct sample* lincxmap_detector_auto(detector_t *self, image_t *image)
{
	// TODO detect automatically
	return NULL;
}

static struct sample* lincxmap_detector_manual(detector_t *self, image_t *image,
		struct selectors *sa)
{
	int i;
	int nos; 							// number of sample
	int dx, dy;							// delta between outer square and inner square
	int w, h, x, y;						// bounds of inner square
	int x1, x2, y1, y2;					// valid bounds of inner square
	int dim[2];							// image dimension
	double sum;							// sum of brightness
	double sqrt2;
	double radius;						// radius of circular selector
	struct rectangle *bounds;
	struct sample *smpa, **smp = &smpa;
	struct selectors *sa0;
	uint8_t *channel;					// selected channel
	uint32_t *pixels;					// pixels of image
	struct rgba rgba;
	struct hsl hsl;
	selector_t selector;

	sqrt2 = sqrt(2);
	dim[0] = (*image)->getwidth(image);
	dim[1] = (*image)->getheight(image);
	pixels = calloc(dim[0] * dim[1], sizeof(uint32_t));

	if (!pixels) {
		ERROR("Out of memory!\n");
		return NULL;
	}

	(*image)->getpixels(image, pixels, 0, dim[0], 0, 0, dim[0], dim[1]);

#ifndef NDEBUG
	DEBUG("Image Size: %u x %u\n", dim[0], dim[1]);

	char *row = calloc(dim[0], sizeof(char));
	if (!row) {
		ERROR("Out of memory!\n");
		goto skip_debug;
	}

	FILE *fimg = lincxmap_detector_open_test_image(dim[0], dim[1]);
	if (!fimg)
		goto skip_debug;

	for (y = 0; y < dim[1]; y++) {
		for (x = 0; x < dim[0]; x++) {
			i = y * dim[0] + x;
			i2rgba(pixels[i], &rgba);

#ifdef __CHANNEL_MODE__
	#if defined(__RED__)
			row[x] = rgba.r;
	#elif defined(__BLUE__)
			row[x] = rgba.b;
	#else
			row[x] = rgba.g;
	#endif
#else /* __CHANNEL_MODE__ */

			row[x] = i2gray(pixels[i]);

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

	switch (lincxmap_detector_choose_channel(self, image, pixels)) {
	case 0: //red
		INFO("Channel Red\n");
		channel = &rgba.r;
		break;
	case 1: // green
		INFO("Channel Green\n");
		channel = &rgba.g;
		break;
	case 2: // blue
		INFO("Channel Blue\n");
		channel = &rgba.b;
		break;
	default: // gray
		INFO("Channel Gray\n");
		channel = &rgba.alpha;
		break;
	}

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
				i = y * dim[0] + x;
				i2rgba(pixels[i], &rgba);
				rgba.r = rgba.b = rgba.g = *channel;
				sum += rgb2hsl((struct rgb*) &rgba, &hsl)->l;
			}
		}

		(*smp)->sum = nos;
		(*smp)->bv = sum / nos;
		(*smp)->cv = (*smp)->bv * __LINCXMAP_PRECISION__;
		sprintf((*smp)->name, "%s", selector->getname(&selector));

		smp = &(*smp)->next;
	}

	free(pixels);

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

