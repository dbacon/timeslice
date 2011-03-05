#! /bin/bash
#
# vim: ai et sw=4 ts=4
#

find . \
    -name pom.xml          -exec ./strip-whitespace.sh {} \; \
    -o -name \*.java       -exec ./strip-whitespace.sh {} \; \
    -o -name \*.css        -exec ./strip-whitespace.sh {} \; \
    -o -name \*.gwt.xml    -exec ./strip-whitespace.sh {} \; \
    -o -name \*.ui.xml     -exec ./strip-whitespace.sh {} \; \
    -o -name \*.ddl        -exec ./strip-whitespace.sh {} \; \

