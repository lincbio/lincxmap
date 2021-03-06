/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
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

#define CLASS_OBJECT                CLASS(java/lang/Object)
#define CLASS_STRING                CLASS(java/lang/String)
#define CLASS_LIST                  CLASS(java/util/List)
#define CLASS_ARRAY_LIST            CLASS(java/util/ArrayList)
#define CLASS_BITMAP                CLASS(android/graphics/Bitmap)
#define CLASS_DATABASE_HELPER       CLASS(com/lincbio/lincxmap/android/database/DatabaseHelper)
#define CLASS_PRODUCT               CLASS(com/lincbio/lincxmap/pojo/Product)
#define CLASS_PRODUCT_ARGUMENT      CLASS(com/lincbio/lincxmap/pojo/ProductArgument)
#define CLASS_SAMPLE                CLASS(com/lincbio/lincxmap/pojo/Sample)
#define CLASS_TEMPLATE              CLASS(com/lincbio/lincxmap/pojo/Template)
#define CLASS_SHAPE                 CLASS(com/lincbio/lincxmap/geom/Shape)
#define CLASS_SAMPLE_DETECTOR       CLASS(com/lincbio/lincxmap/dip/SampleDetector)
#define CLASS_SAMPLE_SELECTOR       CLASS(com/lincbio/lincxmap/dip/SampleSelector)

#if 0
jclass cls_string;

jclass cls_array_list;

jclass cls_bitmap;

jclass cls_database_helper;

jclass cls_product;

jclass cls_product_argument;

jclass cls_sample;

jclass cls_shape;

jclass cls_sample_detector;

jclass cls_sample_selector;

jclass cls_template;
/**
 * SampleDetector#dbhelper
 */
jfieldID pro_sample_detector_dbhelper;

/**
 * ArrayList#ArrayList()
 */
jmethodID fun_array_list_new;

/**
 * ArrayList#add(Object)
 */
jmethodID fun_array_list_add;

/**
 * ArrayList#get(int)
 */
jmethodID fun_array_list_get;

/**
 * ArrayList#size()
 */
jmethodID fun_array_list_size;

/**
 * Bitmap#getWidth()
 */
jmethodID fun_bitmap_get_width;

/**
 * Bitmap#getHeight()
 */
jmethodID fun_bitmap_get_height;

/**
 * Bitmap#getPixel(int, int)
 */
jmethodID fun_bitmap_get_pixel;

/**
 * Bitmap#setPixel(int, int, int)
 */
jmethodID fun_bitmap_set_pixel;

/**
 * Bitmap#getPixels(int[], int, int, int, int, int, int)
 */
jmethodID fun_bitmap_get_pixels;

/**
 * Bitmap#setPixels(int[], int, int, int, int, int, int)
 */
jmethodID fun_bitmap_set_pixels;

/**
 * Bitmap#isMutable()
 */
jmethodID fun_bitmap_is_mutable;

/**
 * DatabaseHelper#getProductArguments(long)
 */
jmethodID fun_database_helper_get_product_arguments;

/**
 * Shape#getX()
 */
jmethodID fun_shape_get_x;

/**
 * Shape#getY()
 */
jmethodID fun_shape_get_y;

/**
 * Shape#getWidth()
 */
jmethodID fun_shape_get_width;

/**
 * Shape#getHeight()
 */
jmethodID fun_shape_get_height;

/**
 * Product#getId()
 */
jmethodID fun_product_get_id;

/**
 * Product#getName()
 */
jmethodID fun_product_get_name;

/**
 * Product#getModel()
 */
jmethodID fun_product_get_model;

/**
 * ProductArgument#getValue()
 */
jmethodID fun_product_argument_get_value;

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
 * SampleSelector#getShape()
 */
jmethodID fun_sample_selector_get_shape;

/**
 * SampleSelector#getProduct()
 */
jmethodID fun_sample_selector_get_product;

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

#endif

#ifdef __cplusplus
}
#endif

#endif /* __LINCXMAP_NATIVE_H__ */
