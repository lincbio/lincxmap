/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * log.h
 *
 * @date    Sep 01, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#if _MSC_VER > 1000
#pragma once
#endif

#ifndef __LINCXMAP_LOG_H__
#define __LINCXMAP_LOG_H__

#ifdef __ANDROID__

#include <android/log.h>

#define __LOG_TAG__ __FILE__

#ifdef NDEBUG
#define DEBUG(fmt, ...)
#else /* NDEBUG */
#define DEBUG(fmt, ...) \
	__android_log_print(ANDROID_LOG_DEBUG, __LOG_TAG__, fmt, ##__VA_ARGS__)
#endif /* !NDEBUG */

#define INFO(fmt, ...) \
	__android_log_print(ANDROID_LOG_INFO, __LOG_TAG__, fmt, ##__VA_ARGS__)

#define ERROR(fmt, ...) \
	__android_log_print(ANDROID_LOG_ERROR, __LOG_TAG__, fmt, ##__VA_ARGS__)

#else /* __ANDROID__ */

#ifdef NDEBUG
#define DEBUG(fmt, ...)
#else /* NDEBUG */
#define DEBUG(fmt, ...) \
	printf("%s#%d: ", __FUNCTION__, __LINE__); \
	printf(fmt, ##__VA_ARGS__)
#endif /* !NDEBUG */

#define INFO(fmt, ...) \
	printf("%s#%d: ", __FUNCTION__, __LINE__); \
	printf(fmt, ##__VA_ARGS__)

#define ERROR(fmt, ...) \
	fprintf(stderr, "%s#%d: ", __FUNCTION__, __LINE__); \
	fprintf(stderr, fmt, ##__VA_ARGS__)

#endif /* !__ANDROID__ */

#endif /* __LINCXMAP_LOG_H__ */
