/*
 * utils.h
 *
 *  Created on: 2012-9-2
 *      Author: Johnson Lee
 */

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

#endif /* __LINCXMAP_UTILS_H__ */
