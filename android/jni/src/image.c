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
	struct image super;
	struct image_options opts;
	void *data;
} image_impl_t;

static void lincxmap_image_free(image_t *self)
{
	assert(self && *self);

	free(*self);
	*self = NULL;
}

static void* lincxmap_image_get_data(image_t *self)
{
	assert(self && *self);

	return ((image_impl_t*) *self)->data;
}

static uint32_t lincxmap_image_get_pixel(image_t *self, uint32_t x, uint32_t y)
{
	return ((image_impl_t*) *self)->opts.getpixel(self, x, y);
}

static void lincxmap_image_get_pixels(image_t *self, uint32_t *pixels, uint32_t offset, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h)
{
	((image_impl_t*) *self)->opts.getpixels(self, pixels, offset, stride, x, y, w, h);
}

static uint32_t lincxmap_image_get_width(image_t *self)
{
	return ((image_impl_t*) *self)->opts.getwidth(self);
}

static uint32_t lincxmap_image_get_height(image_t *self)
{
	return ((image_impl_t*) *self)->opts.getheight(self);
}

static uint32_t lincxmap_image_get_stride(image_t *self)
{
	return ((image_impl_t*) *self)->opts.getstride(self);
}

static void lincxmap_image_set_pixel(image_t *self, uint32_t x, uint32_t y, uint32_t px)
{
	((image_impl_t*) *self)->opts.setpixel(self, x, y, px);
}

static void lincxmap_image_set_pixels(image_t *self, uint32_t *pixels, uint32_t offset, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h)
{
	((image_impl_t*) *self)->opts.setpixels(self, pixels, offset, stride, x, y, w, h);
}

image_t image_new(image_options_t *opts, void *arg)
{
	const static struct image ks_image = {
		free      : lincxmap_image_free,
		getpixel  : lincxmap_image_get_pixel,
		getpixels : lincxmap_image_get_pixels,
		getwidth  : lincxmap_image_get_width,
		getheight : lincxmap_image_get_height,
		getstride : lincxmap_image_get_stride,
		getdata   : lincxmap_image_get_data,
		setpixel  : lincxmap_image_set_pixel,
		setpixels : lincxmap_image_set_pixels,
	};

	assert(opts && *opts);

	image_impl_t *impl = calloc(1, sizeof(image_impl_t));

	if (!impl)
		return NULL;

	impl->data = arg;
	memcpy(&impl->super, &ks_image, sizeof(struct image));
	memcpy(&impl->opts, *opts, sizeof(struct image_options));

	return &impl->super;
}
