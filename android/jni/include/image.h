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

#if _MSC_VER > 1000
#pragma once
#endif

#ifndef __LINCXMAP_IMAGE_H__
#define __LINCXMAP_IMAGE_H__

#include <stdarg.h>
#include <stdlib.h>

typedef struct __image* image_t;
struct __image
{
	void (*free)(image_t *self);

	void** (*getdata)(image_t *self);

	size_t (*getheight)(image_t *self);

	int (*getpixel)(image_t *self, int x, int y);

	size_t (*getstride)(image_t *self);

	size_t (*getwidth)(image_t *self);

	int (*ismutable)(image_t *self);

	void (*setpixel)(image_t *self, int x, int y, int px);
};

struct imgarg
{
	int fd;
};

#define image_option(T, name) \
	T (*name)(image_t *self, ...)

struct image_opts
{
	image_option(void,   free);
	image_option(int,    getpixel);
	image_option(size_t, getwidth);
	image_option(size_t, getheight);
	image_option(size_t, getstride);
	image_option(int,    ismutable);
	image_option(void*,  new);
	image_option(void,   setpixel);
};

#define IMAGE_OPTS(x) \
	{ \
		free      : lincxmap_##x##_free,       \
		getpixel  : lincxmap_##x##_get_pixel,  \
		getwidth  : lincxmap_##x##_get_width,  \
		getheight : lincxmap_##x##_get_height, \
		getstride : lincxmap_##x##_get_stride, \
		ismutable : lincxmap_##x##_is_mutable, \
		new       : lincxmap_##x##_new,        \
		setpixel  : lincxmap_##x##_set_pixel,  \
	}

#ifdef __cplusplus
extern "C" {
#endif

extern image_t image_new(size_t *w, size_t *h, struct image_opts *opts, void *arg);

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_IMAGE_H__ */
