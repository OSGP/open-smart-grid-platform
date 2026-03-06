#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
RELEASE_VERSION=$3

echo "::info:: Executing verify-repositories.sh with parameters:"
echo "::info:: ENV_FILE: $ENV_FILE"
echo "::info:: HOME_DIR: $HOME_DIR"
echo "::info:: RELEASE_VERSION: $RELEASE_VERSION"


# TODO remove
# Needed locally to make sure ssh is working with passphrase
source ../common/ensure-ssh.sh
ensure_ssh


# shellcheck source=../../.env
source "$ENV_FILE"
error=0
old_version=$RELEASE_VERSION
repositories=$(echo "$RELEASE_REPOSITORIES" | tr -d '[:space:]')
release_branch=${RELEASE_BRANCH_PREFIX}$RELEASE_VERSION

for value in ${repositories//,/ }
do
  if [[ ! $value =~ "b:" ]]; then
    cd "$HOME_DIR/$(echo "$value" | tr -d /)" || return

    echo "::notice:: Verifying that branch not yet exists:$release_branch for repo $value"
    if [ -n "$(git ls-remote --exit-code --heads origin "${release_branch}")" ]; then
      echo "::error:: Release branch exists ${release_branch} for repo $value"
      error=1
    else
      echo "::notice:: Release branch non-existent yet (according to expectation) for repo $value."
    fi

    echo "::notice:: Checking POM version $old_version"
    if [ -f pom.xml ]; then
      if grep -q "<version>${old_version}-SNAPSHOT</version>" pom.xml || grep -1 "<version>${old_version}</version>" pom.xml; then
        echo "::notice:: Expected release found ${old_version}"
      else
        echo "::error:: Expected release not found $old_version, found: $(grep '<version>' pom.xml | head -1)"
        error=1
      fi
    else
      echo "::notice:: no pom.xml in $PWD"
    fi
  fi
done
if [[ $error -gt 0 ]]; then
  echo "::error title=validation errors:: Found errors while verifying repositories"
  exit 1
fi