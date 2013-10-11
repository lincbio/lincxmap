/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
 *
 * bitmap.c
 *
 * @date    Feb 6, 2013
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>

#include <asm/byteorder.h>
#include <colorspace.h>
#include <log.h>
#include <bitmap.h>
#include <utils.h>

#include "histogram.c"

typedef struct
{
    struct image super;
    uint32_t size[2];
    uint32_t stride;
    uint32_t nchannels;
    image_type_t type;
    void *arg;
} bitmap_t;

extern int  lincxmap_native_bitmap_get_pixel(void*, uint32_t, uint32_t);
extern void lincxmap_native_bitmap_set_pixel(void*, uint32_t, uint32_t, uint32_t);
extern void lincxmap_native_bitmap_get_pixels(void*, uint8_t**, uint32_t, uint32_t, uint32_t, uint32_t, uint32_t);
extern void lincxmap_native_bitmap_set_pixels(void*, const uint8_t*, uint32_t, uint32_t, uint32_t, uint32_t, uint32_t);
static void lincxmap_bitmap_get_pixels(image_t*, uint8_t**, uint32_t, uint32_t, uint32_t, uint32_t, uint32_t);

static image_t lincxmap_bitmap_equalize(image_t *self, histogram_t *hist)
{
    TRACE();

    assert(self && *self);
    assert(hist && *hist);

    return *self;
}

static void lincxmap_bitmap_free(image_t *self)
{
    TRACE();

    assert(self && *self);

    free(*self);
    *self = NULL;
}

static histogram_t lincxmap_bitmap_get_histogram(image_t *self)
{
    TRACE();

    assert(self && *self);

    int i, j, k, x, y;
    uint8_t *px, *pixels;
    uint32_t width, height, size;
    bitmap_t *bmp = (bitmap_t*) *self;
    histogram_t hist = lincxmap_histogram_new(bmp->nchannels);

    width = bmp->size[0];
    height = bmp->size[1];
    size = width * height;
    pixels = calloc(width, sizeof(uint32_t));

    for (y = 0; y < height; y++) {
        lincxmap_bitmap_get_pixels(self, &pixels, width, 0, y, width, 1);

        for (x = 0; x < width; x++) {
            px = pixels + x * bmp->nchannels;

            for (i = 0; i < bmp->nchannels; i++) {
                hist->freq[i][px[i]]++;
                hist->min[i] = MIN(hist->min[i], px[i]);
                hist->max[i] = MAX(hist->max[i], px[i]);
            }
        }
    }

    free(pixels);

    for (i = 0; i < bmp->nchannels; i++) {
        for (j = 0; j < IMAGE_GRAY_SCALE; j++) {
            if (hist->freq[i][j] <= 0)
                continue;

            hist->avg[i] += hist->freq[i][j];
            hist->prob[i][j] = hist->freq[i][j] * 1.0 / size;
            hist->entropy[i] -= hist->prob[i][j] * log(hist->prob[i][j]);
        }

        for (j = 0; j < IMAGE_GRAY_SCALE; j++) {
            for (k = 0; k <= j; k++) {
                hist->cumu[i][j] += hist->prob[i][k];
            }
        }

        hist->avg[i] /= IMAGE_GRAY_SCALE;

        for (j = 0; j < IMAGE_GRAY_SCALE; j++) {
            hist->stddev[i] += pow(hist->freq[i][j] - hist->avg[i], 2);
        }

        hist->stddev[i] = sqrt(hist->stddev[i] / 256);

        DEBUG("The Minimum [%d] : %d\n", i, hist->min[i]);
        DEBUG("The Maximum [%d] : %d\n", i, hist->max[i]);
        DEBUG("The Entropy [%d] : %lf\n", i, hist->entropy[i]);
        DEBUG("The Standard Deviation [%d] : %lf\n", i, hist->stddev[i]);
    }

    return hist;
}

static uint32_t lincxmap_bitmap_get_n_channels(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((bitmap_t*) *self)->nchannels;
}

static image_t lincxmap_bitmap_get_channel(image_t *self, uint32_t nth)
{
    TRACE();

    assert(self && *self);

    int width, height;
    bitmap_t *src;

    src = (bitmap_t*) *self;
    assert(nth >= 0 && nth < src->nchannels);
    width = src->size[0];
    height = src->size[1];

    // TODO

    return bitmap_new(width, height, IMAGE_TYPE_GRAY, src->arg);
}

static uint32_t lincxmap_bitmap_get_height(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((bitmap_t*) *self)->size[1];
}

