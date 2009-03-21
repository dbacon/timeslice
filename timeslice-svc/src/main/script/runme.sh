#! /bin/ksh
#
#

set -e

# TODO: get these bits into tokens.
SVCVERSION="1.0.3-SNAPSHOT"
WEBVERSION="1.0.3-SNAPSHOT"

WEBROOT="timeslice-web-${WEBVERSION}"

WEBROOTARCH="$WEBROOT-static-web-root.zip"

ROOT="var/lib/webroot/$WEBROOT"
PORT="9080"


#----------------------------------------------------------
# Check command-line switches


while getopts p: arg
do
	case "$arg" in
		p) PORT="$OPTARG";;
	esac
done
shift $(($OPTIND - 1))


#----------------------------------------------------------
# Re-create web root folder from distribution

cd var/lib/webroot

# TODO: check and avoid cases like "/"
if [ -d "$WEBROOT" ]
then
	rm -rf "$WEBROOT"
fi

unzip "$WEBROOTARCH"

cd -


#----------------------------------------------------------
# create ACL

restoreecho()
{
	stty echo
}

trap restoreecho EXIT 

if [ ! -r "users.acl.txt" ]
then
	printf "Create a user: username: "
	read username
	printf "Password: "
	stty -echo
	read password
	stty echo
	
	printf "%s:%s\n" "$username" "$password" >> users.acl.txt
	chmod go-rwx users.acl.txt
fi


#----------------------------------------------------------
# Run web service

exec java \
	-jar lib/timeslice-svc-${SVCVERSION}-jar-with-dependencies.jar \
	bacond.timeslicer.app.restlet.svc.Driver \
		--port "$PORT" \
		--root "$ROOT"
