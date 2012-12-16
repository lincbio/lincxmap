/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * image.h
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#ifndef __LINCXMAP_IMAGE_H__
#define __LINCXMAP_IMAGE_H__

#include <stdarg.h>
#include <stdint.h>

#include "histogram.h"

#ifdef __cplusplus
extern "C" {
#endif

#define IMAGE_GRAY_SCALE 256

typedef enum
{
	IMAGE_TYPE_GRAY,

	IMAGE_TYPE_RGB,
	IMAGE_TYPE_RBG,
	IMAGE_TYPE_GRB,
	IMAGE_TYPE_GBR,
	IMAGE_TYPE_BGR,
	IMAGE_TYPE_BRG,

	IMAGE_TYPE_ARBG,
	IMAGE_TYPE_ARGB,
	IMAGE_TYPE_AGBR,
	IMAGE_TYPE_AGRB,
	IMAGE_TYPE_ABGR,
	IMAGE_TYPE_ABRG,

	IMAGE_TYPE_RABG,
	IMAGE_TYPE_RAGB,
	IMAGE_TYPE_RBAG,
	IMAGE_TYPE_RBGA,
	IMAGE_TYPE_RGAB,
	IMAGE_TYPE_RGBA,

	IMAGE_TYPE_GABR,
	IMAGE_TYPE_GARB,
	IMAGE_TYPE_GBAR,
	IMAGE_TYPE_GBRA,
	IMAGE_TYPE_GRAB,
	IMAGE_TYPE_GRBA,

	IMAGE_TYPE_BAGR,
	IMAGE_TYPE_BARG,
	IMAGE_TYPE_BGAR,
	IMAGE_TYPE_BGRA,
	IMAGE_TYPE_BRAG,
	IMAGE_TYPE_BRGA,
} image_type_t;

typedef enum
{
	IMAGE_SMOOTH_TYPE_MEAN,

	IMAGE_SMOOTH_TYPE_MEDIAN,

	IMAGE_SMOOTH_TYPE_GAUSS,
} image_smooth_type_t;

typedef struct image* image_t;

typedef int (*image_writer_t)(image_t *image, int fd);

struct image
{
	image_t (*equalize)(image_t *self, histogram_t *hist);

	void (*free)(image_t *self);

	image_t (*getchannel)(image_t *self, uint32_t nth);

	uint32_t (*getheight)(image_t *self);

	histogram_t (*gethistogram)(image_t *self);

	uint32_t (*getnchannels)(image_t *self);

	uint32_t  (*getpixel)(image_t *self, uint32_t x, uint32_t y);

	uint8_t* (*getpixels)(image_t *self);

	uint32_t (*getstride)(image_t *self);

	uint32_t (*getwidth)(image_t *self);

	image_type_t (*gettype)(image_t *self);

	void (*setpixel)(image_t *self, uint32_t x, uint32_t y, uint32_t color);

	void (*setpixels)(image_t *self, const uint8_t *pixels, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h);

	image_t (*smooth)(image_t *self, image_smooth_type_t type, ...);

	int (*write)(image_t *self, int fd, image_writer_t *writer);
};

static inline int image_get_nchannels(image_type_t type)
{
	switch (type) {
	case IMAGE_TYPE_GRAY:
		return 1;
	case IMAGE_TYPE_RGB:
	case IMAGE_TYPE_RBG:
	case IMAGE_TYPE_GRB:
	case IMAGE_TYPE_GBR:
	case IMAGE_TYPE_BGR:
	case IMAGE_TYPE_BRG:
		return 3;
	case IMAGE_TYPE_ARBG:
	case IMAGE_TYPE_ARGB:
	case IMAGE_TYPE_AGBR:
	case IMAGE_TYPE_AGRB:
	case IMAGE_TYPE_ABGR:
	case IMAGE_TYPE_ABRG:
	case IMAGE_TYPE_RABG:
	case IMAGE_TYPE_RAGB:
	case IMAGE_TYPE_RBAG:
	case IMAGE_TYPE_RBGA:
	case IMAGE_TYPE_RGAB:
	case IMAGE_TYPE_RGBA:
	case IMAGE_TYPE_GABR:
	case IMAGE_TYPE_GARB:
	case IMAGE_TYPE_GBAR:
	case IMAGE_TYPE_GBRA:
	case IMAGE_TYPE_GRAB:
	case IMAGE_TYPE_GRBA:
	case IMAGE_TYPE_BAGR:
	case IMAGE_TYPE_BARG:
	case IMAGE_TYPE_BGAR:
	case IMAGE_TYPE_BGRA:
	case IMAGE_TYPE_BRAG:
	case IMAGE_TYPE_BRGA:
	default:
		return 4;
	}
}

/**
 * Return the offset of each component of ARGB
 */
static inline int image_get_component_offset(image_type_t type)
{
	switch (type) {
	case IMAGE_TYPE_RGB:
		return 0x00020100;
	case IMAGE_TYPE_RBG:
		return 0x00020001;
	case IMAGE_TYPE_GRB:
		return 0x00010200;
	case IMAGE_TYPE_GBR:
		return 0x00000201;
	case IMAGE_TYPE_BGR:
		return 0x00000102;
	case IMAGE_TYPE_BRG:
		return 0x00010002;
	case IMAGE_TYPE_ARBG:
		return 0x03020001;
	case IMAGE_TYPE_ARGB:
		return 0x03020100;
	case IMAGE_TYPE_AGBR:
		return 0x03000201;
	case IMAGE_TYPE_AGRB:
		return 0x03010200;
	case IMAGE_TYPE_ABGR:
		return 0x03000102;
	case IMAGE_TYPE_ABRG:
		return 0x03010002;
	case IMAGE_TYPE_RABG:
		return 0x02030001;
	case IMAGE_TYPE_RAGB:
		return 0x02030100;
	case IMAGE_TYPE_RBAG:
		return 0x01030002;
	case IMAGE_TYPE_RBGA:
		return 0x00030102;
	case IMAGE_TYPE_RGAB:
		return 0x01030200;
	case IMAGE_TYPE_RGBA:
		return 0x00030201;
	case IMAGE_TYPE_GABR:
		return 0x02000301;
	case IMAGE_TYPE_GARB:
		return 0x02010300;
	case IMAGE_TYPE_GBAR:
		return 0x01000302;
	case IMAGE_TYPE_GBRA:
		return 0x00010302;
	case IMAGE_TYPE_GRAB:
		return 0x01020300;
	case IMAGE_TYPE_GRBA:
		return 0x00020301;
	case IMAGE_TYPE_BAGR:
		return 0x02000103;
	case IMAGE_TYPE_BARG:
		return 0x02010003;
	case IMAGE_TYPE_BGAR:
		return 0x01000203;
	case IMAGE_TYPE_BGRA:
		return 0x00010203;
	case IMAGE_TYPE_BRAG:
		return 0x01020003;
	case IMAGE_TYPE_BRGA:
		return 0x00020103;
	default:
		return 0x00000000;
	}
}

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_IMAGE_H__ */
