LOCAL_PATH := $(call my-dir)

lincxmap_sources := \
	bitmap.c \
	circularselector.c \
	detector.c \
	native.c \
	pixbuf.c \
	samples.c \
	selectors.c \

# liblincxmap.so
include $(CLEAR_VARS)

LOCAL_MODULE          := lincxmap
LOCAL_MODULE_FILENAME := liblincxmap
LOCAL_SRC_FILES       := $(addprefix src/,$(lincxmap_sources))
LOCAL_CFLAGS          += -Wall -D__ANDROID__
LOCAL_C_INCLUDES      += $(LOCAL_PATH)/include
LOCAL_LDLIBS          += -llog

include $(BUILD_SHARED_LIBRARY)

# liblinear.so
include $(CLEAR_VARS)

LOCAL_MODULE          := linear
LOCAL_MODULE_FILENAME := liblinear
LOCAL_SRC_FILES       := src/models/linear.c
LOCAL_CFLAGS          += -Wall -D__ANDROID__
LOCAL_C_INCLUDES      += $(LOCAL_PATH)/include
LOCAL_LDLIBS          += -llog

include $(BUILD_SHARED_LIBRARY)

