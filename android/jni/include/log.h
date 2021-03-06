/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
 *
 * log.h
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#ifndef __LINCXMAP_LOG_H__
#define __LINCXMAP_LOG_H__

#ifdef NDEBUG
#  define DEBUG(fmt, ...)
#  define TRACE()
#endif /* NDEBUG */

#ifdef __ANDROID__

#include <android/log.h>

#define __LOG_TAG__ __FILE__

#ifndef NDEBUG
#define DEBUG(fmt, ...) \
    __android_log_print(ANDROID_LOG_DEBUG, __LOG_TAG__, fmt, ##__VA_ARGS__)

#define TRACE() \
    __android_log_print(ANDROID_LOG_DEBUG, __LOG_TAG__, "%s(%d)", __FUNCTION__, __LINE__)
#endif /* !NDEBUG */

#define INFO(fmt, ...) \
    __android_log_print(ANDROID_LOG_INFO, __LOG_TAG__, fmt, ##__VA_ARGS__)

#define ERROR(fmt, ...) \
    __android_log_print(ANDROID_LOG_ERROR, __LOG_TAG__, fmt, ##__VA_ARGS__)

#else /* __ANDROID__ */

#ifndef NDEBUG
#define DEBUG(fmt, ...) \
    printf("%s#%s(%d): ", __FILE__, __FUNCTION__, __LINE__); \
    printf(fmt, ##__VA_ARGS__)

#define TRACE() \
    printf("%s#%s(%d): ", __FILE__, __FUNCTION__, __LINE__);
#endif /* !NDEBUG */

#define INFO(fmt, ...) \
    printf("%s:%d#%s: ", __FILE__, __LINE__, __FUNCTION__); \
    printf(fmt, ##__VA_ARGS__)

#define ERROR(fmt, ...) \
    printf("%s:%d#%s: ", __FILE__, __LINE__, __FUNCTION__); \
    fprintf(stderr, fmt, ##__VA_ARGS__)

#endif /* !__ANDROID__ */

#endif /* __LINCXMAP_LOG_H__ */

