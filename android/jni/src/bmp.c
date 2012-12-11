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

static void lincxmap_bmp_free(image_t *self, ...)
{
	assert(self && *self);
}

static int lincxmap_bmp_get_pixel(image_t *self, ...)
{
	assert(self && *self);

	int x, y;
	JNIEnv *env;
	va_list ap;
	struct bmparg *arg;

	arg = (struct bmparg*) *((*self)->getdata(self));
	env = arg->env;
	va_start(ap, self);
	x = va_arg(ap, int);
	y = va_arg(ap, int);
	va_end(ap);

	return (*env)->CallIntMethod(env, arg->obj, fun_bitmap_get_pixel, x, y);
}

static size_t lincxmap_bmp_get_width(image_t *self, ...)
{
	assert(self && *self);

	struct bmparg *arg = (struct bmparg*) *((*self)->getdata(self));
	JNIEnv *env = arg->env;

	return (*env)->CallIntMethod(env, arg->obj, fun_bitmap_get_width);
}

static size_t lincxmap_bmp_get_height(image_t *self, ...)
{
	assert(self && *self);

	struct bmparg *arg = (struct bmparg*) *((*self)->getdata(self));
	JNIEnv *env = arg->env;

	return (*env)->CallIntMethod(env, arg->obj, fun_bitmap_get_height);
}

static size_t lincxmap_bmp_get_stride(image_t *self, ...)
{
	return lincxmap_bmp_get_width(self);
}

static void lincxmap_bmp_set_pixel(image_t *self, ...)
{
	assert(self && *self);

	int x, y, px;
	JNIEnv *env;
	va_list ap;
	struct bmparg *arg;

	arg = (struct bmparg*) *((*self)->getdata(self));
	env = arg->env;
	va_start(ap, self);
	x = va_arg(ap, int);
	y = va_arg(ap, int);
	px = va_arg(ap, int);
	va_end(ap);

	(*env)->CallVoidMethod(env, arg->obj, fun_bitmap_set_pixel, x, y, px);
}

static int lincxmap_bmp_is_mutable(image_t *self, ...)
{
	assert(self && *self);

	JNIEnv *env;
	struct bmparg *arg;

	arg = (struct bmparg*) *((*self)->getdata(self));
	env = arg->env;

	return (*env)->CallBooleanMethod(env, arg->obj, fun_bitmap_is_mutable);
}

static void* lincxmap_bmp_new(image_t *self, ...)
{
	assert(self && *self);

	return *(*self)->getdata(self);
}

const struct image_opts bmp_opts = IMAGE_OPTS(bmp);
