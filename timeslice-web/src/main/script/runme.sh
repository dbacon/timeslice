#! /bin/bash

bindir="$(dirname "$0")"
libdir="$bindir/../lib"

cp="$(fs=("$libdir"/*.jar); export IFS=:; echo "${fs[*]}" )"

while getopts r: opt
do
        case "$opt" in
                -) break;;
                r) RES="$OPTARG";;
        esac
done
shift "$(($OPTIND - 1))"

[ -n "$RES" ] && cp="$RES:$cp"

set -e
set -x

java -cp "$cp" com.enokinomi.timeslice.launcher.Driver "$@"
