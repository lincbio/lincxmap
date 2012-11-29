/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * cselector.c
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>

#include <circularselector.h>

typedef struct
{
	struct __selector super;
	struct rectangle bounds;
	char *name;
	float radius;
} circularselector_t;

static int circular_selector_contains(selector_t *self, int x, int y)
{
	assert(self && *self);

	float x0, y0;
	circularselector_t *cs;

	cs = (circularselector_t*) *self;
	x0 = cs->bounds.x + cs->radius;
	y0 = cs->bounds.y + cs->radius;

	return sqrt(pow(x0 - x, 2) + pow(y0 - y, 2)) <= cs->radius;
}

static void circular_selector_free(selector_t *self)
{
	assert(self && *self);

	circularselector_t *cs = (circularselector_t*) *self;

	if (cs->name) {
		free(cs->name);
	}
	free(*self);
	*self = NULL;
}

static struct rectangle* circular_selector_get_bounds(selector_t *self)
{
	assert(self && *self);

	return &((circularselector_t*) *self)->bounds;
}

static const char* circular_selector_get_name(selector_t *self)
{
	assert(self && *self);

	return ((circularselector_t*) *self)->name;
}

static void circular_selector_set_bounds(selector_t *self, struct rectangle *bounds)
{
	assert(self && *self);
	assert(bounds);

	memcpy(&((circularselector_t*) *self)->bounds, bounds, sizeof(struct rectangle));
}

static void circular_selector_set_name(selector_t *self, const char *name, size_t len)
{
	assert(self && *self);

	char *buf, *tmp;
	circularselector_t *cs;

	buf = calloc(len + 1, sizeof(char));
	cs = (circularselector_t*) *self;

	if (!buf)
		return;

	tmp = cs->name;
	cs->name = memcpy(buf, name, len);

	if (tmp) {
		free(tmp);
	}
}

selector_t circular_selector_new(float radius)
{

	const static struct __selector ks_cselector = {
		.contains   = circular_selector_contains,
		.free       = circular_selector_free,
		.getbounds  = circular_selector_get_bounds,
		.getname    = circular_selector_get_name,
		.setbounds  = circular_selector_set_bounds,
		.setname    = circular_selector_set_name,
	};

	circularselector_t *impl = calloc(1, sizeof(circularselector_t));

	assert(impl);
	impl->radius = radius;
	memcpy(&impl->super, &ks_cselector, sizeof(struct __selector));

	return &impl->super;
}

