/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * pgm.c
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <stdarg.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>

#include <log.h>
#include <image.h>

typedef struct
{
	struct imgarg super;
	char *data;
	size_t width;
	size_t height;
} pgm_t;

static void lincxmap_pgm_free(image_t *self, ...)
{
	assert(self && *self);

	int fd;
	pgm_t **pgm = (pgm_t**) (*self)->getdata(self);

	if ((*pgm)->data) {
		fd = (*pgm)->super.fd;
		fdprintf(fd, "P5\n%u %u 255\n", (*pgm)->width, (*pgm)->height);
		write(fd, (*pgm)->data, (*pgm)->width * (*pgm)->height);
		fsync(fd);
		free((*pgm)->data);
	}
	free(*pgm);
	*pgm = NULL;
}

static int lincxmap_pgm_get_pixel(image_t *self, ...)
{
	assert(self && *self);

	int x, y;
	va_list ap;
	pgm_t *arg;

	arg = (pgm_t*) *((*self)->getdata(self));
	if (!arg->data)
		return 0;

	va_start(ap, self);
	x = va_arg(ap, int);
	y = va_arg(ap, int);
	va_end(ap);

	return arg->data[y * arg->height + x];
}

static void lincxmap_pgm_set_pixel(image_t *self, ...)
{
	assert(self && *self);

	int x, y, px;
	va_list ap;
	pgm_t *arg;

	arg = (pgm_t*) *((*self)->getdata(self));
	if (!arg->data)
		return;

	va_start(ap, self);
	x = va_arg(ap, int);
	y = va_arg(ap, int);
	px = va_arg(ap, int);
	va_end(ap);

	arg->data[y * arg->height + x] = px;
}

static size_t lincxmap_pgm_get_width(image_t *self, ...)
{
	assert(self && *self);

	return ((pgm_t*) *((*self)->getdata(self)))->width;
}

static size_t lincxmap_pgm_get_height(image_t *self, ...)
{
	assert(self && *self);

	return ((pgm_t*) *((*self)->getdata(self)))->height;
}

static size_t lincxmap_pgm_get_stride(image_t *self, ...)
{
	return lincxmap_pgm_get_width(self);
}

static int lincxmap_pgm_is_mutable(image_t *self, ...)
{
	return 1;
}

static void* lincxmap_pgm_new(image_t *self, ...)
{
	va_list ap;
	size_t w, h;
	pgm_t *pgm;
	void **data;

	va_start(ap, self);
	w = va_arg(ap, size_t);
	h = va_arg(ap, size_t);
	va_end(ap);

	assert(w > 0 && h > 0);

	data = (*self)->getdata(self);
	pgm = calloc(1, sizeof(pgm_t));
	pgm->width = w;
	pgm->height = h;
	pgm->data = calloc(w * h, sizeof(char));
	memcpy(&pgm->super, *data, sizeof(struct imgarg));
	*data = pgm;

	return *data;
}

const struct image_opts pgm_opts = IMAGE_OPTS(pgm);
