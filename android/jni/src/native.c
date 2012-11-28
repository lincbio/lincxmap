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

#include <stdlib.h>
#include <stdio.h>

#include <circularselector.h>
#include <detector.h>
#include <log.h>
#include <samples.h>
#include <selectors.h>

#include "native.h"

#define SIG_DETECT "(L"CLASS_BITMAP";L"CLASS_TEMPLATE";[L"CLASS_SAMPLE_SELECTOR";)L"CLASS_LIST";"

extern struct image_options bmp_options;

JNIEXPORT jobject JNICALL native_detect(JNIEnv *env, jobject jself,
		jobject jbmp, jobject jtpl, jobjectArray jsela)
{
	int i;
	char *name;
	char buf[256];
	float dx, dy;
	float scaling;
	double x, y, w, h;
	struct rectangle rect;
	struct sample *smp, *smpa;
	struct selectors *sela, **sel = &sela;
	struct image_arg bmp_arg = { env, jbmp };

	jsize len;
	jobject jbounds;
	jobject jdata;
	jstring jname;
	jobject jsel;
	jobject jsmp;

	detector_t detector = detector_new();
	image_t image = image_new(&bmp_options, &bmp_arg);
	jsize n = (*env)->GetArrayLength(env, jsela);
	jobject jlist = (*env)->NewObject(env, cls_array_list, fun_array_list_new);

	for (i = 0; i < n; i++) {
		jsel = (*env)->GetObjectArrayElement(env, jsela, i);
		jdata = (*env)->CallObjectMethod(env, jsel, fun_sample_selector_get_data);

		if ((*env)->IsInstanceOf(env, jdata, cls_product)) {
			jname = (*env)->CallObjectMethod(env, jdata, fun_product_get_name);
			len = (*env)->GetStringUTFLength(env, jname);
			name = (char*) (*env)->GetStringUTFChars(env, jname, JNI_FALSE);
		} else if ((*env)->IsInstanceOf(env, jdata, cls_string)) {
			name = (char*) (*env)->GetStringUTFChars(env, jdata, JNI_FALSE);
			len = (*env)->GetStringUTFLength(env, jdata);
		} else {
			sprintf(buf, "%d", i);
			len = strlen(buf);
			name = buf;
		}

		dx = (*env)->CallFloatMethod(env, jsel, fun_sample_selector_get_delta_x);
		dy = (*env)->CallFloatMethod(env, jsel, fun_sample_selector_get_delta_y);
		scaling = (*env)->CallFloatMethod(env, jsel, fun_sample_selector_get_scaling);

		jbounds = (*env)->CallObjectMethod(env, jsel, fun_sample_selector_get_bounds);
		x = (*env)->CallDoubleMethod(env, jbounds, fun_rectangle_get_x);
		y =  (*env)->CallDoubleMethod(env, jbounds, fun_rectangle_get_y);
		w = (*env)->CallDoubleMethod(env, jbounds, fun_rectangle_get_width);
		h = (*env)->CallDoubleMethod(env, jbounds, fun_rectangle_get_height);

		rect.x = x;		// ((x + dx) * scaling);
		rect.y = y;		// ((y + dy) * scaling);
		rect.width = w;	// (w * scaling);
		rect.height = h;// (h * scaling);

		*sel = calloc(1, sizeof(struct selectors));
		(*sel)->selector = circular_selector_new(rect.width * 0.5f);
		(*sel)->selector->setname(&(*sel)->selector, name, len);
		(*sel)->selector->setbounds(&(*sel)->selector, &rect);

		(*env)->ReleaseStringUTFChars(env, jname, name);
		sel = &(*sel)->next;
	}

	smpa = detector->detect(&detector, image, sela);
	for (smp = smpa; smp; smp = smp->next) {
		jsmp = (*env)->NewObject(env, cls_sample, fun_sample_new);
		jname = (*env)->NewStringUTF(env, smp->name);
		(*env)->CallVoidMethod(env, jsmp, fun_sample_set_name, jname);
		(*env)->CallVoidMethod(env, jsmp, fun_sample_set_sum, smp->sum);
		(*env)->CallVoidMethod(env, jsmp, fun_sample_set_brightness, smp->bv);
		(*env)->CallVoidMethod(env, jsmp, fun_sample_set_concentration, smp->cv);
		(*env)->CallBooleanMethod(env, jlist, fun_array_list_add, jsmp);
	}

	image->free(&image);
	detector->free(&detector);
	samples_free(&smpa);
	selectors_free(&sela);

	return jlist;
}

