#! /bin/bash
printf ',s/[\t ]*$//g\nwq\n' | ed "$1" 1>/dev/null

