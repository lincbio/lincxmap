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

/**
 * Choose the best channel of the specified image
 * 
 * 1st: Red
 * 2nd: Green
 * 3rd: Blue
 * 4th: Gray
 */
static uint8_t detector_choose_channel(detector_t *self, image_t image)
{
	const static int nchannel = 4;

	int i, j;
	int x, y;			// coordinate of pixel 
	int avg[4];			// average of histogram
	int dim[2];			// image dimension
	int hist[4][256];	// histogram of 3 channels
	uint8_t channel;	// selected channel
	double sd[4];		// standard deviation of pixels;
	rgb_t *rgb;

	bzero(avg, sizeof(avg));
	bzero(dim, sizeof(dim));
	bzero(hist, sizeof(hist));
	bzero(sd, sizeof(sd));

	channel = 0;
	dim[0] = image->getwidth(&image);
	dim[1] = image->getheight(&image);

	// calculate histogram
	for (y = 0; y < dim[1]; y++) {
		for (x = 0; x < dim[0]; x++) {
			rgb = i2rgb(image->getpixel(&image, x, y));
			hist[0][rgb->r]++;
			hist[1][rgb->g]++;
			hist[2][rgb->b]++;
			hist[3][rgb2gray(rgb)]++;
		}
	}

	// calculate the standard deviation of histogram
	for (i = 0; i < nchannel; i++) {
		for (j = 0; j <= 0xff; j++) {
			avg[i] += hist[i][j];
		}

		avg[i] /= 0xff;

		for (j = 0; j <= 0xff; j++) {
			sd[i] += pow(hist[i][j] - avg[i], 2);
		}

		sd[i] = sqrt(sd[i] / 0xff);

		if (sd[i] < sd[channel]) {
			channel = i;
		}

		INFO("The Standard Deviation of Channel [%d]: %lf\n", i, sd[i]);
	}

	return channel; 
}

static struct sample* detector_auto(detector_t *self, image_t image)
{
	// TODO detect automatically
	return NULL;
}

static struct sample* detector_manual(detector_t *self, image_t image,
		struct selectors *sa)
{
	int i;
	int px; 							// RGB value of a pixel
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
	rgba_t rgb;
	selector_t selector;

	sqrt2 = sqrt(2);
	dim[0] = image->getwidth(&image);
	dim[1] = image->getheight(&image);

#ifndef NDEBUG
	char *pixels = calloc(dim[0] * dim[1], sizeof(char));
	if (!pixels)
		goto skip_debug;

	FILE *fimg = detector_open_test_image(dim[0], dim[1]);
	if (!fimg)
		goto skip_debug;

	for (y = 0; y < dim[1]; y++) {
		for (x = 0; x < dim[0]; x++) {
			memcpy(&rgb, i2rgb(image->getpixel(&image, x, y)), sizeof(rgb_t));

#ifdef __CHANNEL_MODE__
	#if defined(__RED__)
			pixels[x] = rgb.r;
	#elif defined(__BLUE__)
			pixels[x] = rgb.b;
	#else
			pixels[x] = rgb.g;
	#endif
#else /* __CHANNEL_MODE__ */
			px = i2gray(image->getpixel(&image, x, y));

			for (sa0 = sa; sa0; sa0 = sa0->next) {
				selector = sa0->selector;

				if (selector->contains(&selector, x, y)) {
					px = 0xff;
				}
			}

			pixels[x] = px;
#endif /* !__CHANNEL_MODE__ */
		}

		fwrite(pixels, sizeof(char), dim[0], fimg);
	}

	free(pixels);
	fclose(fimg);

skip_debug:
#endif /* !NDEBUG */

	switch (detector_choose_channel(self, image)) {
	case 0: //red
		INFO("Channel Red\n");
		channel = &rgb.r;
		break;
	case 1: // green
		INFO("Channel Green\n");
		channel = &rgb.g;
		break;
	case 2: // blue
		INFO("Channel Blue\n");
		channel = &rgb.b;
		break;
	default: // gray
		INFO("Channel Gray\n");
		channel = &rgb.alpha;
		break;
	}

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
		x2 = MIN(dim[0], x + w);
		y2 = MIN(dim[1], y + h);

		*smp = calloc(1, sizeof(struct sample));
		if (!*smp)
			continue;

		nos = 0;
		sum = 0;

		for (y = y1; y < y2; y++) {
			for (x = x1; x < x2; x++) {
				nos++;
				px = image->getpixel(&image, x, y);
				memcpy(&rgb, i2rgb(px), sizeof(rgb_t));
				rgb.alpha = i2gray(px);
				rgb.r = rgb.b = rgb.g = *channel;
				sum += rgb2hsl((rgb_t*) &rgb)->l;
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
