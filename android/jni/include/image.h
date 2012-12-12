/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * image.h
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#ifndef __LINCXMAP_IMAGE_H__
#define __LINCXMAP_IMAGE_H__

#include <stdarg.h>
#include <stdint.h>

typedef struct image* image_t;
struct image
{
	void (*free)(image_t *self);

	void* (*getdata)(image_t *self);

	uint32_t (*getheight)(image_t *self);

	uint32_t  (*getpixel)(image_t *self, uint32_t x, uint32_t y);

	void (*getpixels)(image_t *self, uint32_t *pixels, uint32_t offset, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h);

	uint32_t (*getstride)(image_t *self);

	uint32_t (*getwidth)(image_t *self);

	void (*setpixel)(image_t *self, uint32_t x, uint32_t y, uint32_t px);

	void (*setpixels)(image_t *self, uint32_t *pixels, uint32_t offset, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h);
};

typedef struct image_options* image_options_t;
struct image_options
{
	uint32_t (*getpixel)(image_t *self, uint32_t x, uint32_t y);

	void (*getpixels)(image_t *self, uint32_t *pixels, uint32_t offset, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h);

	uint32_t (*getwidth)(image_t *self);

	uint32_t (*getheight)(image_t *self);

	uint32_t (*getstride)(image_t *self);

	int (*ismutable)(image_t *self);

	void (*setpixel)(image_t *self, uint32_t x, uint32_t y, uint32_t px);

	void (*setpixels)(image_t *self, uint32_t *pixels, uint32_t offset, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h);
};

#define IMAGE_OPTIONS(x) \
	{ \
		getpixel  : lincxmap_##x##_get_pixel,  \
		getpixels : lincxmap_##x##_get_pixels, \
		getwidth  : lincxmap_##x##_get_width,  \
		getheight : lincxmap_##x##_get_height, \
		getstride : lincxmap_##x##_get_stride, \
		ismutable : lincxmap_##x##_is_mutable, \
		setpixel  : lincxmap_##x##_set_pixel,  \
		setpixels : lincxmap_##x##_set_pixels, \
	}

#ifdef __cplusplus
extern "C" {
#endif

extern image_t image_new(image_options_t *ops, void *arg);

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_IMAGE_H__ */
