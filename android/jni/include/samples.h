/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
 *
 * samples.h
 *
 * @date    Sep 09, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#ifndef __LINCXMAP_SAMPLES_H__
#define __LINCXMAP_SAMPLES_H__

#ifdef __cplusplus
extern "C" {
#endif

struct sample
{
    char name[256];    /* sample name */
    double bv;        /* brigtness value */
    double cv;        /* concentration value */
    int sum;        /* sum of pixels */

    struct sample *next;
};

extern void samples_add(struct sample **smpa, struct sample *smp);

extern void samples_free(struct sample **smpa);

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_SAMPLES_H__ */

