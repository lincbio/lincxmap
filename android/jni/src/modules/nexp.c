/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * nexp.c
 *
 * @date    Apr 12, 2013
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
} model_nexp_t;

#define __MODEL_NAME__ "nexp"

static void lincxmap_model_nexp_free(model_t *self)
{
    TRACE();

	assert(self && *self);

	model_nexp_t *model = (model_nexp_t*) *self;

	if (model->argv)
		free(model->argv);

	free(*self);
	*self = NULL;
}

static const char* lincxmap_model_nexp_get_name(model_t *self)
{
	TRACE();

	assert(self && *self);

	return __MODEL_NAME__;
}

/**
 * f(x) = n * exp(x) + b
 * 
 *   n : argv[0]
 *   b : argv[1]
 */
static double lincxmap_model_nexp_eval(model_t *self, double x)
{
	TRACE();

	assert(self && *self);

	model_nexp_t *model = (model_nexp_t*) *self;

	return (model->argc <= 0 ? x : model->argv[0] * exp(x) + model->argv[1]) * 100000;
}

model_t model_new(int argc, char *argv[])
{
	TRACE();

	const static struct model ks_model = {
		eval : lincxmap_model_nexp_eval,
		free : lincxmap_model_nexp_free,
		name : lincxmap_model_nexp_get_name,
	};

	int i;
	model_nexp_t *model = calloc(1, sizeof(model_nexp_t));

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

