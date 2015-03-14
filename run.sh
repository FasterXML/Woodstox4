#!/bin/sh

java -Xmx128m -server -XX:-PrintGC -XX:-PrintGCDetails \
 -cp lib/\*\
:build/classes/woodstox \
 $*
