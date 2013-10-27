/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
 *
 * selector.c
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

#include <selector.h>
#include <log.h>

#define MAX_NAME_LEN  128
#define MAX_MODEL_LEN 1024

struct _selector
{
    struct selector super;

    int argc;
    char **argv;
    char name[MAX_NAME_LEN];
    char model[MAX_MODEL_LEN];
    struct rectangle bounds;
};

static void lincxmap_selector_free(selector_t *self)
{
    TRACE();

    assert(self && *self);

    struct _selector *sel = (struct _selector*) *self;

    if (sel->argv) {
        int i;

        for (i = 0; i < sel->argc; i++) {
            free(sel->argv[i]);
        }

        free(sel->argv);
    }

    free(*self);
    *self = NULL;
}

static struct rectangle* lincxmap_selector_get_bounds(selector_t *self)
{
    struct _selector *sel = (struct _selector*) *self;

    return &sel->bounds;
}

static const char* lincxmap_selector_get_model(selector_t *self, int *argcp, char ***argvp)
{
    TRACE();

    assert(self && *self);

    struct _selector *sel = (struct _selector*) *self;

    *argcp = sel->argc;
    *argvp = sel->argv;
    return sel->model;
}

static const char* lincxmap_selector_get_name(selector_t *self)
{
    TRACE();

    assert(self && *self);

    return ((struct _selector*) *self)->name;
}

static void lincxmap_selector_set_model(selector_t *self, const char *model, int argc, char *argv[])
{
    TRACE();

    assert(self && *self);
    assert(model);
    assert(argv);

    int i;
    struct _selector *sel = (struct _selector*) *self;

    if (sel->argv) {
        for (i = 0; i < sel->argc; i++) {
            free(sel->argv[i]);
        }

        free(sel->argv);
        sel->argv = NULL;
    }

    sel->argc = argc;
    sel->argv = calloc(argc, sizeof(char*));

    if (sel->argv) {
        for (i = 0; i < argc; i++) {
            sel->argv[i] = strdup(argv[i]);
        }
    }

    if (model) {
        strncpy(sel->model, model, MAX_MODEL_LEN);
    }
}

static void lincxmap_selector_set_name(selector_t *self, const char *name)
{
    TRACE();

    assert(self && *self);
    assert(name);

    struct _selector *sel = (struct _selector*) *self;
    strncpy(sel->name, name, MAX_NAME_LEN);
}

static const struct selector clazz = {
    free      : lincxmap_selector_free,
    getbounds : lincxmap_selector_get_bounds,
    getmodel  : lincxmap_selector_get_model,
    getname   : lincxmap_selector_get_name,
    setmodel  : lincxmap_selector_set_model,
    setname   : lincxmap_selector_set_name,
};

selector_t selector_new(struct rectangle *bounds)
{
    TRACE();

    struct _selector *sel = calloc(1, sizeof(struct _selector));

    if (!sel) {
        ERROR("Out of memory!\n");
        return NULL;
    }

    sel->argc = 0;
    sel->argv = NULL;
    memcpy(&sel->super, &clazz, sizeof(struct selector));
    memcpy(&sel->bounds, bounds, sizeof(struct rectangle));

    return &sel->super;
}

