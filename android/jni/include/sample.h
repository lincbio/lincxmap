/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * sample.h
 *
 * @date    Sep 09, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#ifndef __LINCXMAP_SAMPLE_H__
#define __LINCXMAP_SAMPLE_H__

#ifdef __cplusplus
extern "C" {
#endif

struct sample
{
	char name[256];
	double bv; /* brigtness value */
	double cv; /* concentration value */
	int sum;   /* sum of pixels */

	struct sample *next;
};

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_SAMPLE_H__ */
