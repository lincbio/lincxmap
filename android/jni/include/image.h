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

typedef struct __image* image_t;
struct __image
{
	uint32_t (*getpixel)(image_t *self, int x, int y);
	uint32_t (*getwidth)(image_t *self);
	uint32_t (*getheight)(image_t *self);
	uint32_t (*getstride)(image_t *self);
	void*    (*getdata)(image_t *self);

	void (*free)(image_t *self);
};

typedef void* (*image_option_f)(image_t *self, ...);

struct image_options
{
	image_option_f get_pixel;
	image_option_f get_width;
	image_option_f get_height;
	image_option_f get_stride;
};

#define IMAGE_OPTS(x) \
	{ \
		.get_pixel = x##_get_pixel, \
		.get_width = x##_get_width, \
		.get_height = x##_get_height, \
		.get_stride = x##_get_stride, \
	}

#ifdef __cplusplus
extern "C" {
#endif

extern image_t image_new(struct image_options *ops, void *arg);

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_IMAGE_H__ */
