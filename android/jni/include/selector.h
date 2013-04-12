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
#include <stdlib.h>

#include "rectangle.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct selector* selector_t;
struct selector
{
	int (*contains)(selector_t *self, uint32_t x, uint32_t y);

	void (*free)(selector_t *self);

	struct rectangle* (*getbounds)(selector_t *self);

    const char* (*getmodel)(selector_t *self, int *argcp, char ***argvp);

	const char* (*getname)(selector_t *self);

	void (*setbounds)(selector_t *self, struct rectangle *bounds);

    void (*setmodel)(selector_t *self, const char *model, int argc, char **argv);

	void (*setname)(selector_t *self, const char *name);
};

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_SELECTOR_H__ */

