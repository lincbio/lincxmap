/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * histogram.c
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <stdint.h>
#include <stdlib.h>

#include <image.h>
#include <log.h>

static void lincxmap_histogram_free(histogram_t *self)
{
	assert(self && *self);

	histogram_t hist = *self;

	if (hist->cumu)
		free(hist->cumu);

	if (hist->entropy)
		free(hist->entropy);

	if (hist->freq)
		free(hist->freq);

	if (hist->max)
		free(hist->freq);

	if (hist->min)
		free(hist->min);

	if (hist->prob)
		free(hist->prob);

	free(*self);
	*self = NULL;
}

static histogram_t lincxmap_histogram_new(uint32_t channel)
{
	uint32_t *nchannels;
	histogram_t hist = calloc(1, sizeof(struct histogram));

	if (!hist) {
		ERROR("Out of memory!\n");
		return NULL;
	}

	nchannels = (uint32_t*) &hist->nchannels;
	*nchannels = channel;
	hist->free = lincxmap_histogram_free;
	hist->freq = calloc(channel * IMAGE_GRAY_SCALE, sizeof(int));
	hist->prob = calloc(channel * IMAGE_GRAY_SCALE, sizeof(double));
	hist->cumu = calloc(channel * IMAGE_GRAY_SCALE, sizeof(double));
	hist->avg = calloc(channel, sizeof(double));
	hist->stddev = calloc(channel, sizeof(double));
	hist->entropy = calloc(channel, sizeof(double));
	hist->max = calloc(channel, sizeof(uint8_t));
	hist->min = calloc(channel, sizeof(uint8_t));

	return hist;
}
