/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
 *
 * histogram.h
 *
 * @date    Dec 13, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#if _MSC_VER > 1000
#pragma once
#endif

#ifndef __LINCXMAP_HISTOGRAM_H__
#define __LINCXMAP_HISTOGRAM_H__

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

/**
 * The dimension of a histogram equals the number of channel
 */
typedef struct histogram* histogram_t;
struct histogram
{
    const uint32_t nchannels;

    /**
     * The FREQUENCY distribution
     */
    int (*freq)[256];

    /**
     * The PROBABILITY distribution
     */
    double (*prob)[256];

    /**
     * The CUMULATIVE distribution
     */
    double (*cumu)[256];

    /**
     * The AVERAGE DEVIATION distribution of each channel
     */
    double *avg;

    /**
     * The STANDARD DEVIATION distribution of each channel
     */
    double *stddev;

    /**
     * The ENTROPY of each channel
     */
    double *entropy;

    /**
     * Max pixel value of each channel
     */
    uint8_t *max;

    /**
     * Min pixel value of each channel
     */
    uint8_t *min;

    void (*free)(histogram_t *self);
};

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* __LINCXMAP_HISTOGRAM_H__ */
