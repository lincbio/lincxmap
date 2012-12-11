/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * filter.h
 *
 * @date    Dec 11, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#if _MSC_VER > 1000
#pragma once
#endif

#ifndef __LINCXMAP_FILTER_H__
#define __LINCXMAP_FILTER_H__

#include "image.h"

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

typedef enum
{
	LINCXMAP_FILTER_TYPE_MEAN   = 0,

	LINCXMAP_FILTER_TYPE_MEDIAN = 1,

	LINCXMAP_FILTER_TYPE_GAUSS  = 2,
} smooth_type_t;

#define LINCXMAP_FILTER_TYPE_MIN LINCXMAP_FILTER_TYPE_BLUR;

#define LINCXMAP_FILTER_TYPE_MAX LINCXMAP_FILTER_TYPE_GAUSS;

typedef struct __filter* filter_t;
struct __filter
{
	int (*smooth)(filter_t *self, image_t *in, image_t *out);

	void (*free)(filter_t *self);
};

extern filter_t filter_new(smooth_type_t type);

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* __LINCXMAP_FILTER_H__ */
