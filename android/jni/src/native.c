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

#include <assert.h>
#include <stdlib.h>
#include <stdio.h>

#include <circularselector.h>
#include <detector.h>
#include <log.h>
#include <image.h>
#include <bitmap.h>
#include <samples.h>
#include <selectors.h>

#include "native.h"

#define SIG_DETECT "(L"CLASS_BITMAP";L"CLASS_TEMPLATE";[L"CLASS_SAMPLE_SELECTOR";)L"CLASS_LIST";"

const static jint k_jniver = JNI_VERSION_1_4;

struct native_obj
{
	JNIEnv *env;
	jobject obj;
};

JNIEXPORT jobject JNICALL native_detect(JNIEnv *env, jobject jself,
		jobject jbmp, jobject jtpl, jobjectArray jsela)
{
	TRACE();

	int i, j, argc;
	char *name, *model, *val, **argv;
	float dx, dy, scaling;
	double x, y, w, h;
	struct rectangle rect;
	struct sample *smp, *smpa;
	struct selectors *sela, **sel = &sela;
	struct native_obj arg = { env, jbmp };
	image_t img;
	detector_t detector;
	jint jwidth, jheight;
    jlong jid;
	jsize nselectors;
	jobject jbounds, jdbhelper, jproduct, jlist, jsel, jsmp, jname, jmodel, jpalist, jpa, jval;

	nselectors = (*env)->GetArrayLength(env, jsela);
    jdbhelper = (*env)->GetObjectField(env, jself, pro_sample_detector_dbhelper);
	jwidth = (*env)->CallIntMethod(env, jbmp, fun_bitmap_get_width);
	jheight = (*env)->CallIntMethod(env, jbmp, fun_bitmap_get_height);
    DEBUG("cls_array_list=%p, fun_array_list_new=%p", cls_array_list, fun_array_list_new);
	jlist = (*env)->NewObject(env, cls_array_list, fun_array_list_new);

	// initialize selectors
    TRACE();
	for (i = 0; i < nselectors; i++) {
    TRACE();
		jsel = (*env)->GetObjectArrayElement(env, jsela, i);
    TRACE();
		jproduct = (*env)->CallObjectMethod(env, jsel, fun_sample_selector_get_product);
    TRACE();
        jmodel = (*env)->CallObjectMethod(env, jproduct, fun_product_get_model);
    TRACE();
        model = (char*) (*env)->GetStringUTFChars(env, jmodel, JNI_FALSE);
    TRACE();
        jname = (*env)->CallObjectMethod(env, jproduct, fun_product_get_name);
    TRACE();
        name = (char*) (*env)->GetStringUTFChars(env, jname, JNI_FALSE);
    TRACE();
		dx = (*env)->CallFloatMethod(env, jsel, fun_sample_selector_get_delta_x);
    TRACE();
		dy = (*env)->CallFloatMethod(env, jsel, fun_sample_selector_get_delta_y);
    TRACE();
		scaling = (*env)->CallFloatMethod(env, jsel, fun_sample_selector_get_scaling);
    TRACE();
		jbounds = (*env)->CallObjectMethod(env, jsel, fun_sample_selector_get_bounds);
    TRACE();
		x = (*env)->CallDoubleMethod(env, jbounds, fun_rectangle_get_x);
    TRACE();
		y =  (*env)->CallDoubleMethod(env, jbounds, fun_rectangle_get_y);
    TRACE();
		w = (*env)->CallDoubleMethod(env, jbounds, fun_rectangle_get_width);
    TRACE();
		h = (*env)->CallDoubleMethod(env, jbounds, fun_rectangle_get_height);
    TRACE();
		rect.x = (x - dx) / scaling;
		rect.y = (y - dy) / scaling;
		rect.width = w / scaling;
		rect.height = h / scaling;

    TRACE();
        jid = (*env)->CallLongMethod(env, jproduct, fun_product_get_id);
    TRACE();
        jpalist = (*env)->CallObjectMethod(env, jdbhelper, fun_database_helper_get_product_arguments, jid);
    TRACE();
        argc = (*env)->CallIntMethod(env, jpalist, fun_array_list_size);
    TRACE();
        argv = calloc(argc, sizeof(char*));
    TRACE();

        for (j = 0; j < argc; j++) {
    TRACE();
            jpa = (*env)->CallObjectMethod(env, jpalist, fun_array_list_get, j);
    TRACE();
            jval = (*env)->CallObjectMethod(env, jpa, fun_product_argument_get_value);
    TRACE();
            val = (char*) (*env)->GetStringUTFChars(env, jval, JNI_FALSE);
    TRACE();
            argv[j] = strdup(val);
    TRACE();
            (*env)->ReleaseStringUTFChars(env, jval, val);
    TRACE();
        }

		DEBUG("[%d] {x:%.0lf, y:%.0lf, width:%.0lf, height:%.0lf} =={scaling:%f, (dx:%.0f, dy:%.0f)}=> {x:%.0lf, y:%.0lf, width:%.0lf, height:%.0lf}\n", i, x, y, w, h, scaling, dx, dy, rect.x, rect.y, rect.width, rect.height);

		*sel = calloc(1, sizeof(struct selectors));
		(*sel)->selector = circular_selector_new(rect.width * 0.5f);
		(*sel)->selector->setname(&(*sel)->selector, name);
		(*sel)->selector->setbounds(&(*sel)->selector, &rect);
        (*sel)->selector->setmodel(&(*sel)->selector, model, argc, argv);

        (*env)->ReleaseStringUTFChars(env, jname, name);
        (*env)->ReleaseStringUTFChars(env, jmodel, model);

        for (j = 0; j < argc; j++)
            free(argv[j]);
        free(argv);

		sel = &(*sel)->next;
	}

	detector = detector_new();
	img = bitmap_new(jwidth, jheight, IMAGE_TYPE_ARGB, &arg);
	smpa = detector->detect(&detector, &img, sela);

	for (smp = smpa; smp; smp = smp->next) {
		jsmp = (*env)->NewObject(env, cls_sample, fun_sample_new);
		jname = (*env)->NewStringUTF(env, smp->name);
		(*env)->CallVoidMethod(env, jsmp, fun_sample_set_name, jname);
		(*env)->CallVoidMethod(env, jsmp, fun_sample_set_sum, smp->sum);
		(*env)->CallVoidMethod(env, jsmp, fun_sample_set_brightness, smp->bv);
		(*env)->CallVoidMethod(env, jsmp, fun_sample_set_concentration, smp->cv);
		(*env)->CallBooleanMethod(env, jlist, fun_array_list_add, jsmp);
		(*env)->DeleteLocalRef(env, jsmp);
		(*env)->DeleteLocalRef(env, jname);
	}

	img->free(&img);
	detector->free(&detector);
	samples_free(&smpa);
	selectors_free(&sela);

	return jlist;
}

