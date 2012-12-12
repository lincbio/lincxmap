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

#ifdef __MEDIAN_SMOOTH__
static int cmp_uint8(const void *a, const void *b)
{
	return *((uint8_t*) a) - *((uint8_t*) b);
}
#endif

#ifndef NDEBUG
static FILE* lincxmap_detector_open_test_image(int w, int h)
{
	FILE *file = fopen("/sdcard/DCIM/Camera/test.pgm", "wb");
	fprintf(file,"P5\n%u %u 255\n", w, h);

	return file;
}
#endif /* !NDEBUG */

static void lincxmap_detector_get_histogram(uint32_t *pixels, uint32_t w, uint32_t h, int hist[NCHANNEL][NLEVEL], struct rgbx *min, struct rgbx *max)
{
	assert(pixels);

	int i, x, y;
	struct rgbx rgbx;

	// calculate histogram
	for (y = 0; y < h; y++) {
		for (x = 0; x < w; x++) {
			i = y * w + x;
			i2rgbx(pixels[i], &rgbx);

			hist[0][rgbx.r]++;
			hist[1][rgbx.g]++;
			hist[2][rgbx.b]++;
			hist[3][rgbx.x]++;

			min->r = MIN(min->r, rgbx.r);
			min->g = MIN(min->g, rgbx.g);
			min->b = MIN(min->b, rgbx.b);
			min->x = MIN(min->x, rgbx.x);

			max->r = MAX(max->r, rgbx.r);
			max->g = MAX(max->g, rgbx.g);
			max->b = MAX(max->b, rgbx.b);
			max->x = MAX(max->x, rgbx.x);
		}
	}
}

/**
 * Choose the best channel of the specified image
 * 
 * 1st: Red
 * 2nd: Green
 * 3rd: Blue
 * 4th: Gray
 */
static uint8_t lincxmap_detector_choose_channel(uint32_t w, uint32_t h, int hist[NCHANNEL][NLEVEL], double pd[NCHANNEL][NLEVEL], double cd[NCHANNEL][NLEVEL])
{
	int size;
	int i, j, k;
	int avg[NCHANNEL];			// average of histogram
	double entropy[NCHANNEL];	// the entropy of image
	double sd[NCHANNEL];		// standard deviation of pixels;
	uint8_t channel;			// selected channel

	size = w * h;
	memset(avg, 0, sizeof(avg));
	memset(sd, 0, sizeof(sd));
	memset(entropy, 0, sizeof(entropy));

	// calculate the standard deviation of histogram
	for (i = 0, channel = 0; i < NCHANNEL; i++) {
		for (j = 0; j < NLEVEL; j++) {
			if (hist[i][j] <= 0)
				continue;

			avg[i] += hist[i][j];
			pd[i][j] = hist[i][j] * 1.0f / size;
			entropy[i] -= pd[i][j] * log(pd[i][j]);
		}

		for (j = 0; j < NLEVEL; j++) {
			for (k = 0; k <= j; k++) {
				cd[i][j] += pd[i][k];
			}
		}

		for (avg[i] /= NLEVEL, j = 0; j < NLEVEL; j++) {
			sd[i] += pow(hist[i][j] - avg[i], 2);
		}

		sd[i] = sqrt(sd[i] / NLEVEL);

		if (sd[i] < sd[channel]) {
			channel = i;
		}

		INFO("The Entropy of Channel [%d]: %lf\n", i, entropy[i]);
		INFO("The Standard Deviation of Channel [%d]: %lf\n", i, sd[i]);
	}

	return channel; 
}

static struct sample* lincxmap_detector_auto(detector_t *self, image_t *image)
{
	// TODO detect automatically
	return NULL;
}

static struct sample* lincxmap_detector_manual(detector_t *self, image_t *image, struct selectors *sa)
{
	int i;
	int nos; 							// number of sample
	int dx, dy;							// delta between outer square and inner square
	int w, h, x, y;						// bounds of inner square
	int x1, y1, x2, y2;					// valid bounds of inner square
	int dim[2];							// image dimension
	int area[9];						// area for smooth
	int hist[NCHANNEL][NLEVEL];			// histogram
	double pd[NCHANNEL][NLEVEL];		// probility distribution
	double cd[NCHANNEL][NLEVEL];		// cumulative distribution
	double sum;							// sum of brightness
	double sqrt2;
	double radius;						// radius of circular selector
	struct rectangle *bounds;
	struct sample *smpa, **smp = &smpa;
	struct selectors *sa0;
	uint8_t nth;						// selected channel number
	uint8_t *channel;					// selected channel
	uint8_t *pmin, *pmax;				// min & max pixel of specified channel
	uint32_t *pixels;					// pixels of image
	struct rgbx max;					// max pixel value of each channel
	struct rgbx min;					// min pixel value of each channel
	struct rgbx rgbx;
	struct hsl hsl;
	selector_t selector;

