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

#if _MSC_VER > 1000
#pragma once
#endif

#ifndef __LINCXMAP_SELECTORS_H__
#define __LINCXMAP_SELECTORS_H__

#include <stdlib.h>

#include <rectangle.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct __selector* selector_t;
struct __selector
{
	const char*       (*getname)(selector_t *self);
	void              (*setname)(selector_t *self, const char *name, size_t len);
	struct rectangle* (*getbounds)(selector_t *self);
	void              (*setbounds)(selector_t *self, struct rectangle *bounds);
	int               (*contains)(selector_t *self, int x, int y);
	void              (*free)(selector_t *self);
};

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