static void lincxmap_native_initialize(JNIEnv *env)
{
	TRACE();

	cls_string = (*env)->FindClass(env, CLASS_STRING);
	cls_array_list = (*env)->FindClass(env, CLASS_ARRAY_LIST);
	cls_bitmap = (*env)->FindClass(env, CLASS_BITMAP);
    cls_database_helper = (*env)->FindClass(env, CLASS_DATABASE_HELPER);
	cls_product = (*env)->FindClass(env, CLASS_PRODUCT);
	cls_product_argument = (*env)->FindClass(env, CLASS_PRODUCT_ARGUMENT);
	cls_sample = (*env)->FindClass(env, CLASS_SAMPLE);
	cls_template = (*env)->FindClass(env, CLASS_TEMPLATE);
	cls_rectangle = (*env)->FindClass(env, CLASS_RECTANGLE);
	cls_sample_detector = (*env)->FindClass(env, CLASS_SAMPLE_DETECTOR);
	cls_sample_selector = (*env)->FindClass(env, CLASS_SAMPLE_SELECTOR);
	cls_progress_listener = (*env)->FindClass(env, CLASS_PROGRESS_LISTENER);

    pro_sample_detector_dbhelper = (*env)->GetFieldID(env, cls_sample_detector, "dbhelper", "L"CLASS_DATABASE_HELPER";");
	pro_sample_detector_prg_listener = (*env)->GetFieldID(env, cls_sample_detector, "prgListener", "L"CLASS_PROGRESS_LISTENER";");

	fun_array_list_new = (*env)->GetMethodID(env, cls_array_list, "<init>", "()V");
	fun_array_list_add = (*env)->GetMethodID(env, cls_array_list, "add", "(L"CLASS_OBJECT";)Z");
	fun_array_list_get = (*env)->GetMethodID(env, cls_array_list, "get", "(I)L"CLASS_OBJECT";");
    fun_array_list_size = (*env)->GetMethodID(env, cls_array_list, "size", "()I");

	fun_bitmap_get_width = (*env)->GetMethodID(env, cls_bitmap, "getWidth", "()I");
	fun_bitmap_get_height = (*env)->GetMethodID(env, cls_bitmap, "getHeight", "()I");
	fun_bitmap_get_pixel = (*env)->GetMethodID(env, cls_bitmap, "getPixel", "(II)I");
	fun_bitmap_get_pixels = (*env)->GetMethodID(env, cls_bitmap, "getPixels", "([IIIIIII)V");
	fun_bitmap_is_mutable = (*env)->GetMethodID(env, cls_bitmap, "isMutable", "()Z");
	fun_bitmap_set_pixel = (*env)->GetMethodID(env, cls_bitmap, "setPixel", "(III)V");
	fun_bitmap_set_pixels = (*env)->GetMethodID(env, cls_bitmap, "setPixels", "([IIIIIII)V");

    fun_database_helper_get_product_arguments = (*env)->GetMethodID(env, cls_database_helper, "getProductArguments", "(J)L"CLASS_LIST";");

	fun_rectangle_get_x = (*env)->GetMethodID(env, cls_rectangle, "getX", "()D");
	fun_rectangle_get_y = (*env)->GetMethodID(env, cls_rectangle, "getY", "()D");
	fun_rectangle_get_width = (*env)->GetMethodID(env, cls_rectangle, "getWidth", "()D");
	fun_rectangle_get_height = (*env)->GetMethodID(env, cls_rectangle, "getHeight", "()D");

	fun_product_get_id = (*env)->GetMethodID(env, cls_product, "getId", "()J");
	fun_product_get_name = (*env)->GetMethodID(env, cls_product, "getName", "()L"CLASS_STRING";");
	fun_product_get_model = (*env)->GetMethodID(env, cls_product, "getModel", "()L"CLASS_STRING";");

    fun_product_argument_get_value = (*env)->GetMethodID(env, cls_product_argument, "getValue", "()L"CLASS_STRING";");

	fun_sample_new = (*env)->GetMethodID(env, cls_sample, "<init>", "()V");
	fun_sample_set_sum = (*env)->GetMethodID(env, cls_sample, "setSum", "(I)V");
	fun_sample_set_name = (*env)->GetMethodID(env, cls_sample, "setName", "(L"CLASS_STRING";)V");
	fun_sample_set_brightness = (*env)->GetMethodID(env, cls_sample, "setBrightness", "(D)V");
	fun_sample_set_concentration = (*env)->GetMethodID(env, cls_sample, "setConcentration", "(D)V");

	fun_sample_selector_get_bounds = (*env)->GetMethodID(env, cls_sample_selector, "getBounds", "()L"CLASS_RECTANGLE";");
	fun_sample_selector_get_product = (*env)->GetMethodID(env, cls_sample_selector, "getProduct", "()L"CLASS_PRODUCT";");
	fun_sample_selector_get_scaling = (*env)->GetMethodID(env, cls_sample_selector, "getScaling", "()F");
	fun_sample_selector_get_delta_x = (*env)->GetMethodID(env, cls_sample_selector, "getDeltaX", "()F");
	fun_sample_selector_get_delta_y = (*env)->GetMethodID(env, cls_sample_selector, "getDeltaY", "()F");

	fun_progress_listener_on_progress_changed = (*env)->GetMethodID(env, cls_progress_listener, "onProgressChanged", "(I)V");

#if 0
	cls_string = (*env)->NewGlobalRef(env, cls_string);
	cls_array_list = (*env)->NewGlobalRef(env, cls_array_list);
	cls_bitmap = (*env)->NewGlobalRef(env, cls_bitmap);
    cls_database_helper = (*env)->NewGlobalRef(env, cls_database_helper);
	cls_product = (*env)->NewGlobalRef(env, cls_product);
	cls_product_argument = (*env)->NewGlobalRef(env, cls_product_argument);
	cls_sample = (*env)->NewGlobalRef(env, cls_sample);
	cls_template = (*env)->NewGlobalRef(env, cls_template);
	cls_rectangle = (*env)->NewGlobalRef(env, cls_rectangle);
	cls_sample_detector = (*env)->NewGlobalRef(env, cls_sample_detector);
	cls_sample_selector = (*env)->NewGlobalRef(env, cls_sample_selector);
	cls_progress_listener = (*env)->NewGlobalRef(env, cls_progress_listener);

    pro_sample_detector_dbhelper = (*env)->NewGlobalRef(env, pro_sample_detector_dbhelper);
	pro_sample_detector_prg_listener = (*env)->NewGlobalRef(env, pro_sample_detector_prg_listener);

	fun_array_list_new = (*env)->NewGlobalRef(env, fun_array_list_new);
	fun_array_list_add = (*env)->NewGlobalRef(env, fun_array_list_add);
	fun_array_list_get = (*env)->NewGlobalRef(env, fun_array_list_get);
    fun_array_list_size = (*env)->NewGlobalRef(env, fun_array_list_size);

	fun_bitmap_get_width = (*env)->NewGlobalRef(env, fun_bitmap_get_width);
	fun_bitmap_get_height = (*env)->NewGlobalRef(env, fun_bitmap_get_height);
	fun_bitmap_get_pixel = (*env)->NewGlobalRef(env, fun_bitmap_get_pixel);
	fun_bitmap_get_pixels = (*env)->NewGlobalRef(env, fun_bitmap_get_pixels);
	fun_bitmap_is_mutable = (*env)->NewGlobalRef(env, fun_bitmap_is_mutable);
	fun_bitmap_set_pixel = (*env)->NewGlobalRef(env, fun_bitmap_set_pixel);
	fun_bitmap_set_pixels = (*env)->NewGlobalRef(env, fun_bitmap_set_pixels);

    fun_database_helper_get_product_arguments = (*env)->NewGlobalRef(env, fun_database_helper_get_product_arguments);

	fun_rectangle_get_x = (*env)->NewGlobalRef(env, fun_rectangle_get_x);
	fun_rectangle_get_y = (*env)->NewGlobalRef(env, fun_rectangle_get_y);
	fun_rectangle_get_width = (*env)->NewGlobalRef(env, fun_rectangle_get_width);
	fun_rectangle_get_height = (*env)->NewGlobalRef(env, fun_rectangle_get_height);

	fun_product_get_id = (*env)->NewGlobalRef(env, fun_product_get_id);
	fun_product_get_name = (*env)->NewGlobalRef(env, fun_product_get_name);
	fun_product_get_model = (*env)->NewGlobalRef(env, fun_product_get_model);

    fun_product_argument_get_value = (*env)->NewGlobalRef(env, fun_product_argument_get_value);

	fun_sample_new = (*env)->NewGlobalRef(env, fun_sample_new);
	fun_sample_set_sum = (*env)->NewGlobalRef(env, fun_sample_set_sum);
	fun_sample_set_name = (*env)->NewGlobalRef(env, fun_sample_set_name);
	fun_sample_set_brightness = (*env)->NewGlobalRef(env, fun_sample_set_brightness);
	fun_sample_set_concentration = (*env)->NewGlobalRef(env, fun_sample_set_concentration);

	fun_sample_selector_get_bounds = (*env)->NewGlobalRef(env, fun_sample_selector_get_bounds);
	fun_sample_selector_get_product = (*env)->NewGlobalRef(env, fun_sample_selector_get_product);
	fun_sample_selector_get_scaling = (*env)->NewGlobalRef(env, fun_sample_selector_get_scaling);
	fun_sample_selector_get_delta_x = (*env)->NewGlobalRef(env, fun_sample_selector_get_delta_x);
	fun_sample_selector_get_delta_y = (*env)->NewGlobalRef(env, fun_sample_selector_get_delta_y);

	fun_progress_listener_on_progress_changed = (*env)->NewGlobalRef(env, fun_progress_listener_on_progress_changed);
#endif
}

