/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
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

#define LINCXMAP_MODEL_FREE(M)                          \
    void lincxmap_model_##M##_free(model_t *self)       \
    {                                                   \
        assert(self && *self);                          \
        model_##M##_t *model = (model_##M##_t*) *self;  \
                                                        \
        if (model->argv)                                \
            free(model->argv);                          \
                                                        \
        free(*self);                                    \
        *self = NULL;                                   \
    }

#define LINCXMAP_MODEL_GET_NAME(M)                      \
    void lincxmap_model_##M##_get_name(model_t *self)   \
    {                                                   \
        assert(self && *self);                          \
        model_##M##_t *model = (model_##M##_t*) *self;  \
                                                        \
        return __MODEL_NAME__;                          \
    }

#ifdef __cplusplus
extern "C" {
#endif

typedef struct model* model_t;
struct model
{
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

    /**
     * Free this object
     */
    void (*free)(model_t *self);

    /**
     * Returns the model's name.
     * 
     * @param self
     *           {@link model_t} object
     * @return the model name
     */
    const char* (*name)(model_t *self);
};

extern model_t model_new(int argc, char *argv[]);

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_MODEL_H__ */
