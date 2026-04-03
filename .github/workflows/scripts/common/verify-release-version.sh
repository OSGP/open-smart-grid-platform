#!/bin/bash

RELEASE_VERSION="$1"
REGEX="$2"

echo "::debug::Executing verify-release-version.sh with parameters:"
echo "::debug::RELEASE_VERSION: ${RELEASE_VERSION}"
echo "::debug::REGEX: ${REGEX}"

if [[ ! ${RELEASE_VERSION} =~ ${REGEX} ]]; then
  echo "::error:: Incorrect Release version ${RELEASE_VERSION} does not match ${REGEX}"
  exit 1
fi
