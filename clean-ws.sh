#! /bin/bash

find . -name \*.java -exec ./strip-whitespace.sh {} \;

