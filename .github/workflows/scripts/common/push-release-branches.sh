#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
RELEASE_VERSION=$3
DRY_RUN=$4

echo "::debug:: Executing push-release-branches.sh with parameters:"
echo "::debug:: ENV_FILE: $ENV_FILE"
echo "::debug:: HOME_DIR: $HOME_DIR"
echo "::debug:: RELEASE_VERSION: $RELEASE_VERSION"
echo "::debug:: DRY_RUN: $DRY_RUN"

# shellcheck source=../../.env
source "$ENV_FILE"
repositories=$(echo "$RELEASE_REPOSITORIES" | tr -d '[:space:]')
release_branch=${RELEASE_BRANCH_PREFIX}$RELEASE_VERSION

for value in ${repositories//,/ }
do
  if [[ ! $value =~ "b:" ]]; then
    cd "$HOME_DIR/$(echo "$value" | tr -d /)" || return
    echo "::notice:: pushing release branch $release_branch"
    echo "::debug:: in $HOME_DIR/$(echo "$value" | tr -d /)"

    status=$(git status 2>&1)
    echo "::debug:: $status"
    git push "$(if $DRY_RUN; then echo "--dry-run"; fi)"
    git push --set-upstream "$(if $DRY_RUN; then echo "--dry-run"; fi)" origin "$release_branch"
  fi
done
