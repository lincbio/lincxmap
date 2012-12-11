#
# Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
#
# Android.mk
#
# @date    Sep 09, 2012
#
# @author  Johnson Lee <g.johnsonlee@gmail.com>
#
# @version 1.0
#

LOCAL_PATH := $(call my-dir)

lincxmap_sources := \
	bmp.c \
	circularselector.c \
	detector.c \
	filter.c \
	image.c \
	native.c \
	pgm.c \
	samples.c \
	selectors.c \

include $(CLEAR_VARS)

LOCAL_MODULE          := lincxmap
LOCAL_MODULE_FILENAME := liblincxmap
LOCAL_SRC_FILES       := $(addprefix src/,$(lincxmap_sources))
LOCAL_CFLAGS          += -Wall -D__ANDROID__
LOCAL_C_INCLUDES      += $(LOCAL_PATH)/include
LOCAL_LDLIBS          += -llog

include $(BUILD_SHARED_LIBRARY)

