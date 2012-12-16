#
# Copyright (c) 2010-2012, linc-bio Inc. All Rights Reserved.
#
# Makefile
# 
# @data    Nov 30, 2012
# 
# @author  Johnson Lee <g.johnsonlee@gmail.com>
#
# @version 1.0
#

all:
	@ndk-build && ant debug

debug:
	@ndk-build NDK_DEBUG=1 && ant debug

release:
	@ndk-build && ant release

install:
	@ant installd

install-release:
	@ant installr

uninstall:
	@ant uninstall

clean:
	@ndk-build clean && ant clean

.PHONY: all debug release install install-release uninstall clean 

