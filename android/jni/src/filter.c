/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * filter.c
 *
 * @date    Dec 11, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <stdlib.h>
#include <string.h>

#include <colorspace.h>
#include <filter.h>
#include <log.h>
#include <utils.h>

typedef struct
{
	struct __filter super;
	smooth_type_t type;
} filter_impl_t;

static void lincxmap_filter_free(filter_t *self)
{
	assert(self && *self);

	free(*self);
	*self = NULL;
}

/**
 * p1 p2 p3
 * p8 p0 p4
 * p7 p6 p5
 */
static int lincxmap_filter_mean_smooth(filter_t *self, image_t *in, image_t *out)
{
	assert(self && *self);
	assert(in && *in);

	int x, y;
	char pxs[9];
	size_t width = (*in)->getwidth(in);
	size_t height = (*in)->getheight(in);

	for (y = 1; y < height - 1; y++) {
		for (x = 1; x < width - 1; x++) {
			memset(pxs, 0, sizeof(pxs));

			pxs[0] = i2rgb((*in)->getpixel(in, x, y))->g;
			pxs[1] = i2rgb((*in)->getpixel(in, x - 1, y - 1))->g;
			pxs[2] = i2rgb((*in)->getpixel(in, x, y - 1))->g;
			pxs[3] = i2rgb((*in)->getpixel(in, x + 1, y - 1))->g;
			pxs[4] = i2rgb((*in)->getpixel(in, x + 1, y))->g;
			pxs[5] = i2rgb((*in)->getpixel(in, x + 1, y + 1))->g;
			pxs[6] = i2rgb((*in)->getpixel(in, x, y + 1))->g;
			pxs[7] = i2rgb((*in)->getpixel(in, x - 1, y + 1))->g;
			pxs[8] = i2rgb((*in)->getpixel(in, x - 1, y))->g;

			SORT(pxs, >);

			(*out)->setpixel(out, x, y, pxs[4]);
		}
	}

	return 0;
}

static int lincxmap_filter_median_smooth(filter_t *self, image_t *image, image_t *out)
{
	assert(self && *self);
	assert(image);

	return 0;
}

static int lincxmap_filter_gauss_smooth(filter_t *self, image_t *image, image_t *out)
{
	assert(self && *self);
	assert(image);

	return 0;
}

static int lincxmap_filter_none_smooth(filter_t *self, image_t *image, image_t *out)
{
	assert(self && *self);
	assert(image);

	return 0;
}

filter_t filter_new(smooth_type_t type)
{
	const static struct __filter ks_filter = {
		free   : lincxmap_filter_free,
		smooth : lincxmap_filter_none_smooth,
	};

	filter_impl_t *impl = calloc(1, sizeof(filter_impl_t));

	if (!impl)
		return NULL;

	impl->type = type;
	memcpy(&impl->super, &ks_filter, sizeof(struct __filter));

	switch (type) {
	case LINCXMAP_FILTER_TYPE_MEAN:
		impl->super.smooth = lincxmap_filter_mean_smooth;
		break;
	case LINCXMAP_FILTER_TYPE_MEDIAN:
		impl->super.smooth = lincxmap_filter_median_smooth;
		break;
	case LINCXMAP_FILTER_TYPE_GAUSS:
		impl->super.smooth = lincxmap_filter_gauss_smooth;
		break;
	default:
		break;
	}

	return &impl->super;
}
