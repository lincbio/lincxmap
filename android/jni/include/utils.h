/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * utils.h
 *
 * @date    Sep 09, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#if _MSC_VER > 1000
#pragma once
#endif

#ifndef __LINCXMAP_UTILS_H__
#define __LINCXMAP_UTILS_H__

#ifndef ABS
#define ABS(x) ((x) < 0 ? -(x) : (x))
#endif

#ifndef MAX
#define MAX(x, y) ((x) > (y) ? (x) : (y))
#endif

#ifndef MIN
#define MIN(x, y) ((x) < (y) ? (x) : (y))
#endif

#ifndef BOUND
#define BOUND(x, min, max) ((x) < (min) ? (min) : (x) > (max) ? (max) : (x))
#endif

#define SORT(a, cmp) (                                                         \
	{                                                                          \
		int __i__, __j__;                                                      \
		size_t __n__ = sizeof(a) / sizeof(a[0]);                               \
		typeof(*a) __tmp__;                                                    \
		for (__i__ = 0; __i__ < __n__; __i__++) {                              \
			for (__j__ = 1; __j__ < __n__ - __i__; __j__++) {                  \
				if (a[__i__] cmp a[__j__]) {                                   \
					__tmp__ = a[__i__];                                        \
					a[__i__] = a[__j__];                                       \
					a[__j__] = __tmp__;                                        \
				}                                                              \
			}                                                                  \
		}                                                                      \
	})

#endif /* __LINCXMAP_UTILS_H__ */
