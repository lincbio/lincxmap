/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
 *
 * pixbuf.c
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <math.h>
#include <stdarg.h>
#include <stdlib.h>
#include <string.h>

#include <colorspace.h>
#include <log.h>
#include <pixbuf.h>
#include <utils.h>

#include "histogram.c"

struct _pixbuf
{
    struct image super;
    uint8_t *data;
    uint32_t size[2];
    uint32_t stride;
    uint32_t nchannels;
    image_type_t type;
};

static image_t lincxmap_pixbuf_equalize(image_t *self, histogram_t *hist)
{
    TRACE();

    assert(self && *self);
    assert(hist && *hist);

    return *self;
}

static void lincxmap_pixbuf_free(image_t *self)
{
    TRACE();

    assert(self && *self);

    struct _pixbuf *pbf = (struct _pixbuf*) *self;

    if (pbf->data) {
        free(pbf->data);
    }

    free(*self);
    *self = NULL;
}

static histogram_t lincxmap_pixbuf_get_histogram(image_t *self)
{
    TRACE();

    assert(self && *self);

    int i, j, k, x, y;
    uint8_t *px;
    uint32_t width, height, stride, size;
    struct _pixbuf *pbf = (struct _pixbuf*) *self;
    histogram_t hist = lincxmap_histogram_new(pbf->nchannels);

    width = pbf->size[0];
    height = pbf->size[1];
    stride = pbf->stride;
    size = width * height;

    for (y = 0; y < height; y++) {
        for (x = 0; x < width; x++) {
            px = pbf->data + y * stride + x * pbf->nchannels;

            for (i = 0; i < pbf->nchannels; i++) {
                hist->freq[i][px[i]]++;
                hist->min[i] = MIN(hist->min[i], px[i]);
                hist->max[i] = MAX(hist->max[i], px[i]);
            }
        }
    }

    for (i = 0; i < pbf->nchannels; i++) {
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

static uint32_t lincxmap_pixbuf_get_n_channels(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((struct _pixbuf*) *self)->nchannels;
}

static image_t lincxmap_pixbuf_get_channel(image_t *self, uint32_t nth)
{
    TRACE();

    assert(self && *self);

    int x, y;
    uint8_t *p;
    image_t img;
    struct _pixbuf *src, *pbf;
    
    src = (struct _pixbuf*) *self;
    assert(nth >= 0 && nth < src->nchannels);
    img = pixbuf_new(src->size[0], src->size[1], IMAGE_TYPE_GRAY);
    pbf = (struct _pixbuf*) img;

    for (y = 0; y < src->size[1]; y++) {
        for (x = 0; x < src->size[0]; x++) {
            p = src->data + y * src->stride + x * src->nchannels;
            pbf->data[y * src->size[0] + x] = p[nth];
        }
    }

    return img;
}

static uint32_t lincxmap_pixbuf_get_height(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((struct _pixbuf*) *self)->size[1];
}

static uint32_t lincxmap_pixbuf_get_pixel(image_t *self, uint32_t x, uint32_t y)
{
    TRACE();

    assert(self && *self);

    struct _pixbuf *pbf = (struct _pixbuf*) *self;
    uint8_t *data = pbf->data + y * pbf->stride + x * pbf->nchannels;
    uint32_t color = 0;

    memcpy(&color, data, pbf->nchannels);

    return color;
}

static void lincxmap_pixbuf_get_pixels(image_t *self, uint8_t **pixels, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h)
{
    TRACE();

    assert(self && *self);

    struct _pixbuf *pbf = (struct _pixbuf*) *self;
    uint8_t *p = pbf->data + y * stride + x * pbf->nchannels;

    assert(w > 0 && w <= pbf->size[0] && h > 0 && h <= pbf->size[1]);

    if (!*pixels) {
        *pixels = p;
    } else {
        memcpy(*pixels, p, w * h - y * stride - x * pbf->nchannels);
    }
}

static uint32_t lincxmap_pixbuf_get_stride(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((struct _pixbuf*) *self)->stride;
}

static uint32_t lincxmap_pixbuf_get_width(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((struct _pixbuf*) *self)->size[0];
}

static image_type_t lincxamp_pixbuf_get_type(image_t *self)
{
    TRACE();

    assert(self && *self);

    return ((struct _pixbuf*) *self)->type;
}

static void lincxmap_pixbuf_set_pixel(image_t *self, uint32_t x, uint32_t y, uint32_t color)
{
    TRACE();

    assert(self && *self);

    struct _pixbuf *pbf = (struct _pixbuf*) *self;
    uint8_t *data = pbf->data + y * pbf->stride + x * pbf->nchannels;

    memcpy(data, &color, pbf->nchannels);
}

static void lincxmap_pixbuf_set_pixels(image_t *self, const uint8_t *pixels, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h)
{
    TRACE();

    assert(self && *self);
    assert(pixels);
    assert(stride >= w);

    struct _pixbuf *pbf = (struct _pixbuf*) *self;
    int offset = y * stride + x * pbf->nchannels;
    memcpy(pbf->data + offset, pixels, w * h - offset);
}

static image_t lincxmap_pixbuf_mean_smooth(image_t *self, va_list ap)
{
    TRACE();

    return *self;
}

static image_t lincxmap_pixbuf_median_smooth(image_t *self, va_list ap)
{
    TRACE();

    return *self;
}

static image_t lincxmap_pixbuf_gauss_smooth(image_t *self, va_list ap)
{
    TRACE();

    return *self;
}

static image_t lincxmap_pixbuf_smooth(image_t *self, image_smooth_type_t type, ...)
{
    TRACE();

    assert(self && *self);

    image_t out;
    va_list ap;

    va_start(ap, type);

    switch (type) {
    case IMAGE_SMOOTH_TYPE_MEAN:
        out = lincxmap_pixbuf_mean_smooth(self, ap);
        break;
    case IMAGE_SMOOTH_TYPE_MEDIAN:
        out = lincxmap_pixbuf_median_smooth(self, ap);
        break;
    case IMAGE_SMOOTH_TYPE_GAUSS:
        out = lincxmap_pixbuf_gauss_smooth(self, ap);
        break;
    default:
        out = NULL;
        break;
    }

    va_end(ap);

    return out;
}

static int lincxmap_pixbuf_write(image_t *self, int fd, image_writer_t *writer)
{
    TRACE();

    assert(self && *self);
    assert(fd >= 0);
    assert(writer);

    return (*writer)(self, fd);
}

static const struct image clazz = {
    equalize     : lincxmap_pixbuf_equalize,
    free         : lincxmap_pixbuf_free,
    getchannel   : lincxmap_pixbuf_get_channel,
    getheight    : lincxmap_pixbuf_get_height,
    gethistogram : lincxmap_pixbuf_get_histogram,
    getnchannels : lincxmap_pixbuf_get_n_channels,
    getpixel     : lincxmap_pixbuf_get_pixel,
    getpixels    : lincxmap_pixbuf_get_pixels,
    getstride    : lincxmap_pixbuf_get_stride,
    getwidth     : lincxmap_pixbuf_get_width,
    gettype      : lincxamp_pixbuf_get_type,
    setpixel     : lincxmap_pixbuf_set_pixel,
    setpixels    : lincxmap_pixbuf_set_pixels,
    smooth       : lincxmap_pixbuf_smooth,
    write        : lincxmap_pixbuf_write,
};

image_t pixbuf_new(uint32_t w, uint32_t h, image_type_t type)
{
    TRACE();

    assert(w > 0);
    assert(h > 0);

    const uint32_t size[] = { w, h };
    struct _pixbuf *pbf = calloc(1, sizeof(struct _pixbuf));

    if (!pbf) {
        ERROR("Out of memory!\n");
        return NULL;
    }

    pbf->type = type;
    pbf->nchannels = image_get_nchannels(type);
    pbf->stride = w * pbf->nchannels;
    pbf->data = calloc(w * h * pbf->nchannels, sizeof(uint8_t));

    if (!pbf->data) {
        ERROR("Out of memory!\n");
        free(pbf);
        return NULL;
    }

    memcpy(&pbf->size, &size, sizeof(size));
    memcpy(&pbf->super, &clazz, sizeof(struct image));

    return &pbf->super;
}

