#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
RELEASE_VERSION=$3

echo "::debug::Executing verify-repositories.sh with parameters:"
echo "::debug::ENV_FILE: ${ENV_FILE}"
echo "::debug::HOME_DIR: ${HOME_DIR}"
echo "::debug::RELEASE_VERSION: ${RELEASE_VERSION}"

# shellcheck source=../../.env
source "${ENV_FILE}"

warning=0
repositories="$(echo "${RELEASE_REPOSITORIES}" | tr -d '[:space:]')"
expected_version="${RELEASE_VERSION}-SNAPSHOT"
release_branch="${RELEASE_BRANCH_PREFIX}${RELEASE_VERSION}"
echo "::notice::Expecting release: ${expected_version} on release branch: ${release_branch}"

for value in ${repositories//,/ }
do
  if [[ ! ${value} =~ "b:" ]]; then
    cd "${HOME_DIR}/$(echo "${value}" | tr -d /)" || exit 1

    current_branch="$(git rev-parse --abbrev-ref HEAD)"

    if [ -z "$(git ls-remote --exit-code --heads origin "${release_branch}")" ]; then
      echo "::error::Source branch doesn't exist ${release_branch} in repository ${value}"
      exit 1
    fi
    git checkout "${release_branch}"

    echo "::notice::Checking POM version ${expected_version} in repository ${value}"
    if [ -f pom.xml ]; then
      if grep -1 "<version>${expected_version}</version>" pom.xml; then
        echo "::notice::Expected release found ${expected_version} in repository ${value}"
      else
        echo "::warning::Expected release not found ${expected_version} in repository ${value}, found: $(grep '<version>' pom.xml | head -1)"
        warning=1
      fi
    else
      echo "::notice::no pom.xml in $PWD"
    fi
    git checkout "${current_branch}"
  fi
done
if [[ ${warning} -gt 0 ]]; then
  echo "::warning:: Found conflicts while verifying repositories, possibly this is a rerun of this pipeline. Continuing."
fi
