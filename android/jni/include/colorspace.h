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

struct rgb
{
    uint8_t r;
    uint8_t g;
    uint8_t b;
};

struct argb
{
    uint8_t r;
    uint8_t g;
    uint8_t b;
    uint8_t a;
};

struct rgbx
{
    uint8_t r;
    uint8_t g;
    uint8_t b;
    uint8_t x;
};

struct hsl
{
    float h;
    float s;
    float l;
};

struct hsv
{
    float h;
    float s;
    float v;
};

struct cmyk
{
    float c;
    float m;
    float y;
    float k;
};

static inline uint32_t rgb2i(struct rgb *rgb)
{
    return ((rgb->r & 0xff) << 16) | ((rgb->g & 0xff) << 8) | (rgb->b & 0xff);
}

static inline uint32_t rgba2i(struct argb *rgba)
{
    return ((rgba->a & 0xff) << 24) | ((rgba->r & 0xff) << 16) | ((rgba->g & 0xff) << 8) | (rgba->b & 0xff);
}

static inline uint8_t i2gray(uint32_t i)
{
    return (((i >> 16) & 0xff) * 30 + ((i >> 8) & 0xff) * 59 + (i & 0xff) * 11) / 100;
}

static inline uint8_t rgb2gray(struct rgb *rgb)
{
    return (rgb->r * 30 + rgb->g * 59 + rgb->b * 11) / 100;
}

static inline struct rgb* i2rgb(uint32_t i, struct rgb *rgb)
{
    rgb->r = (i >> 16) & 0xff;
    rgb->g = (i >> 8) & 0xff;
    rgb->b = i & 0xff;

    return rgb;
}

static inline struct argb* i2rgba(uint32_t i, struct argb *rgba)
{
    rgba->a = (i >> 24) & 0xff;
    rgba->r = (i >> 16) & 0xff;
    rgba->g = (i >> 8) & 0xff;
    rgba->b = i & 0xff;

    return rgba;
}

static inline struct rgbx* i2rgbx(uint32_t i, struct rgbx *rgbx)
{
    rgbx->r = (i >> 16) & 0xff;
    rgbx->g = (i >> 8) & 0xff;
    rgbx->b = i & 0xff;
    rgbx->x = (rgbx->r * 30 + rgbx->g * 59 + rgbx->b * 11) / 100;

    return rgbx;
}

static inline struct hsl* rgb2hsl(struct rgb *rgb, struct hsl *hsl)
{
    register float r = rgb->r / 255.0f;
    register float g = rgb->g / 255.0f;
    register float b = rgb->b / 255.0f;
    register float max = MAX(MAX(r, g), b);
    register float min = MIN(MIN(r, g), b);
    register float delta = max - min;

    if (max == min) {
        hsl->h = 0;
        hsl->s = 0;
    } else if (max == r && g >= b) {
        hsl->h = 60 * (g - b) / delta;
    } else if (max == r && g < b) {
        hsl->h = 60 * (g - b) / delta + 360;
    } else if (max == g) {
        hsl->h = 60 * (b - r) / delta + 120;
    } else if (max == b) {
        hsl->h = 60 * (r - g) / delta + 240;
    }

    hsl->l = (max + min) / 2.0f;

    if (hsl->l == 0) {
        hsl->s = 0;
    } else if (hsl->l > 0 && hsl->l <= 0.5f) {
        hsl->s = delta / (2 * hsl->l);
    } else {
        hsl->s = delta / (2 - 2 * hsl->l);
    }

    return hsl;
}

static inline struct hsv* rgb2hsv(struct rgb *rgb, struct hsv *hsv)
{
    register float r = rgb->r / 255.0f;
    register float g = rgb->g / 255.0f;
    register float b = rgb->b / 255.0f;
    register float max = MAX(MAX(r, g), b);
    register float min = MIN(MIN(r, g), b);
    register float delta = max - min;

    if (max == min) {
        hsv->h = 0;
    } else if (max == r && g >= b) {
        hsv->h = 60 * (g - b) / delta;
    } else if (max == r && g < b) {
        hsv->h = 60 * (g - b) / delta + 360;
    } else if (max == g) {
        hsv->h = 60 * (b - r) / delta + 120;
    } else if (max == b) {
        hsv->h = 60 * (r - g) / delta + 240;
    }
    hsv->s = max == 0 ? 0 : 1 - min / max;
    hsv->v = max;

    return hsv;
}

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_COLORSPACE_H__ */

