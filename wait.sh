#!/bin/bash

URL=$1
TIMEOUT=${2:-300}

echo "Waiting for url: [$URL]"
echo "Waiting for [$TIMEOUT] seconds ..."

TRIES=0
until $(curl --output /dev/null --silent --head --fail $URL) || [ ${TRIES} -gt ${TIMEOUT} ]; do
    printf '.'
    sleep $(( TRIES++ ))
done

[ $TRIES -gt $TIMEOUT ] && echo " not availabe"
[ $TRIES -lt $TIMEOUT ] && echo " availabe"

echo Done
