/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
 *
 * pixbuf.h
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

#ifndef __LINCXMAP_PIXBUF_H__
#define __LINCXMAP_PIXBUF_H__

#include "image.h"

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

extern image_t pixbuf_new(uint32_t w, uint32_t h, image_type_t type);

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* __LINCXMAP_PIXBUF_H__ */

