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
	struct image_opts opts;
	void *data;
} image_impl_t;

static void lincxmap_image_free(image_t *self)
{
	assert(self && *self);

	image_impl_t *impl = (image_impl_t*) *self;

	if (impl->opts.free) {
		impl->opts.free(self);
	}

	free(*self);
	*self = NULL;
}

static void** lincxmap_image_get_data(image_t *self)
{
	assert(self && *self);

	return &((image_impl_t*) *self)->data;
}

static int lincxmap_image_get_pixel(image_t *self, int x, int y)
{
	return ((image_impl_t*) *self)->opts.getpixel(self, x, y);
}

static void lincxmap_image_set_pixel(image_t *self, int x, int y, int px)
{
	((image_impl_t*) *self)->opts.setpixel(self, x, y, px);
}

static size_t lincxmap_image_get_width(image_t *self)
{
	return ((image_impl_t*) *self)->opts.getwidth(self);
}

static size_t lincxmap_image_get_height(image_t *self)
{
	return ((image_impl_t*) *self)->opts.getheight(self);
}

static size_t lincxmap_image_get_stride(image_t *self)
{
	return ((image_impl_t*) *self)->opts.getstride(self);
}

static int lincxmap_image_is_mutable(image_t *self)
{
	return ((image_impl_t*) *self)->opts.ismutable(self);
}

const static struct __image gs_image = {
	free      : lincxmap_image_free,
	getpixel  : lincxmap_image_get_pixel,
	getwidth  : lincxmap_image_get_width,
	getheight : lincxmap_image_get_height,
	getstride : lincxmap_image_get_stride,
	getdata   : lincxmap_image_get_data,
	ismutable : lincxmap_image_is_mutable,
	setpixel  : lincxmap_image_set_pixel,
};

image_t image_new(size_t *w, size_t *h, struct image_opts *opts, void *arg)
{
	assert(opts);

	image_impl_t *impl = calloc(1, sizeof(image_impl_t));

	if (!impl)
		return NULL;

	impl->data = arg;
	memcpy(&impl->opts, opts, sizeof(struct image_opts));
	memcpy(&impl->super, &gs_image, sizeof(struct __image));

	if (opts->new && w && h) {
		opts->new((image_t*) &impl, *w, *h);
	}

	if (w) {
		*w = opts->getwidth((image_t*) &impl);
	}

	if (h) {
		*h = opts->getheight((image_t*) &impl);
	}

	return &impl->super;
}
