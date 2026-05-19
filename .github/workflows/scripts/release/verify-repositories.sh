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

error=0
repositories="$(echo "${RELEASE_REPOSITORIES}" | tr -d '[:space:]')"
release_branch="${RELEASE_BRANCH_PREFIX}${RELEASE_VERSION}"

for value in ${repositories//,/ }
do
  if [[ ! ${value} =~ "b:" ]]; then
    cd "${HOME_DIR}/$(echo "${value}" | tr -d /)" || exit 1

    echo "::notice::Verifying that branch not yet exists:${release_branch} in repository ${value}"
    if [ -n "$(git ls-remote --exit-code --heads origin "${release_branch}")" ]; then
      echo "::error::Release branch exists ${release_branch} in repository ${value}"
      error=1
    else
      echo "::notice::Release branch non-existent yet (according to expectation) in repository ${value}."
    fi

    echo "::notice::Checking POM version ${RELEASE_VERSION} in repository ${value}"
    if [ -f pom.xml ]; then
      if grep -q "<version>${RELEASE_VERSION}-SNAPSHOT</version>" pom.xml || grep -1 "<version>${RELEASE_VERSION}</version>" pom.xml; then
        echo "::notice::Expected release found ${RELEASE_VERSION} in repository ${value}"
      else
        echo "::error::Expected release not found ${RELEASE_VERSION} in repository ${value}, found: $(grep '<version>' pom.xml | head -1)"
        error=1
      fi
    else
      echo "::notice::no pom.xml in $PWD"
    fi
  fi
done
if [[ ${error} -gt 0 ]]; then
  echo "::error title=validation errors::Found errors while verifying repositories"
  exit 1
fi
