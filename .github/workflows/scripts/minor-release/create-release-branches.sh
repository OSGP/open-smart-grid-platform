#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
RELEASE_VERSION=$3

echo "::debug:: Executing create-release-branches.sh with parameters:"
echo "::debug:: ENV_FILE: $ENV_FILE"
echo "::debug:: HOME_DIR: $HOME_DIR"
echo "::debug:: RELEASE_VERSION: $RELEASE_VERSION"

# TODO remove
# Needed locally to make sure ssh is working with passphrase
source ./.github/workflows/scripts/common/ensure-ssh.sh
ensure_ssh


# shellcheck source=../../.env
source "$ENV_FILE"
repositories=$(echo "$RELEASE_REPOSITORIES" | tr -d '[:space:]')
release_branch=${RELEASE_BRANCH_PREFIX}$RELEASE_VERSION
for value in ${repositories//,/ }
do
  if [[ ! $value =~ "b:" ]]; then
    echo "::debug:: Change directory to $HOME_DIR/$(echo "$value" | tr -d /)"
    cd "$HOME_DIR/$(echo "$value" | tr -d /)" || return
    current_branch=$(git rev-parse --abbrev-ref HEAD)
    echo "::notice:: creating new release branch ${release_branch} in $value"
    git checkout -b "${release_branch}"

    status=$(git status 2>&1)
    echo "::debug::$status"
    git checkout "$current_branch"
  fi
done
