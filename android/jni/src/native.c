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

    int i, j;
    struct selectors *sela;
    struct selectors **sel = &sela;
    struct native_obj bmparg = { env, jbmp };

    jclass cls_array_list = (*env)->FindClass(env, CLASS_ARRAY_LIST);
    jclass cls_bitmap = (*env)->FindClass(env, CLASS_BITMAP);
    jclass cls_database_helper = (*env)->FindClass(env, CLASS_DATABASE_HELPER);
    jclass cls_product = (*env)->FindClass(env, CLASS_PRODUCT);
    jclass cls_product_argument = (*env)->FindClass(env, CLASS_PRODUCT_ARGUMENT);
    jclass cls_sample = (*env)->FindClass(env, CLASS_SAMPLE);
    jclass cls_shape = (*env)->FindClass(env, CLASS_SHAPE);
    jclass cls_sample_detector = (*env)->FindClass(env, CLASS_SAMPLE_DETECTOR);
    jclass cls_sample_selector = (*env)->FindClass(env, CLASS_SAMPLE_SELECTOR);

    jfieldID pro_sample_detector_dbhelper = (*env)->GetFieldID(env, cls_sample_detector, "dbhelper", "L"CLASS_DATABASE_HELPER";");

    jmethodID fun_array_list_new = (*env)->GetMethodID(env, cls_array_list, "<init>", "()V");
    jmethodID fun_array_list_add = (*env)->GetMethodID(env, cls_array_list, "add", "(L"CLASS_OBJECT";)Z");
    jmethodID fun_array_list_get = (*env)->GetMethodID(env, cls_array_list, "get", "(I)L"CLASS_OBJECT";");
    jmethodID fun_array_list_size = (*env)->GetMethodID(env, cls_array_list, "size", "()I");

    jmethodID fun_bitmap_get_width = (*env)->GetMethodID(env, cls_bitmap, "getWidth", "()I");
    jmethodID fun_bitmap_get_height = (*env)->GetMethodID(env, cls_bitmap, "getHeight", "()I");

    jmethodID fun_database_helper_get_product_arguments = (*env)->GetMethodID(env, cls_database_helper, "getProductArguments", "(J)L"CLASS_LIST";");

    jmethodID fun_shape_get_x = (*env)->GetMethodID(env, cls_shape, "getX", "()F");
    jmethodID fun_shape_get_y = (*env)->GetMethodID(env, cls_shape, "getY", "()F");
    jmethodID fun_shape_get_width = (*env)->GetMethodID(env, cls_shape, "getWidth", "()F");
    jmethodID fun_shape_get_height = (*env)->GetMethodID(env, cls_shape, "getHeight", "()F");

    jmethodID fun_product_get_id = (*env)->GetMethodID(env, cls_product, "getId", "()J");
    jmethodID fun_product_get_name = (*env)->GetMethodID(env, cls_product, "getName", "()L"CLASS_STRING";");
    jmethodID fun_product_get_model = (*env)->GetMethodID(env, cls_product, "getModel", "()L"CLASS_STRING";");

    jmethodID fun_product_argument_get_value = (*env)->GetMethodID(env, cls_product_argument, "getValue", "()L"CLASS_STRING";");

    jmethodID fun_sample_new = (*env)->GetMethodID(env, cls_sample, "<init>", "()V");
    jmethodID fun_sample_set_sum = (*env)->GetMethodID(env, cls_sample, "setSum", "(I)V");
    jmethodID fun_sample_set_name = (*env)->GetMethodID(env, cls_sample, "setName", "(L"CLASS_STRING";)V");
    jmethodID fun_sample_set_brightness = (*env)->GetMethodID(env, cls_sample, "setBrightness", "(D)V");
    jmethodID fun_sample_set_concentration = (*env)->GetMethodID(env, cls_sample, "setConcentration", "(D)V");

    jmethodID fun_sample_selector_get_shape = (*env)->GetMethodID(env, cls_sample_selector, "getShape", "()L"CLASS_SHAPE";");
    jmethodID fun_sample_selector_get_product = (*env)->GetMethodID(env, cls_sample_selector, "getProduct", "()L"CLASS_PRODUCT";");
    jmethodID fun_sample_selector_get_scaling = (*env)->GetMethodID(env, cls_sample_selector, "getScaling", "()F");
    jmethodID fun_sample_selector_get_delta_x = (*env)->GetMethodID(env, cls_sample_selector, "getDeltaX", "()F");
    jmethodID fun_sample_selector_get_delta_y = (*env)->GetMethodID(env, cls_sample_selector, "getDeltaY", "()F");

    jobject jlist = (*env)->NewObject(env, cls_array_list, fun_array_list_new);
    jobject jdbhelper = (*env)->GetObjectField(env, jself, pro_sample_detector_dbhelper);
    jsize nselectors = (*env)->GetArrayLength(env, jsela);
    jint jwidth = (*env)->CallIntMethod(env, jbmp, fun_bitmap_get_width);
    jint jheight = (*env)->CallIntMethod(env, jbmp, fun_bitmap_get_height);

    DEBUG("cls_array_list=%p, fun_array_list_new=%p", cls_array_list, fun_array_list_new);

    // initialize selectors
    for (i = 0; i < nselectors; i++) {
        jobject jsel = (*env)->GetObjectArrayElement(env, jsela, i);
        jobject jproduct = (*env)->CallObjectMethod(env, jsel, fun_sample_selector_get_product);
        jlong jid = (*env)->CallLongMethod(env, jproduct, fun_product_get_id);
        jobject jmodel = (*env)->CallObjectMethod(env, jproduct, fun_product_get_model);
        jobject jname = (*env)->CallObjectMethod(env, jproduct, fun_product_get_name);
        jobject jshape = (*env)->CallObjectMethod(env, jsel, fun_sample_selector_get_shape);
        jobject jpalist = (*env)->CallObjectMethod(env, jdbhelper, fun_database_helper_get_product_arguments, jid);
        const char *model = (const char*) (*env)->GetStringUTFChars(env, jmodel, JNI_FALSE);
        const char *name = (const char*) (*env)->GetStringUTFChars(env, jname, JNI_FALSE);

        int argc = (*env)->CallIntMethod(env, jpalist, fun_array_list_size);
        char **argv = calloc(argc, sizeof(char*));
        float dx = (*env)->CallFloatMethod(env, jsel, fun_sample_selector_get_delta_x);
        float dy = (*env)->CallFloatMethod(env, jsel, fun_sample_selector_get_delta_y);
        float scaling = (*env)->CallFloatMethod(env, jsel, fun_sample_selector_get_scaling);
        float x = (*env)->CallFloatMethod(env, jshape, fun_shape_get_x);
        float y =  (*env)->CallFloatMethod(env, jshape, fun_shape_get_y);
        float w = (*env)->CallFloatMethod(env, jshape, fun_shape_get_width);
        float h = (*env)->CallFloatMethod(env, jshape, fun_shape_get_height);

        INFO("Delta  = {dx:%f, dy:%f}\n", dx, dy);
        INFO("Origin = {x:%f, y:%f, w:%f, h:%f}\n", x, y, w, h);

        struct rectangle rect;
        rect.x = (x - dx) / scaling;
        rect.y = (y - dy) / scaling;
        rect.width = w / scaling;
        rect.height = h / scaling;

        INFO("Resize = {x:%f, y:%f, w:%f, h:%f}\n", rect.x, rect.y, rect.width, rect.height);

        for (j = 0; j < argc; j++) {
            jobject jpa = (*env)->CallObjectMethod(env, jpalist, fun_array_list_get, j);
            jobject jval = (*env)->CallObjectMethod(env, jpa, fun_product_argument_get_value);
            const char *val = (const char*) (*env)->GetStringUTFChars(env, jval, JNI_FALSE);

            argv[j] = strdup(val);
            (*env)->ReleaseStringUTFChars(env, jval, val);

            // delete local references
            (*env)->DeleteLocalRef(env, jpa);
            (*env)->DeleteLocalRef(env, jval);
        }

        DEBUG("[%d] {x:%.0lf, y:%.0lf, width:%.0lf, height:%.0lf} == {scaling:%f, (dx:%.0f, dy:%.0f)} => {x:%.0lf, y:%.0lf, width:%.0lf, height:%.0lf}\n", i, x, y, w, h, scaling, dx, dy, rect.x, rect.y, rect.width, rect.height);

        *sel = calloc(1, sizeof(struct selectors));
        (*sel)->selector = selector_new(&rect);
        (*sel)->selector->setname(&(*sel)->selector, name);
        (*sel)->selector->setmodel(&(*sel)->selector, model, argc, argv);

        // release local string
        (*env)->ReleaseStringUTFChars(env, jname, name);
        (*env)->ReleaseStringUTFChars(env, jmodel, model);

        // delete local references
        (*env)->DeleteLocalRef(env, jpalist);
        (*env)->DeleteLocalRef(env, jshape);
        (*env)->DeleteLocalRef(env, jname);
        (*env)->DeleteLocalRef(env, jmodel);
        (*env)->DeleteLocalRef(env, jproduct);
        (*env)->DeleteLocalRef(env, jsel);

        // free memory
        for (j = 0; j < argc; j++)
            free(argv[j]);
        free(argv);

        sel = &(*sel)->next;
    }

    struct sample *smp;
    detector_t detector = detector_new();
    image_t img = bitmap_new(jwidth, jheight, IMAGE_TYPE_ARGB, &bmparg);
    struct sample *smpa = detector->detect(&detector, &img, sela);

    for (smp = smpa; smp; smp = smp->next) {
        jobject jsmp = (*env)->NewObject(env, cls_sample, fun_sample_new);
        jobject jname = (*env)->NewStringUTF(env, smp->name);

        (*env)->CallVoidMethod(env, jsmp, fun_sample_set_name, jname);
        (*env)->CallVoidMethod(env, jsmp, fun_sample_set_sum, smp->sum);
        (*env)->CallVoidMethod(env, jsmp, fun_sample_set_brightness, smp->bv);
        (*env)->CallVoidMethod(env, jsmp, fun_sample_set_concentration, smp->cv);
        (*env)->CallBooleanMethod(env, jlist, fun_array_list_add, jsmp);

        // delete local references
        (*env)->DeleteLocalRef(env, jsmp);
        (*env)->DeleteLocalRef(env, jname);
    }

    // delete local references
    (*env)->DeleteLocalRef(env, jdbhelper);

    // free memory
    img->free(&img);
    detector->free(&detector);
    samples_free(&smpa);
    selectors_free(&sela);

    return jlist;
}

