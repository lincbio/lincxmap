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

static uint32_t lincxmap_bitmap_get_pixel(image_t *self, uint32_t x, uint32_t y)
{
	assert(self && *self);

	struct bmparg *arg = (struct bmparg*) (*self)->getdata(self);
	JNIEnv *env = arg->env;

	return (*env)->CallIntMethod(env, arg->obj, fun_bitmap_get_pixel, x, y);
}

static void lincxmap_bitmap_get_pixels(image_t *self, uint32_t *pixels, uint32_t offset, uint32_t stride, uint32_t x, uint32_t y, uint32_t width, uint32_t height)
{
	assert(self && *self);

	size_t len = (width - y) * height - x;
	struct bmparg *arg = (struct bmparg*) (*self)->getdata(self);
	jint *buf;
	JNIEnv *env = arg->env;
	jintArray *arr = (*env)->NewIntArray(env, len);

	(*env)->CallVoidMethod(env, arg->obj, fun_bitmap_get_pixels, arr, offset, stride, x, y, width, height);
	buf = (*env)->GetIntArrayElements(env, arr, NULL);
	memcpy(pixels, buf, len * sizeof(uint32_t));
	(*env)->ReleaseIntArrayElements(env, arr, buf, 0);
}

static uint32_t lincxmap_bitmap_get_width(image_t *self)
{
	assert(self && *self);

	struct bmparg *arg = (struct bmparg*) (*self)->getdata(self);
	JNIEnv *env = arg->env;

	return (*env)->CallIntMethod(env, arg->obj, fun_bitmap_get_width);
}

static uint32_t lincxmap_bitmap_get_height(image_t *self)
{
	assert(self && *self);

	struct bmparg *arg = (struct bmparg*) (*self)->getdata(self);
	JNIEnv *env = arg->env;

	return (*env)->CallIntMethod(env, arg->obj, fun_bitmap_get_height);
}

static uint32_t lincxmap_bitmap_get_stride(image_t *self)
{
	return lincxmap_bitmap_get_width(self);
}

static void lincxmap_bitmap_set_pixel(image_t *self, uint32_t x, uint32_t y, uint32_t px)
{
	assert(self && *self);

	struct bmparg *arg = (struct bmparg*) (*self)->getdata(self);
	JNIEnv *env = arg->env;

	(*env)->CallVoidMethod(env, arg->obj, fun_bitmap_set_pixel, x, y, px);
}

static void lincxmap_bitmap_set_pixels(image_t *self, uint32_t *pixels, uint32_t offset, uint32_t stride, uint32_t x, uint32_t y, uint32_t width, uint32_t height)
{
	assert(self && *self);

	size_t len = (width - y) * height - x;
	struct bmparg *arg = (struct bmparg*) (*self)->getdata(self);
	JNIEnv *env = arg->env;
	jintArray *arr = (*env)->NewIntArray(env, len);
	jint *buf = (*env)->GetIntArrayElements(env, arr, NULL);

	memcpy(buf, pixels, len * sizeof(uint32_t));
	(*env)->ReleaseIntArrayElements(env, arr, buf, 0);
	(*env)->CallVoidMethod(env, arg->obj, fun_bitmap_set_pixels, arr, offset, stride, x, y, width, height);
}

static int lincxmap_bitmap_is_mutable(image_t *self)
{
	assert(self && *self);

	struct bmparg *arg = (struct bmparg*) (*self)->getdata(self);
	JNIEnv *env = arg->env;

	return (*env)->CallBooleanMethod(env, arg->obj, fun_bitmap_is_mutable);
}

const struct image_options bmp_opts = IMAGE_OPTIONS(bitmap);

