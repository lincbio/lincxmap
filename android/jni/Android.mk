LOCAL_PATH := $(call my-dir)

lincxmap_sources := \
	bitmap.c \
	circularselector.c \
	detector.c \
	image.c \
	native.c \
	samples.c \
	selectors.c \

include $(CLEAR_VARS)

LOCAL_MODULE          := lincxmap
LOCAL_MODULE_FILENAME := liblincxmap
LOCAL_SRC_FILES       := $(addprefix src/,$(lincxmap_sources))
LOCAL_CFLAGS          += -D__ANDROID__ -D__DEBUG__
LOCAL_C_INCLUDES      += $(LOCAL_PATH)/include
LOCAL_LDLIBS          += -llog

include $(BUILD_SHARED_LIBRARY)

