#! /bin/bash
#
# vim: ai et sw=4 ts=4
#

find . \
    -name pom.xml \
    -o -name \*.java \
    -o -name \*.css \
    -o -name \*.gwt.xml \
    -o -name \*.ui.xml \
    -o -name \*.ddl \
        -exec ./strip-whitespace.sh {} \;

