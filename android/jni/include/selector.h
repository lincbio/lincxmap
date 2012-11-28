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

#ifndef __LINCXMAP_SELECTOR_H__
#define __LINCXMAP_SELECTOR_H__

#include <stdint.h>

#include "rectangle.h"

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
	float             (*getscaling)(selector_t *self);
	void              (*setscaling)(selector_t *self, float scaling);
	int               (*getdeltax)(selector_t *self);
	void              (*setdeltax)(selector_t *self, int dx);
	int               (*getdeltay)(selector_t *self);
	void              (*setdeltay)(selector_t *self, int dy);
	int               (*contains)(selector_t *self, int x, int y);
	void              (*free)(selector_t *self);
};

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_SELECTOR_H__ */
