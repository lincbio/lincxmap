LOCAL_PATH := $(call my-dir)

build-module = \
    $(eval include $(CLEAR_VARS)) \
    $(eval LOCAL_MODULE          := $1)  \
    $(eval LOCAL_MODULE_FILENAME := lib$1) \
    $(eval LOCAL_SRC_FILES       := $(addprefix src/modules/,$2)) \
    $(eval LOCAL_CFLAGS          += -Wall -D__ANDROID__) \
    $(eval LOCAL_C_INCLUDES      += $(LOCAL_PATH)/include) \
    $(eval LOCAL_LDLIBS          += -llog) \
    $(eval include $(BUILD_SHARED_LIBRARY))

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

$(call build-module,linear,linear.c)
$(call build-module,nexp,nexp.c)

