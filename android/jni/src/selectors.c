/*
 * Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
 *
 * selectors.c
 *
 * @date    Sep 09, 2012
 *
 * @author  Johnson Lee <g.johnsonlee@gmail.com>
 *
 * @version 1.0
 */

#include <assert.h>
#include <stdlib.h>
#include <string.h>

#include <log.h>
#include <selectors.h>

void selectors_free(struct selectors **sela)
{
    TRACE();

    assert(sela && *sela);

    struct selectors *i, *sel;

    for (i = *sela; i;) {
        sel = i;
        i = i->next;
        sel->selector->free(&sel->selector);
        free(sel);
    }

    *sela = NULL;
}

