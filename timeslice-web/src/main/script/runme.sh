#! /bin/bash

#set -e
#set -x

# use the cd/pwd method to get explicit paths if needed.
#bindir="$(cd $(dirname -- "$0") && pwd)"

# leave as relative (however the script was launched).
bindir="$(dirname -- "$0")"

topdir="$(dirname -- "$bindir")"
libdir="$topdir/lib"
webdir="$topdir/web"

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

WEBARG="--default-web-root $webdir"

JAVA=java
[ -n "$JAVA_HOME" ] && JAVA="$JAVA_HOME/bin/java"

set -e
set -x

"$JAVA" -cp "$cp" com.enokinomi.timeslice.web.launcher.Driver $WEBARG "$@"
