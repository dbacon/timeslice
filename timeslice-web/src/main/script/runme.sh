#! /bin/bash

bindir="$(dirname "$0")"
libdir="$bindir/../lib"

cp="$(fs=("$libdir"/*.jar); export IFS=:; echo "${fs[*]}" )"

set -e
set -x

java -cp "$cp" com.enokinomi.timeslice.launcher.Driver "$@"
