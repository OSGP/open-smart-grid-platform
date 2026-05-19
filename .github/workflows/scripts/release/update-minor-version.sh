#!/bin/bash

RELEASE_VERSION=$1

echo "::debug::Executing update-minor-version.sh with parameters:"
echo "::debug::RELEASE_VERSION: ${RELEASE_VERSION}"

IFS="." read -r -a split_versions <<< "${RELEASE_VERSION}"
major_version="${split_versions[0]}"
minor_version="${split_versions[1]}"
patch_version=0
incremented_minor_version=$((minor_version+1))
echo "::debug::Incremented minor version: ${incremented_minor_version}"
echo "new=${major_version}.${incremented_minor_version}.${patch_version}" >> "$GITHUB_OUTPUT"
