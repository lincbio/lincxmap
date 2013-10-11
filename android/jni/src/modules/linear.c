/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * linear.c
 *
 * @date    Mar 02, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <math.h>
#include <stdarg.h>
#include <stdlib.h>
#include <string.h>

#include <log.h>
#include <model.h>
#include <utils.h>

typedef struct
{
    struct model super;
    int argc;
    double *argv;
} model_linear_t;

#define __MODEL_NAME__ "linear"

static void lincxmap_model_linear_free(model_t *self)
{
    assert(self && *self);

    model_linear_t *model = (model_linear_t*) *self;

    if (model->argv)
        free(model->argv);

    free(*self);
    *self = NULL;
}

static const char* lincxmap_model_linear_get_name(model_t *self)
{
    TRACE();

    assert(self && *self);

    return __MODEL_NAME__;
}

/**
 * f(x) = k * x + b
 * 
 *   k : argv[0]
 *   b : argv[1]
 */
static double lincxmap_model_linear_eval(model_t *self, double x)
{
    TRACE();

    assert(self && *self);

    model_linear_t *model = (model_linear_t*) *self;

    return (model->argc <= 0 ? x : model->argv[0] * x + model->argv[1]) * 100000;
}

model_t model_new(int argc, char *argv[])
{
    TRACE();
    DEBUG("argc=%d, argv=%p", argc, argv);

    assert(argc == 2);
    DEBUG("argv[0]=%s, argv[1]=%s", argv[0], argv[1]);

    const static struct model ks_model = {
        eval : lincxmap_model_linear_eval,
        free : lincxmap_model_linear_free,
        name : lincxmap_model_linear_get_name,
    };

    int i;
    model_linear_t *model = calloc(1, sizeof(model_linear_t));

    if (!model) {
        ERROR("Out of memory!\n");
        return NULL;
    }

    model->argc = argc;
    model->argv = calloc(argc, sizeof(double));

    if (!model->argv) {
        ERROR("Out of memory!\n");
        free(model);
        return NULL;
    }

    for (i = 0; i < argc; i++) {
        model->argv[i] = strtod(argv[i], NULL);
    }

    memcpy(&model->super, &ks_model, sizeof(struct model));

    return &model->super;
}

