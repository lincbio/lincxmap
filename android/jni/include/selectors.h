/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * selectors.h
 *
 * @date    Sep 09, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#ifndef __LINCXMAP_SELECTORS_H__
#define __LINCXMAP_SELECTORS_H__

#include "selector.h"

#ifdef __cplusplus
extern "C" {
#endif

struct selectors
{
	selector_t selector;
	struct selectors *next;
};

extern void selectors_free(struct selectors **sa);

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_SELECTORS_H__ */
