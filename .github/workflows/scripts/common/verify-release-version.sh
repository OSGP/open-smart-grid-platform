#!/bin/bash

RELEASE_VERSION="$1"

echo "::info:: Executing verify-release-version.sh with parameters:"
echo "::info:: RELEASE_VERSION: $RELEASE_VERSION"

regex="^[0-9]+[.][0-9]+[.]0$"
if [[ ! $RELEASE_VERSION =~ $regex ]]; then
  echo "::error:: Incorrect Release version $RELEASE_VERSION does not match $regex"
  exit 1
fi