#! /bin/bash
#
#

set -e

SVCVERSION="${pom.version}"
WEBVERSION="${pom.version}"

PORT=""

#----------------------------------------------------------
# Check command-line switches


while getopts a:p:w: arg
do
    case "$arg" in
        a) ACLFILE="$OPTARG";;
        p) PORT="$OPTARG";;
        w) WAR="$OPTARG";;
    esac
done
shift $(($OPTIND - 1))

topdir="$( (cd "$(dirname $0)/.." && pwd) )"

#----------------------------------------------------------
# create RC file

RCFILE="$HOME/.timeslicerc"

if [ ! -r "$RCFILE" ]
then
    printf "Creating rc file ...\n"

    # port stuff - make sure it was passed.
    if [ -z "$PORT" ]
    then
        printf "  You must specify -p <port> if running for the 1st time.\n"
        exit 1
    fi

    # acl file - make sure it's created.
    ACLFILE="${ACLFILE:-$HOME/.timeslice.acl}"
    if [ ! -r "$ACLFILE" ]
    then
        printf "Specified ACL file not found, creating.\n"
        printf "Create a user:\n"
        printf "  username: "
        read username
        printf "  password: "
        read -s password
        printf "\n"

        printf "%s:%s\n" "$username" "$password" >> "$ACLFILE"
        chmod go-rwx "$ACLFILE"
    else
        printf "Found existing ACL file.\n";
    fi

    # http proxy stuff
    printf "Do you need an HTTP proxy ? "
    read ans
    case "$ans" in
        [Yy])
            printf "  proxy host       (proxhost.mydomain.com)   : "
            read HTTP_PROXY_HOST
            printf "  proxy port       (88)                      : "
            read HTTP_PROXY_PORT
            printf "  proxy exceptions (*.mydomain.com)          : "
            read HTTP_PROXY_EXCEPT
        ;;
    esac

    # safedir stuff
    # Wherever this was unzipped,
    # we should be able to unzip another one.
    safedir="$( cd "$topdir/.." && pwd )"

    datadir="${HOME}/.timeslice-data"

    printf "Where to store timeslice data?\n"
    printf " default is '%s', enter to accept, or type in a new one: " "$datadir"
    read
    datadir="${REPLY:-$datadir}"

    [ -d "$datadir" ] || {
        printf "Datadir '%s' doesn't exist, create now (Y/n) ? " "$datadir"
        read
        case "$REPLY" in
            ""|y|Y) mkdir -p "$datadir"
        esac
    }

    if ls "$datadir"/*.sd.properties > /dev/null 2>&1
    then
        printf "Found existing datastore(s).\n"
    else
        printf "Create a default on-disk data store? (Y/n) "
        read
        case "$REPLY" in
            ""|y|Y)
                cat << EOF > "$datadir/default.sd.properties"
type=hsqldb
starting=2000-01-01T00:00:00.000Z
ending=3000-12-31T00:00:00.000Z
firsttagtext=[--first-partial-task--]
hsqldb.name=hsql/default-01
autoenable=true
EOF
            ;;
        esac
    fi

    # create the rcfile
    cat << EOF > "$RCFILE"

# generated by $0 on $(date).

timeslice.port = $PORT
timeslice.acl = $ACLFILE
timeslice.safedir = $safedir
timeslice.war = ${topdir}/webapps/timeslice-web-${WEBVERSION}.war
timeslice.datadir = $datadir
timeslice.tzoffset = 9

EOF

    [ "$HTTP_PROXY_HOST" ] && printf "http.proxyHost = %s\n" "$HTTP_PROXY_HOST" >> "$RCFILE"
    [ "$HTTP_PROXY_PORT" ] && printf "http.proxyPort = %s\n" "$HTTP_PROXY_PORT" >> "$RCFILE"
    [ "$HTTP_PROXY_EXCEPT" ] && printf "http.nonProxyHosts = %s\n" "$HTTP_PROXY_EXCEPT" >> "$RCFILE"

    printf "\n" >> "$RCFILE"

else
    printf "Found existing RC file.\n";
fi


#----------------------------------------------------------
# Run web service

set -x
set +e

java \
    -jar "${topdir}/lib/timeslice-host-${SVCVERSION}-jar-with-dependencies.jar" \
    "$RCFILE"
