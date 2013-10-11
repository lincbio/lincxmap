/*
 * Copyright (c) 2010-2013, linc-bio Inc. All Rights Reserved.
 *
 * samples.c
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
#include <samples.h>

void samples_add(struct sample **smpa, struct sample *smp)
{
    TRACE();

    assert(smpa && *smpa);
    assert(smp);

    (*smpa)->next = smp;
    *smpa = smp;
}

void samples_free(struct sample **smpa)
{
    TRACE();

    assert(smpa && *smpa);

    struct sample *i, *smp;

    for (i = *smpa; i; ) {
        smp = i;
        i = i->next;
        free(smp);
    }

    *smpa = NULL;
}