	memset(dim, 0, sizeof(dim));
	memset(area, 0, sizeof(area));
	memset(hist, 0, sizeof(hist));
	memset(pd, 0, sizeof(pd));
	memset(cd, 0, sizeof(cd));

	sqrt2 = sqrt(2);
	dim[0] = (*image)->getwidth(image);
	dim[1] = (*image)->getheight(image);
	pixels = calloc(dim[0] * dim[1], sizeof(uint32_t));
	i2rgbx(pixels[0], &rgbx);
	max.r = min.r = rgbx.r;
	max.g = min.g = rgbx.g;
	max.b = min.b = rgbx.b;
	max.x = min.x = rgbx.x;

	DEBUG("Image Size: %u x %u\n", dim[0], dim[1]);

	if (!pixels) {
		ERROR("Out of memory!\n");
		return NULL;
	}

	(*image)->getpixels(image, pixels, 0, dim[0], 0, 0, dim[0], dim[1]);
	lincxmap_detector_get_histogram(pixels, dim[0], dim[1], hist, &min, &max);
	nth = lincxmap_detector_choose_channel(dim[0], dim[1], hist, pd, cd);

	switch (nth) {
	case 0: //red
		INFO("Channel Red\n");
		channel = &rgbx.r;
		break;
	case 1: // green
		INFO("Channel Green\n");
		channel = &rgbx.g;
		break;
	case 2: // blue
		INFO("Channel Blue\n");
		channel = &rgbx.b;
		break;
	default: // gray
		INFO("Channel Gray\n");
		channel = &rgbx.x;
		break;
	}

	pmin = (uint8_t*)((void*) &min + ((size_t) channel - (size_t) &rgbx));
	pmax = (uint8_t*)((void*) &max + ((size_t) channel - (size_t) &rgbx));
	DEBUG("min=%p pmin=%p rgbx=%p channel=%p\n", &min, pmin, &rgbx, channel);
	DEBUG("max=%p pmax=%p rgbx=%p channel=%p\n", &max, pmax, &rgbx, channel);

#ifndef NDEBUG

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
			i2rgbx(pixels[i], &rgbx);

#ifdef __EQUALIZE__
			rgbx.r = rgbx.g = rgbx.b = rgbx.x = cd[nth][*channel] * (*pmax - *pmin) + *pmin;
			pixels[i] = rgb2i((struct rgb*) &rgbx);
#endif

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
				i = 0;
				nos++;

#if defined(__MEDIAN_SMOOTH__) || defined(__MEAN_SMOOTH__)
				i2rgbx(pixels[(y - 1) * dim[0] + x - 1], &rgbx);
				area[i++] = *channel;
				i2rgbx(pixels[(y - 1) * dim[0] + x], &rgbx);
				area[i++] = *channel;
				i2rgbx(pixels[(y - 1) * dim[0] + x + 1], &rgbx);
				area[i++] = *channel;
				i2rgbx(pixels[y * dim[0] + x - 1], &rgbx);
				area[i++] = *channel;
				i2rgbx(pixels[y * dim[0] + x], &rgbx);
				area[i++] = *channel;
				i2rgbx(pixels[y * dim[0] + x + 1], &rgbx);
				area[i++] = *channel;
				i2rgbx(pixels[(y + 1) * dim[0] + x - 1], &rgbx);
				area[i++] = *channel;
				i2rgbx(pixels[(y + 1) * dim[0] + x], &rgbx);
				area[i++] = *channel;
				i2rgbx(pixels[(y + 1) * dim[0] + x + 1], &rgbx);
				area[i++] = *channel;
	#if defined(__MEDIAN_SMOOTH__)
				qsort(area, 9, sizeof(uint8_t), cmp_uint8);
				rgbx.r = rgbx.b = rgbx.g = area[4];
	#elif defined(__MEAN_SMOOTH__)
				rgbx.r = rgbx.b = rgbx.g = (area[0] + area[1] + area[2] + area[3] + area[4] + area[5] + area[6] + area[7] + area[8]) / 9;
	#endif
#else
				i = y * dim[0] + x;
				i2rgbx(pixels[i], &rgbx);
				rgbx.r = rgbx.b = rgbx.g = rgbx.x = *channel;
#endif
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