static uint32_t lincxmap_bitmap_get_pixel(image_t *self, uint32_t x, uint32_t y)
{
    TRACE();

    assert(self && *self);

    bitmap_t *bmp = (bitmap_t*) *self;

    return lincxmap_native_bitmap_get_pixel(bmp->arg, x, y);
}

static void lincxmap_bitmap_get_pixels(image_t *self, uint8_t **pixels, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h)
{
    TRACE();

    assert(self && *self);

    bitmap_t *bmp = (bitmap_t*) *self;

    lincxmap_native_bitmap_get_pixels(bmp->arg, pixels, stride, x, y, w, h);
}

static uint32_t lincxmap_bitmap_get_stride(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((bitmap_t*) *self)->stride;
}

static uint32_t lincxmap_bitmap_get_width(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((bitmap_t*) *self)->size[0];
}

static image_type_t lincxamp_bitmap_get_type(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((bitmap_t*) *self)->type;
}

static void lincxmap_bitmap_set_pixel(image_t *self, uint32_t x, uint32_t y, uint32_t color)
{
    TRACE();

    assert(self && *self);

    bitmap_t *bmp = (bitmap_t*) *self;

    lincxmap_native_bitmap_set_pixel(bmp->arg, x, y, color);
}

static void lincxmap_bitmap_set_pixels(image_t *self, const uint8_t *pixels, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h)
{
    TRACE();

    assert(self && *self);
    assert(pixels);
    assert(stride >= w);

    bitmap_t *bmp = (bitmap_t*) *self;

    lincxmap_native_bitmap_set_pixels(bmp->arg, pixels, stride, x, y, w, h);
}

static image_t lincxmap_bitmap_mean_smooth(image_t *self, va_list ap)
{
    TRACE();

    return *self;
}

static image_t lincxmap_bitmap_median_smooth(image_t *self, va_list ap)
{
    TRACE();

    return *self;
}

static image_t lincxmap_bitmap_gauss_smooth(image_t *self, va_list ap)
{
    TRACE();

    return *self;
}

static image_t lincxmap_bitmap_smooth(image_t *self, image_smooth_type_t type, ...)
{
    TRACE();

    assert(self && *self);

    image_t out;
    va_list ap;

    va_start(ap, type);

    switch (type) {
    case IMAGE_SMOOTH_TYPE_MEAN:
        out = lincxmap_bitmap_mean_smooth(self, ap);
        break;
    case IMAGE_SMOOTH_TYPE_MEDIAN:
        out = lincxmap_bitmap_median_smooth(self, ap);
        break;
    case IMAGE_SMOOTH_TYPE_GAUSS:
        out = lincxmap_bitmap_gauss_smooth(self, ap);
        break;
    default:
        out = NULL;
        break;
    }

    va_end(ap);

    return out;
}

static int lincxmap_bitmap_write(image_t *self, int fd, image_writer_t *writer)
{
    TRACE();

    assert(self && *self);
    assert(fd >= 0);
    assert(writer);

    return (*writer)(self, fd);
}

image_t bitmap_new(uint32_t w, uint32_t h, image_type_t type, void *arg)
{
    TRACE();

    const static struct image ks_image = {
        equalize     : lincxmap_bitmap_equalize,
        free         : lincxmap_bitmap_free,
        getchannel   : lincxmap_bitmap_get_channel,
        getheight    : lincxmap_bitmap_get_height,
        gethistogram : lincxmap_bitmap_get_histogram,
        getnchannels : lincxmap_bitmap_get_n_channels,
        getpixel     : lincxmap_bitmap_get_pixel,
        getpixels    : lincxmap_bitmap_get_pixels,
        getstride    : lincxmap_bitmap_get_stride,
        getwidth     : lincxmap_bitmap_get_width,
        gettype      : lincxamp_bitmap_get_type,
        setpixel     : lincxmap_bitmap_set_pixel,
        setpixels    : lincxmap_bitmap_set_pixels,
        smooth       : lincxmap_bitmap_smooth,
        write        : lincxmap_bitmap_write,
    };

    assert(w > 0);
    assert(h > 0);

    const uint32_t size[] = { w, h };
    bitmap_t *bmp = calloc(1, sizeof(bitmap_t));

    if (!bmp) {
        ERROR("Out of memory!\n");
        return NULL;
    }

    bmp->arg = arg;
    bmp->type = type;
    bmp->nchannels = image_get_nchannels(type);
    bmp->stride = w * bmp->nchannels;
    memcpy(&bmp->size, &size, sizeof(size));
    memcpy(&bmp->super, &ks_image, sizeof(struct image));

    return &bmp->super;
}

