#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
RELEASE_VERSION=$3
DRY_RUN=$4

echo "::debug:: Executing verify-repositories.sh with parameters:"
echo "::debug:: ENV_FILE: $ENV_FILE"
echo "::debug:: HOME_DIR: $HOME_DIR"
echo "::debug:: RELEASE_VERSION: $RELEASE_VERSION"
echo "::debug:: DRY_RUN: $DRY_RUN"

## TODO remove - Needed locally to make sure ssh is working with passphrase-protected key
#source ./.github/workflows/scripts/common/ensure-ssh.sh
#ensure_ssh

# shellcheck source=../../.env
source "$ENV_FILE"
expected_version=${RELEASE_VERSION}-SNAPSHOT
repositories=$(echo "$RELEASE_REPOSITORIES" | tr -d '[:space:]')
release_branch=${RELEASE_BRANCH_PREFIX}${RELEASE_VERSION}
for value in ${repositories//,/ }
do
  if [[ ! $value =~ "b:" ]]; then
    cd "$HOME_DIR/$(echo "$value" | tr -d /)" || return
    current_branch=$(git rev-parse --abbrev-ref HEAD)
    echo "::debug:: Updating pom versions in $(echo "$value" | tr -d /)"
    git checkout -f "$release_branch"
    poms=$(git ls-files '**pom.xml')

    if [ -z "$poms" ]; then
      echo "::notice::No pom.xml files found in $(echo "$value" | tr -d /). Skip incrementing pom versions."
      continue
    fi

    for pom in $poms
    do
      echo "::debug::Updating: $pom in $value"
      sed -i "s#<\\(osgp.[A-Za-z.-]*\\|shared.\\|smart.meter.[A-Za-z.-]*\\)\\?version>${expected_version}#<\\1version>${RELEASE_VERSION}#g" "$pom"
      git add "$pom"
    done

    git commit -m "Changed to release ${RELEASE_VERSION}" || true
    # shellcheck disable=SC2046
    git push --set-upstream $(if ${DRY_RUN}; then echo "--dry-run"; fi) origin "$release_branch" || true
    status=$(git status 2>&1)
    echo "::debug::$status"
    git checkout "$current_branch"
  fi
done
