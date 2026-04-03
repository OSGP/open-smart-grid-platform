#!/bin/bash

RELEASE_VERSION=$1

echo "::debug::Executing update-patch-version.sh with parameters:"
echo "::debug::RELEASE_VERSION: ${RELEASE_VERSION}"

IFS="." read -r -a split_versions <<< "${RELEASE_VERSION}"
major_version="${split_versions[0]}"
minor_version="${split_versions[1]}"
patch_version="${split_versions[2]}"
incremented_patch_version=$((patch_version+1))
echo "::debug::Incremented patch version: ${incremented_patch_version}"
echo "new=${major_version}.${minor_version}.${incremented_patch_version}" >> "$GITHUB_OUTPUT"
