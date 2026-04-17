#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
OLD_RELEASE_VERSION=$3
NEW_RELEASE_VERSION=$4

echo "::debug::Executing verify-repositories.sh with parameters:"
echo "::debug::ENV_FILE: ${ENV_FILE}"
echo "::debug::HOME_DIR: ${HOME_DIR}"
echo "::debug::OLD_RELEASE_VERSION: ${OLD_RELEASE_VERSION}"
echo "::debug::NEW_RELEASE_VERSION: ${NEW_RELEASE_VERSION}"

# shellcheck source=../../.env
source "${ENV_FILE}"

error=0
repositories="$(echo "${RELEASE_REPOSITORIES}" | tr -d '[:space:]')"
new_release_branch="${RELEASE_BRANCH_PREFIX}${NEW_RELEASE_VERSION}"
old_release_branch="${RELEASE_BRANCH_PREFIX}${OLD_RELEASE_VERSION}"

for value in ${repositories//,/ }
do
  if [[ ! ${value} =~ "b:" ]]; then
    cd "${HOME_DIR}/$(echo "${value}" | tr -d /)" || exit 1

    current_branch="$(git rev-parse --abbrev-ref HEAD)"

    if [ -z "$(git ls-remote --exit-code --heads origin "${old_release_branch}")" ]; then
      echo "::error::Source branch doesn't exist ${old_release_branch} in repository ${value}"
      exit 1
    fi
    git checkout "${old_release_branch}"

    echo "::notice::Checking branch not exists:${new_release_branch} in repository ${value}"
    if [ -n "$(git ls-remote --exit-code --heads origin "${new_release_branch}")" ]; then
      echo "::error::Release branch exists ${new_release_branch} in repository ${value}"
      error=1
    else
      echo "::notice::Release branch non-existent yet (according to expectation) in repository ${value}."
    fi

    echo "::notice::Checking POM version ${OLD_RELEASE_VERSION} in repository ${value}"
    if [ -f pom.xml ]; then
      if grep -q "<version>${OLD_RELEASE_VERSION}-SNAPSHOT</version>" pom.xml || grep -1 "<version>${OLD_RELEASE_VERSION}</version>" pom.xml; then
        echo "::notice::Expected release found ${OLD_RELEASE_VERSION} in repository ${value}"
      else
        echo "::error::Expected release not found ${OLD_RELEASE_VERSION} in repository ${value}, found: $(grep '<version>' pom.xml | head -1)"
        error=1
      fi
    else
      echo "::notice:: no pom.xml in $PWD"
    fi

    git checkout "${current_branch}"
  fi
done
if [[ ${error} -gt 0 ]]; then
  echo "::error title=validation errors:: Found errors while verifying repositories"
  exit 1
fi