static void lincxmap_native_finalize(JNIEnv *env)
{
#if 0
	(*env)->DeleteGlobalRef(env, cls_string);
	(*env)->DeleteGlobalRef(env, cls_array_list);
	(*env)->DeleteGlobalRef(env, cls_bitmap);
    (*env)->DeleteGlobalRef(env, cls_database_helper);
	(*env)->DeleteGlobalRef(env, cls_product);
	(*env)->DeleteGlobalRef(env, cls_product_argument);
	(*env)->DeleteGlobalRef(env, cls_sample);
	(*env)->DeleteGlobalRef(env, cls_template);
	(*env)->DeleteGlobalRef(env, cls_rectangle);
	(*env)->DeleteGlobalRef(env, cls_sample_detector);
	(*env)->DeleteGlobalRef(env, cls_sample_selector);
	(*env)->DeleteGlobalRef(env, cls_progress_listener);
    (*env)->DeleteGlobalRef(env, pro_sample_detector_dbhelper);
	(*env)->DeleteGlobalRef(env, pro_sample_detector_prg_listener);
	(*env)->DeleteGlobalRef(env, fun_array_list_new);
	(*env)->DeleteGlobalRef(env, fun_array_list_add);
	(*env)->DeleteGlobalRef(env, fun_array_list_get);
    (*env)->DeleteGlobalRef(env, fun_array_list_size);
	(*env)->DeleteGlobalRef(env, fun_bitmap_get_width);
	(*env)->DeleteGlobalRef(env, fun_bitmap_get_height);
	(*env)->DeleteGlobalRef(env, fun_bitmap_get_pixel);
	(*env)->DeleteGlobalRef(env, fun_bitmap_get_pixels);
	(*env)->DeleteGlobalRef(env, fun_bitmap_is_mutable);
	(*env)->DeleteGlobalRef(env, fun_bitmap_set_pixel);
	(*env)->DeleteGlobalRef(env, fun_bitmap_set_pixels);
    (*env)->DeleteGlobalRef(env, fun_database_helper_get_product_arguments);
	(*env)->DeleteGlobalRef(env, fun_rectangle_get_x); 
	(*env)->DeleteGlobalRef(env, fun_rectangle_get_y);
	(*env)->DeleteGlobalRef(env, fun_rectangle_get_width);
	(*env)->DeleteGlobalRef(env, fun_rectangle_get_height);
	(*env)->DeleteGlobalRef(env, fun_product_get_id);
	(*env)->DeleteGlobalRef(env, fun_product_get_name);
	(*env)->DeleteGlobalRef(env, fun_product_get_model);
    (*env)->DeleteGlobalRef(env, fun_product_argument_get_value);
	(*env)->DeleteGlobalRef(env, fun_sample_new);
	(*env)->DeleteGlobalRef(env, fun_sample_set_sum);
	(*env)->DeleteGlobalRef(env, fun_sample_set_name);
	(*env)->DeleteGlobalRef(env, fun_sample_set_brightness);
	(*env)->DeleteGlobalRef(env, fun_sample_set_concentration);
	(*env)->DeleteGlobalRef(env, fun_sample_selector_get_bounds);
	(*env)->DeleteGlobalRef(env, fun_sample_selector_get_product);
	(*env)->DeleteGlobalRef(env, fun_sample_selector_get_scaling);
	(*env)->DeleteGlobalRef(env, fun_sample_selector_get_delta_x);
	(*env)->DeleteGlobalRef(env, fun_sample_selector_get_delta_y);
	(*env)->DeleteGlobalRef(env, fun_progress_listener_on_progress_changed);
#endif
}

