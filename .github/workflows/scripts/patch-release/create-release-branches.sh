#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
OLD_RELEASE_VERSION=$3
NEW_RELEASE_VERSION=$4

echo "::debug::Executing create-release-branches.sh with parameters:"
echo "::debug::ENV_FILE: ${ENV_FILE}"
echo "::debug::HOME_DIR: ${HOME_DIR}"
echo "::debug::OLD_RELEASE_VERSION: ${OLD_RELEASE_VERSION}"
echo "::debug::NEW_RELEASE_VERSION: ${NEW_RELEASE_VERSION}"

# shellcheck source=../../.env
source "${ENV_FILE}"

repositories="$(echo "${RELEASE_REPOSITORIES}" | tr -d '[:space:]')"
new_release_branch="${RELEASE_BRANCH_PREFIX}${NEW_RELEASE_VERSION}"
old_release_branch="${RELEASE_BRANCH_PREFIX}${OLD_RELEASE_VERSION}"

for value in ${repositories//,/ }
do
  if [[ ! ${value} =~ "b:" ]]; then
    working_dir="${HOME_DIR}/$(echo "${value}" | tr -d /)"
    echo "::debug::Change directory to ${working_dir}"
    cd "${working_dir}" || exit 1

    current_branch="$(git rev-parse --abbrev-ref HEAD)"

    git checkout -f "${old_release_branch}"

    echo "::notice::Creating new release branch ${new_release_branch} in repository ${value}"
    git checkout -b "${new_release_branch}"

    status="$(git status 2>&1)"
    echo "::debug::Git status: ${status}"

    git checkout "${current_branch}"
  fi
done
