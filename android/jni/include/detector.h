/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * detector.h
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#ifndef __LINCXMAP_DETECTOR_H__
#define __LINCXMAP_DETECTOR_H__

#include <stdarg.h>
#include <stdint.h>

#include <image.h>
#include <samples.h>
#include <selectors.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct __detector* detector_t;
struct __detector
{
    /**
     * Detect samples without selector(s).
     *
     * @param self
     *           {@link detector_t} object
     * @param image
     *           {@link image_t} object
     * @param selectors
     *           {@link selector_t} object array
     * @param n
     *           number of selectors
     */
    struct sample* (*detect)(detector_t *self, image_t *image, struct selectors *sa);

    /**
     * Free this object
     *
     * @param self
     *           {@link detector_t} object
     */
    void (*free)(detector_t *self);
};

extern detector_t detector_new();

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_DETECTOR_H__ */
