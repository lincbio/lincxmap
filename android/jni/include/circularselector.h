/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * cselector.h
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#if _MSC_VER > 1000
#pragma once
#endif

#ifndef __LINCXMAP_CIRCULAR_SELECTOR_H__
#define __LINCXMAP_CIRCULAR_SELECTOR_H__

#include "selectors.h"

#ifdef __cplusplus
extern "C" {
#endif

extern selector_t circular_selector_new(float radius);

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_CIRCULAR_SELECTOR_H__ */
