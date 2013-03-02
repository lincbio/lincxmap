/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * model.h
 *
 * @date    Mar 02, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#ifndef __LINCXMAP_MODEL_H__
#define __LINCXMAP_MODEL_H__

#include <stdarg.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct model* model_t;
struct model
{
	/**
	 * Returns the model's name.
	 * 
	 * @param self
	 *           {@link model_t} object
	 * @return the model name
	 */
	const char* (*name)(model_t *self);

    /**
	 * Evaluate with the specified arguments
	 * 
	 * @param self
	 *           {@link model_t} object
	 * @param x
	 *           the value to be evaluated
	 * @return the value that evaluated by this model
	 */
    double (*eval)(model_t *self, double x);

};

extern model_t model_new(int argc, char *argv[]);

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_MODEL_H__ */