static void lincxmap_native_register(JNIEnv *env)
{
	TRACE();

	const static JNINativeMethod ks_methods[] = {
		{ "detect", SIG_DETECT, (void*) native_detect },
	};

	size_t n = sizeof(ks_methods) / sizeof(JNINativeMethod);
	(*env)->RegisterNatives(env, cls_sample_detector, ks_methods, n);
}

static void lincxmap_native_unregister(JNIEnv *env)
{
    (*env)->UnregisterNatives(env, cls_sample_detector);
}

uint32_t lincxmap_native_bitmap_get_pixel(void *obj, uint32_t x, uint32_t y)
{
	TRACE();

	struct native_obj *arg = (struct native_obj*) obj;
	JNIEnv *env = arg->env;
	jobject jbmp = arg->obj;

	return (*env)->CallIntMethod(env, jbmp, fun_bitmap_get_pixel, x, y);
}

void lincxmap_native_bitmap_get_pixels(void *obj, uint8_t **pixels, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h)
{
	TRACE();

	assert(pixels && *pixels);

	jint *jbuf;
	jintArray *jpixels;
	size_t n = w * h - x;
	struct native_obj *arg = (struct native_obj*) obj;
	JNIEnv *env = arg->env;
	jobject jbmp = arg->obj;
	
	jpixels = (*env)->NewIntArray(env, n);
	(*env)->CallVoidMethod(env, jbmp, fun_bitmap_get_pixels, jpixels, 0, stride, x, y, w, h);
	jbuf = (*env)->GetIntArrayElements(env, jpixels, NULL);
	memcpy(*pixels, jbuf, n * sizeof(uint32_t));
	(*env)->ReleaseIntArrayElements(env, jpixels, jbuf, 0);
	(*env)->DeleteLocalRef(env, jpixels);
}

void lincxmap_native_bitmap_set_pixel(void *obj, uint32_t x, uint32_t y, uint32_t color)
{
	TRACE();

	struct native_obj *arg = (struct native_obj*) obj;
	JNIEnv *env = arg->env;
	jobject jbmp = arg->obj;

	(*env)->CallVoidMethod(env, jbmp, fun_bitmap_set_pixel, x, y, color);
}

void lincxmap_native_bitmap_set_pixels(void *obj, const uint8_t *pixels, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h)
{
	TRACE();

	// TODO
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
	TRACE();

	JNIEnv *env;

	if (JNI_OK != (*vm)->GetEnv(vm, (void**) &env, k_jniver))
		return -1;

	lincxmap_native_initialize(env);
	lincxmap_native_register(env);

	return k_jniver;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved)
{
    TRACE();

	JNIEnv *env;

	if (JNI_OK != (*vm)->GetEnv(vm, (void**) &env, k_jniver))
		return;

    lincxmap_native_unregister(env);
    lincxmap_native_finalize(env);
}

