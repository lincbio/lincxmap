/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * bitmap.c
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <stdlib.h>
#include <string.h>

#include <log.h>
#include <image.h>

#include "native.h"

static void* bitmap_get_pixel(image_t *self, ...)
{
	assert(self && *self);

	int x, y;
	JNIEnv *env;
	va_list ap;
	struct image_arg *arg;

	arg = (struct image_arg*) (*self)->getdata(self);
	env = arg->env;
	va_start(ap, self);
	x = va_arg(ap, int);
	y = va_arg(ap, int);
	va_end(ap);

	return (void*) (*env)->CallIntMethod(env, arg->obj, fun_bitmap_get_pixel, x, y);
}

static void* bitmap_get_width(image_t *self, ...)
{
	assert(self && *self);

	struct image_arg *arg = (struct image_arg*) (*self)->getdata(self);
	JNIEnv *env = arg->env;

	return (void*) (*env)->CallIntMethod(env, arg->obj, fun_bitmap_get_width);
}

static void* bitmap_get_height(image_t *self, ...)
{
	assert(self && *self);

	struct image_arg *arg = (struct image_arg*) (*self)->getdata(self);
	JNIEnv *env = arg->env;

	return (void*) (*env)->CallIntMethod(env, arg->obj, fun_bitmap_get_height);
}

static void* bitmap_get_stride(image_t *self, ...)
{
	return bitmap_get_width(self);
}

const struct image_options bmp_options = {
	.get_pixel  = bitmap_get_pixel,
	.get_width  = bitmap_get_width,
	.get_height = bitmap_get_height,
	.get_stride = bitmap_get_stride,
};
