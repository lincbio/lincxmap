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

#ifndef __LINCXMAP_COLORSPACE_H__
#define __LINCXMAP_COLORSPACE_H__

#include <stdint.h>

#include "utils.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct
{
	uint8_t r;
	uint8_t g;
	uint8_t b;
} rgb_t;

typedef struct
{
	uint8_t r;
	uint8_t g;
	uint8_t b;
	uint8_t alpha;
} rgba_t;

typedef struct
{
	float h;
	float s;
	float l;
} hsl_t;

typedef struct
{
	float h;
	float s;
	float v;
} hsv_t;

typedef struct
{
	float c;
	float m;
	float y;
	float k;
} cmyk_t;

static inline uint32_t rgb2i(rgb_t *rgb)
{
	return ((rgb->r & 0xff) << 16) | ((rgb->g & 0xff) << 8) | (rgb->b & 0xff);
}

static inline uint8_t i2gray(uint32_t i)
{
	return (((i >> 16) & 0xff) * 30 + ((i >> 8) & 0xff) * 59 + (i & 0xff) * 11) / 100;
}

static inline uint8_t rgb2gray(rgb_t *rgb)
{
	return (rgb->r * 30 + rgb->g * 59 + rgb->b * 11) / 100;
}

static inline rgb_t* i2rgb(uint32_t i)
{
	static rgb_t rgb;

	rgb.r = (i >> 16) & 0xff;
	rgb.g = (i >> 8) & 0xff;
	rgb.b = i & 0xff;

	return &rgb;
}

static inline hsl_t* rgb2hsl(rgb_t *rgb)
{
	static hsl_t hsl;

	register float r = rgb->r / 255.0f;
	register float g = rgb->g / 255.0f;
	register float b = rgb->b / 255.0f;
	register float max = MAX(MAX(r, g), b);
	register float min = MIN(MIN(r, g), b);
	register float delta = max - min;

	if (max == min) {
		hsl.h = 0;
		hsl.s = 0;
	} else if (max == r && g >= b) {
		hsl.h = 60 * (g - b) / delta;
	} else if (max == r && g < b) {
		hsl.h = 60 * (g - b) / delta + 360;
	} else if (max == g) {
		hsl.h = 60 * (b - r) / delta + 120;
	} else if (max == b) {
		hsl.h = 60 * (r - g) / delta + 240;
	}

	hsl.l = (max + min) / 2.0f;

	if (hsl.l == 0) {
		hsl.s = 0;
	} else if (hsl.l > 0 && hsl.l <= 0.5f) {
		hsl.s = delta / (2 * hsl.l);
	} else {
		hsl.s = delta / (2 - 2 * hsl.l);
	}

	return &hsl;
}

static inline hsv_t* rgb2hsv(rgb_t *rgb)
{
	static hsv_t hsv;

	register float r = rgb->r / 255.0f;
	register float g = rgb->g / 255.0f;
	register float b = rgb->b / 255.0f;
	register float max = MAX(MAX(r, g), b);
	register float min = MIN(MIN(r, g), b);
	register float delta = max - min;

	if (max == min) {
		hsv.h = 0;
	} else if (max == r && g >= b) {
		hsv.h = 60 * (g - b) / delta;
	} else if (max == r && g < b) {
		hsv.h = 60 * (g - b) / delta + 360;
	} else if (max == g) {
		hsv.h = 60 * (b - r) / delta + 120;
	} else if (max == b) {
		hsv.h = 60 * (r - g) / delta + 240;
	}
	hsv.s = max == 0 ? 0 : 1 - min / max;
	hsv.v = max;

	return &hsv;
}

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_COLORSPACE_H__ */