static void lincxmap_native_initialize(JNIEnv *env)
{
    TRACE();
}

static void lincxmap_native_finalize(JNIEnv *env)
{
    TRACE();
}

static void lincxmap_native_register(JNIEnv *env)
{
    TRACE();

    const static JNINativeMethod ks_methods[] = {
        { "detect", SIG_DETECT, (void*) native_detect },
    };

    jclass clazz = (*env)->FindClass(env, CLASS_SAMPLE_DETECTOR);
    size_t n = sizeof(ks_methods) / sizeof(JNINativeMethod);
    (*env)->RegisterNatives(env, clazz, ks_methods, n);

    // delete local references
    (*env)->DeleteLocalRef(env, clazz);
}

static void lincxmap_native_unregister(JNIEnv *env)
{
    jclass clazz = (*env)->FindClass(env, CLASS_SAMPLE_DETECTOR);
    (*env)->UnregisterNatives(env, clazz);

    // delete local references
    (*env)->DeleteLocalRef(env, clazz);
}

uint32_t lincxmap_native_bitmap_get_pixel(void *obj, uint32_t x, uint32_t y)
{
    TRACE();

    struct native_obj *arg = (struct native_obj*) obj;
    JNIEnv *env = arg->env;
    jobject jbmp = arg->obj;
    jclass clazz = (*env)->FindClass(env, CLASS_BITMAP);
    jmethodID fun = (*env)->GetMethodID(env, clazz, "getPixel", "(II)I");
    uint32_t pixel = (*env)->CallIntMethod(env, jbmp, fun, x, y);

    // delete local references
    (*env)->DeleteLocalRef(env, fun);
    (*env)->DeleteLocalRef(env, clazz);

    return pixel;
}

