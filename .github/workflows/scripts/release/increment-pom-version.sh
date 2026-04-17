#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
RELEASE_VERSION=$3
NEW_MINOR_VERSION=$4

echo "::debug:: Executing increment-pom-version.sh with parameters:"
echo "::debug:: ENV_FILE: ${ENV_FILE}"
echo "::debug:: HOME_DIR: ${HOME_DIR}"
echo "::debug:: RELEASE_VERSION: ${RELEASE_VERSION}"
echo "::debug:: NEW_MINOR_VERSION: ${NEW_MINOR_VERSION}"

# shellcheck source=../../.env
source "${ENV_FILE}"

repositories="$(echo "${RELEASE_REPOSITORIES}" | tr -d '[:space:]')"

for value in ${repositories//,/ }
do
  if [[ ! $value =~ "b:" ]]; then
    working_dir="${HOME_DIR}/$(echo "${value}" | tr -d /)"
    cd "${working_dir}" || exit 1
    echo "::notice::Updating pom versions in repository ${value}, default branch"
    echo "::debug::in directory ${working_dir}"

    poms="$(git ls-files '**pom.xml')"

    if [ -z "${poms}" ]; then
      echo "::notice::No pom.xml files found in ${working_dir}. Skip incrementing pom versions."
      continue
    fi

    for pom in ${poms}
    do
      echo "::debug::Updating: ${pom} in ${value}"
      sed -i "s#<\\(osgp.[A-Za-z.-]*\\|shared.\\|smart.meter.[A-Za-z.-]*\\)\\?version>${RELEASE_VERSION}#<\\1version>${NEW_MINOR_VERSION}#g" "${pom}"
      git add "${pom}"
    done

    git commit -m "Adapted version to ${NEW_MINOR_VERSION}" || true

    status="$(git status 2>&1)"
    echo "::debug::Git status: ${status}"
  fi
done
