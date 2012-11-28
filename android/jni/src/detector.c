/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * detector.c
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <log.h>
#include <colorspace.h>
#include <detector.h>

typedef struct
{
	struct __detector super;
} detectorimpl_t;

static FILE* detector_open_test_image(int w, int h)
{
	FILE *file = fopen("/sdcard/DCIM/Camera/test.pgm", "wb");
	fprintf(file,"P5\n%u %u 255\n", w, h);

	return file;
}

static struct sample* detector_auto(detector_t *self, image_t image)
{
	// TODO detect automatically
	return NULL;
}

static struct sample* detector_manual(detector_t *self, image_t image,
		struct selectors *sa)
{
	int i, px, num;
	int w, h, x, y, x1, x2, y1, y2;
	float sum;
	char *buf;
	char *name;
	size_t len;
	hsl_t *hsl;
	rgb_t *rgb;
	selector_t selector;
	struct rectangle *bounds;
	struct sample *smpa, **smp = &smpa;
	struct selectors *sa0;

	w = image->getwidth(&image);
	h = image->getheight(&image);

#ifdef __DEBUG__
	unsigned char gray;
	FILE *fimg = detector_open_test_image(w, h);

	if (!fimg)
		goto skip_debug;

	for (y = 0; y < h; y++) {
		for (x = 0; x < w; x++) {
			for (i = 0, sa0 = sa; sa0; sa0 = sa0->next, i++) {
				selector = sa0->selector;

				if (selector->contains(&selector, x, y)) {
					gray = 0xff;
				} else {
					gray = i2gray(image->getpixel(&image, x, y));
				}

				fwrite(&gray, 1, 1, fimg);
			}
		}
	}

	fclose(fimg);

skip_debug:
#endif /* __DEBUG__ */

	// calculate valid boundary
	for (i = 0, sa0 = sa; sa0; sa0 = sa0->next, i++) {
		num = 0;
		sum = 0;
		selector = sa0->selector;
		bounds = selector->getbounds(&selector);
		x1 = MAX(0, bounds->x);
		y1 = MAX(0, bounds->y);
		x2 = MIN(w, bounds->x + bounds->width);
		y2 = MIN(h, bounds->y + bounds->height);

		*smp = calloc(1, sizeof(struct sample));
		if (!*smp)
			continue;

		for (y = y1; y < y2; y++) {
			for (x = x1; x < x2; x++) {
				if (!selector->contains(&selector, x, y))
					continue;

				num++;
				px = image->getpixel(&image, x, y);
				rgb = i2rgb(px);
				sum += rgb2hsl(rgb)->l;
			}
		}
		name = (char*) selector->getname(&selector);
		len = strlen(name);
		buf = calloc(len + 1, sizeof(char));
		if (buf) {
			(*smp)->name = memcpy(buf, name, len);
		}
		(*smp)->sum = num;
		(*smp)->bv = sum / num;
		smp = &(*smp)->next;
	}

	return smpa;
}

static struct sample* detector_detect(detector_t *self, image_t image,
		struct selectors *sa)
{
	assert(self && *self);
	assert(image);

	if (sa) {
		return detector_manual(self, image, sa);
	} else {
		return detector_auto(self, image);
	}
}

static void detector_free(detector_t *self)
{
	assert(self && *self);

	free(*self);
	*self = NULL;
}

const static struct __detector gs_detector = {
	.detect = detector_detect,
	.free   = detector_free,
};

extern detector_t detector_new()
{
	detectorimpl_t *impl = calloc(1, sizeof(detectorimpl_t));

	assert(impl);
	memcpy(&impl->super, &gs_detector, sizeof(struct __detector));

	return &impl->super;
}