void lincxmap_native_bitmap_get_pixels(void *obj, uint8_t **pixels, uint32_t stride, uint32_t x, uint32_t y, uint32_t w, uint32_t h)
{
    TRACE();

    assert(pixels && *pixels);

    size_t n = w * h - x;
    struct native_obj *arg = (struct native_obj*) obj;

    jint *jbuf;
    JNIEnv *env = arg->env;
    jobject jbmp = arg->obj;
    jclass clazz = (*env)->FindClass(env, CLASS_BITMAP);
    jmethodID fun = (*env)->GetMethodID(env, clazz, "getPixels", "([IIIIIII)V");
    jintArray jpixels = (*env)->NewIntArray(env, n);

    (*env)->CallVoidMethod(env, jbmp, fun, jpixels, 0, stride, x, y, w, h);
    jbuf = (*env)->GetIntArrayElements(env, jpixels, NULL);
    memcpy(*pixels, jbuf, n * sizeof(uint32_t));
    (*env)->ReleaseIntArrayElements(env, jpixels, jbuf, 0);

    // delete local references
    (*env)->DeleteLocalRef(env, jpixels);
    (*env)->DeleteLocalRef(env, fun);
    (*env)->DeleteLocalRef(env, clazz);
}

void lincxmap_native_bitmap_set_pixel(void *obj, uint32_t x, uint32_t y, uint32_t color)
{
    TRACE();

    struct native_obj *arg = (struct native_obj*) obj;
    JNIEnv *env = arg->env;
    jobject jbmp = arg->obj;
    jclass clazz = (*env)->FindClass(env, CLASS_BITMAP);
    jmethodID fun = (*env)->GetMethodID(env, clazz, "setPixel", "(III)V");

    (*env)->CallVoidMethod(env, jbmp, fun, x, y, color);

    // delete local references
    (*env)->DeleteLocalRef(env, fun);
    (*env)->DeleteLocalRef(env, clazz);
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

