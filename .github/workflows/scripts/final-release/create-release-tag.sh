#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
RELEASE_VERSION=$3
RELEASE_TAG_MESSAGE=$4
DRY_RUN=$5

echo "::debug:: Executing verify-repositories.sh with parameters:"
echo "::debug:: ENV_FILE: $ENV_FILE"
echo "::debug:: HOME_DIR: $HOME_DIR"
echo "::debug:: RELEASE_VERSION: $RELEASE_VERSION"
echo "::debug:: RELEASE_TAG_MESSAGE: $RELEASE_TAG_MESSAGE"
echo "::debug:: DRY_RUN: $DRY_RUN"

## TODO remove - Needed locally to make sure ssh is working with passphrase-protected key
#source ./.github/workflows/scripts/common/ensure-ssh.sh
#ensure_ssh

# shellcheck source=../../.env
source "$ENV_FILE"
repositories=$(echo "$RELEASE_REPOSITORIES" | tr -d '[:space:]')
release_branch=${RELEASE_BRANCH_PREFIX}${RELEASE_VERSION}
release_tag=${RELEASE_TAG_PREFIX}${RELEASE_VERSION}
for value in ${repositories//,/ }
do
  if [[ ! $value =~ "b:" ]]; then
    cd "$HOME_DIR/$(echo "$value" | tr -d /)" || return
    current_branch=$(git rev-parse --abbrev-ref HEAD)
    git checkout -f "$release_branch"

    echo "::notice::Creating release tag $release_tag in repository $value"
    echo "::debug::With message '${RELEASE_TAG_MESSAGE}'"
    # shellcheck disable=SC2046
    if [ $(git tag -l "$release_tag") ]; then
      echo "::warning::Tag exists, skipping release creation."
      continue
    fi

    git tag -a "$release_tag" -m "${RELEASE_TAG_MESSAGE}"
    # shellcheck disable=SC2046
    git push $(if ${DRY_RUN}; then echo "--dry-run"; fi) --tags

    echo "::notice::Create github release $release_branch in repository $value"
    if ! ${DRY_RUN}; then
      gh release create "$release_tag" --latest=true --repo "$value" --generate-notes --notes-start-tag "$release_tag"
    fi

    status=$(git status 2>&1)
    echo "::debug::$status"
    git checkout "$current_branch"
  fi
done
