/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * bitmap.h
 *
 * @date    Feb 6, 2013
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#if _MSC_VER > 1000
#pragma once
#endif

#ifndef __LINCXMAP_BITMAP_H__
#define __LINCXMAP_BITMAP_H__

#include "image.h"

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

extern image_t bitmap_new(uint32_t w, uint32_t h, image_type_t type, void *arg);

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* __LINCXMAP_BITMAP_H__ */

