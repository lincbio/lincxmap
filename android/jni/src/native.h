/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * native.h
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#ifndef __LINCXMAP_NATIVE_H__
#define __LINCXMAP_NATIVE_H__

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

#define CLASS(clsname) #clsname

#define CLASS_OBJECT			CLASS(java/lang/Object)
#define CLASS_STRING			CLASS(java/lang/String)
#define CLASS_LIST				CLASS(java/util/List)
#define CLASS_ARRAY_LIST		CLASS(java/util/ArrayList)
#define CLASS_BITMAP            CLASS(android/graphics/Bitmap)
#define CLASS_PRODUCT			CLASS(com/lincbio/lincxmap/pojo/Product)
#define CLASS_SAMPLE			CLASS(com/lincbio/lincxmap/pojo/Sample)
#define CLASS_TEMPLATE          CLASS(com/lincbio/lincxmap/pojo/Template)
#define CLASS_RECTANGLE         CLASS(com/lincbio/lincxmap/dip/Rectangle)
#define CLASS_SAMPLE_DETECTOR   CLASS(com/lincbio/lincxmap/dip/SampleDetector)
#define CLASS_SAMPLE_SELECTOR   CLASS(com/lincbio/lincxmap/dip/SampleSelector)
#define CLASS_PROGRESS_LISTENER CLASS(com/lincbio/lincxmap/dip/SampleDetector$ProgressListener)

struct bmparg
{
	jobject *obj;
	JNIEnv *env;
} ;

JavaVM *jvm;

jclass cls_string;

jclass cls_array_list;

jclass cls_bitmap;

jclass cls_product;

jclass cls_sample;

jclass cls_template;

jclass cls_rectangle;

jclass cls_sample_detector;

jclass cls_sample_selector;

jclass cls_progress_listener;

/**
 * SampleDetector#prgListener
 */
jfieldID pro_sample_detector_prg_listener;

/**
 * ArrayList#ArrayList()
 */
jmethodID fun_array_list_new;

/**
 * List#add(Object)
 */
jmethodID fun_array_list_add;

/**
 * Bitmap#getWidth()
 */
jmethodID fun_bitmap_get_width;

/**
 * Bitmap#getHeight()
 */
jmethodID fun_bitmap_get_height;

/**
 * Bitmap#isMutable()
 */
jmethodID fun_bitmap_is_mutable;

/**
 * Bitmap#getPixel(int, int)
 */
jmethodID fun_bitmap_get_pixel;

/**
 * Bitmap#setPixel(int, int)
 */
jmethodID fun_bitmap_set_pixel;

/**
 * Rectangle#getX()
 */
jmethodID fun_rectangle_get_x;

/**
 * Rectangle#getY()
 */
jmethodID fun_rectangle_get_y;

/**
 * Rectangle#getWidth()
 */
jmethodID fun_rectangle_get_width;

/**
 * Rectangle#getHeight()
 */
jmethodID fun_rectangle_get_height;

/**
 * Product#getName()
 */
jmethodID fun_product_get_name;

/**
 * Sample#Sample()
 */
jmethodID fun_sample_new;

/**
 * Sample#setName(java.lang.String)
 */
jmethodID fun_sample_set_name;

/**
 * Sample#setSum(int)
 */
jmethodID fun_sample_set_sum;

/**
 * Sample#setBrightness(double)
 */
jmethodID fun_sample_set_brightness;

/**
 * Sample#setConcentration(double)
 */
jmethodID fun_sample_set_concentration;

/**
 * SampleSelector#getBounds()
 */
jmethodID fun_sample_selector_get_bounds;

/**
 * SampleSelector#getData()
 */
jmethodID fun_sample_selector_get_data;

/**
 * SampleSelector#getScaling()
 */
jmethodID fun_sample_selector_get_scaling;

/**
 * SampleSelector#getDeltaX()
 */
jmethodID fun_sample_selector_get_delta_x;

/**
 * SampleSelector#getDeltaY()
 */
jmethodID fun_sample_selector_get_delta_y;

/**
 * ProgressListener#onProgressChanged(int)
 */
jmethodID fun_progress_listener_on_progress_changed;

extern void native_setup(JavaVM*, void*);

extern void native_release(JavaVM*, void*);

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_NATIVE_H__ */
