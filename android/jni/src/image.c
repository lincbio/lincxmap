/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * image.c
 *
 * @date    Sep 09, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <stdlib.h>
#include <string.h>

#include <image.h>
#include <log.h>

typedef struct
{
	struct __image super;
	struct image_options ops;
	void *data;
} imageimpl_t;

static void image_free(image_t *self)
{
	assert(self && *self);

	free(*self);
	*self = NULL;
}

static void* image_get_data(image_t *self)
{
	assert(self && *self);

	return ((imageimpl_t*) *self)->data;
}

static uint32_t image_get_pixel(image_t *self, int x, int y)
{
	return (uint32_t) ((imageimpl_t*) *self)->ops.get_pixel(self, x, y);
}

static uint32_t image_get_width(image_t *self)
{
	return (uint32_t) ((imageimpl_t*) *self)->ops.get_width(self);
}

static uint32_t image_get_height(image_t *self)
{
	return (uint32_t) ((imageimpl_t*) *self)->ops.get_height(self);
}

static uint32_t image_get_stride(image_t *self)
{
	return (uint32_t) ((imageimpl_t*) *self)->ops.get_stride(self);
}

const static struct __image gs_image = {
	.getpixel  = image_get_pixel,
	.getwidth  = image_get_width,
	.getheight = image_get_height,
	.getstride = image_get_stride,
	.getdata   = image_get_data,
	.free      = image_free,
};

image_t image_new(struct image_options *ops, void *arg)
{
	assert(ops);

	imageimpl_t *impl = calloc(1, sizeof(imageimpl_t));

	if (!impl)
		return NULL;

	impl->data = arg;
	memcpy(&impl->ops, ops, sizeof(struct image_options));
	memcpy(&impl->super, &gs_image, sizeof(struct __image));

	return &impl->super;
}
