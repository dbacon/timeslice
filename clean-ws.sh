#! /bin/bash

find . -name \*.java   -exec ./strip-whitespace.sh {} \;
find . -name \*.css    -exec ./strip-whitespace.sh {} \;
find . -name pom.xml   -exec ./strip-whitespace.sh {} \;
find . -name \*.gwt.xml   -exec ./strip-whitespace.sh {} \;