const static JNINativeMethod gs_methods[] = {
	{ "detect", SIG_DETECT, (void*) native_detect },
};

static void native_init(JNIEnv *env)
{
	cls_string = (*env)->FindClass(env, CLASS_STRING);
	cls_array_list = (*env)->FindClass(env, CLASS_ARRAY_LIST);
	cls_bitmap = (*env)->FindClass(env, CLASS_BITMAP);
	cls_product = (*env)->FindClass(env, CLASS_PRODUCT);
	cls_sample = (*env)->FindClass(env, CLASS_SAMPLE);
	cls_template = (*env)->FindClass(env, CLASS_TEMPLATE);
	cls_rectangle = (*env)->FindClass(env, CLASS_RECTANGLE);
	cls_sample_detector = (*env)->FindClass(env, CLASS_SAMPLE_DETECTOR);
	cls_sample_selector = (*env)->FindClass(env, CLASS_SAMPLE_SELECTOR);
	cls_progress_listener = (*env)->FindClass(env, CLASS_PROGRESS_LISTENER);

	pro_sample_detector_prg_listener = (*env)->GetFieldID(env, cls_sample_detector, "prgListener", "L"CLASS_PROGRESS_LISTENER";");

	fun_array_list_new = (*env)->GetMethodID(env, cls_array_list, "<init>", "()V");
	fun_array_list_add = (*env)->GetMethodID(env, cls_array_list, "add", "(L"CLASS_OBJECT";)Z");

	fun_bitmap_get_width = (*env)->GetMethodID(env, cls_bitmap, "getWidth", "()I");
	fun_bitmap_get_height = (*env)->GetMethodID(env, cls_bitmap, "getHeight", "()I");
	fun_bitmap_get_pixel = (*env)->GetMethodID(env, cls_bitmap, "getPixel", "(II)I");

	fun_rectangle_get_x = (*env)->GetMethodID(env, cls_rectangle, "getX", "()D");
	fun_rectangle_get_y = (*env)->GetMethodID(env, cls_rectangle, "getY", "()D");
	fun_rectangle_get_width = (*env)->GetMethodID(env, cls_rectangle, "getWidth", "()D");
	fun_rectangle_get_height = (*env)->GetMethodID(env, cls_rectangle, "getHeight", "()D");

	fun_product_get_name = (*env)->GetMethodID(env, cls_product, "getName", "()L"CLASS_STRING";");

	fun_sample_new = (*env)->GetMethodID(env, cls_sample, "<init>", "()V");
	fun_sample_set_sum = (*env)->GetMethodID(env, cls_sample, "setSum", "(I)V");
	fun_sample_set_name = (*env)->GetMethodID(env, cls_sample, "setName", "(L"CLASS_STRING";)V");
	fun_sample_set_brightness = (*env)->GetMethodID(env, cls_sample, "setBrightness", "(D)V");
	fun_sample_set_concentration = (*env)->GetMethodID(env, cls_sample, "setConcentration", "(D)V");

	fun_sample_selector_get_bounds = (*env)->GetMethodID(env, cls_sample_selector, "getBounds", "()L"CLASS_RECTANGLE";");
	fun_sample_selector_get_data = (*env)->GetMethodID(env, cls_sample_selector, "getData", "()L"CLASS_OBJECT";");
	fun_sample_selector_get_scaling = (*env)->GetMethodID(env, cls_sample_selector, "getScaling", "()F");
	fun_sample_selector_get_delta_x = (*env)->GetMethodID(env, cls_sample_selector, "getDeltaX", "()F");
	fun_sample_selector_get_delta_y = (*env)->GetMethodID(env, cls_sample_selector, "getDeltaY", "()F");

	fun_progress_listener_on_progress_changed = (*env)->GetMethodID(env, cls_progress_listener, "onProgressChanged", "(I)V");
}

static void native_register(JNIEnv *env)
{
	size_t n = sizeof(gs_methods) / sizeof(JNINativeMethod);
	(*env)->RegisterNatives(env, cls_sample_detector, gs_methods, n);
}

void native_setup(JavaVM *vm, void *reserved)
{
	JNIEnv *env = NULL;

	if (JNI_OK != (*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4))
		return;

	native_init(env);
	native_register(env);
}

void native_release(JavaVM *vm, void *reserved)
{
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
	jvm = vm;
	native_setup(vm, reserved);

	return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL JNI_OnUnLoad(JavaVM *vm, void *reserved)
{
	native_release(vm, reserved);
	jvm = NULL;
}
