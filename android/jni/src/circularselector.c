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
#include <log.h>

#define MAX_NAME_LEN  128
#define MAX_MODEL_LEN 1024

typedef struct
{
	struct selector super;
	struct rectangle bounds;
	char name[MAX_NAME_LEN];
    char model[MAX_MODEL_LEN];
	float radius;
    int argc;
    char **argv;
} circular_selector_t;

static int lincxmap_circular_selector_contains(selector_t *self, uint32_t x, uint32_t y)
{
	TRACE();

	assert(self && *self);

	float x0, y0;
	circular_selector_t *cs;

	cs = (circular_selector_t*) *self;
	x0 = cs->bounds.x + cs->radius;
	y0 = cs->bounds.y + cs->radius;

	return sqrt(pow(x0 - x, 2) + pow(y0 - y, 2)) <= cs->radius;
}

static void lincxmap_circular_selector_free(selector_t *self)
{
	TRACE();

	assert(self && *self);

	circular_selector_t *cs = (circular_selector_t*) *self;

    if (cs->argv) {
        int i;

        for (i = 0; i < cs->argc; i++) {
            free(cs->argv[i]);
        }

        free(cs->argv);
    }

	free(*self);
	*self = NULL;
}

static struct rectangle* lincxmap_circular_selector_get_bounds(selector_t *self)
{
	TRACE();

	assert(self && *self);

	return &((circular_selector_t*) *self)->bounds;
}

static const char* lincxmap_circular_selector_get_model(selector_t *self, int *argcp, char ***argvp)
{
    TRACE();

    assert(self && *self);

	circular_selector_t *cs = (circular_selector_t*) *self;

    *argcp = cs->argc;
    *argvp = cs->argv;
	return cs->model;
}

static const char* lincxmap_circular_selector_get_name(selector_t *self)
{
	TRACE();

	assert(self && *self);

	return ((circular_selector_t*) *self)->name;
}

static void lincxmap_circular_selector_set_bounds(selector_t *self, struct rectangle *bounds)
{
	TRACE();

	assert(self && *self);
	assert(bounds);

	memcpy(&((circular_selector_t*) *self)->bounds, bounds, sizeof(struct rectangle));
}

static void lincxmap_circular_selector_set_model(selector_t *self, const char *model, int argc, char *argv[])
{
	TRACE();

	assert(self && *self);
    assert(model);
    assert(argv);

	circular_selector_t *cs = (circular_selector_t*) *self;

    if (cs->argv) {
        int i;

        for (i = 0; i < cs->argc; i++) {
			free(cs->argv[i]);
		}

        free(cs->argv);
        cs->argv = NULL;
    }

    cs->argc = argc;
    cs->argv = calloc(argc, sizeof(char*));

    if (cs->argv) {
        int i;

        for (i = 0; i < argc; i++) {
            cs->argv[i] = strdup(argv[i]);
        }
    }

    if (model) {
        strncpy(cs->model, model, MAX_MODEL_LEN);
    }
}

static void lincxmap_circular_selector_set_name(selector_t *self, const char *name)
{
	TRACE();

	assert(self && *self);
    assert(name);

	circular_selector_t *cs = (circular_selector_t*) *self;
    strncpy(cs->name, name, MAX_NAME_LEN);
}

selector_t circular_selector_new(float radius)
{
	TRACE();


	const static struct selector ks_cselector = {
		contains  : lincxmap_circular_selector_contains,
		free      : lincxmap_circular_selector_free,
		getbounds : lincxmap_circular_selector_get_bounds,
		getmodel  : lincxmap_circular_selector_get_model,
		getname   : lincxmap_circular_selector_get_name,
		setbounds : lincxmap_circular_selector_set_bounds,
		setmodel  : lincxmap_circular_selector_set_model,
		setname   : lincxmap_circular_selector_set_name,
	};

	circular_selector_t *impl = calloc(1, sizeof(circular_selector_t));

	if (!impl) {
		ERROR("Out of memory!\n");
		return NULL;
	}

	impl->argc = 0;
	impl->argv = NULL;
	impl->radius = radius;
	memcpy(&impl->super, &ks_cselector, sizeof(struct selector));

	return &impl->super;
}

