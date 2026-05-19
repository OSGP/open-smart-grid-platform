#!/bin/bash

ENV_FILE=$1
HOME_DIR=$2
RELEASE_VERSION=$3
DRY_RUN=$4

echo "::debug::Executing update-release-version.sh with parameters:"
echo "::debug::ENV_FILE: ${ENV_FILE}"
echo "::debug::HOME_DIR: ${HOME_DIR}"
echo "::debug::RELEASE_VERSION: ${RELEASE_VERSION}"
echo "::debug::DRY_RUN: ${DRY_RUN}"

# shellcheck source=../../.env
source "${ENV_FILE}"

expected_version="${RELEASE_VERSION}-SNAPSHOT"
repositories="$(echo "${RELEASE_REPOSITORIES}" | tr -d '[:space:]')"
release_branch="${RELEASE_BRANCH_PREFIX}${RELEASE_VERSION}"

for value in ${repositories//,/ }
do
  if [[ ! ${value} =~ "b:" ]]; then
    working_dir="${HOME_DIR}/$(echo "${value}" | tr -d /)"
    cd "${working_dir}" || exit 1

    current_branch="$(git rev-parse --abbrev-ref HEAD)"

    echo "::notice::Updating pom versions in repository ${value}, branch ${release_branch}"
    echo "::debug::in directory ${working_dir}"

    git checkout -f "${release_branch}"

    poms="$(git ls-files '**pom.xml')"

    if [ -z "${poms}" ]; then
      echo "::notice::No pom.xml files found in ${working_dir}. Skip incrementing pom versions."
      continue
    fi

    for pom in ${poms}
    do
      echo "::debug::Updating: ${pom} in ${value}"
      sed -i "s#<\\(osgp.[A-Za-z.-]*\\|shared.\\|smart.meter.[A-Za-z.-]*\\)\\?version>${expected_version}#<\\1version>${RELEASE_VERSION}#g" "${pom}"
      git add "${pom}"
    done

    git commit -m "Changed to release ${RELEASE_VERSION}" || true

    if [[ "${DRY_RUN}" == "true" ]]; then
      git push --set-upstream --dry-run origin "${release_branch}" || true
    else
      git push --set-upstream origin "${release_branch}" || true
    fi

    status="$(git status 2>&1)"
    echo "::debug::Git status: ${status}"

    git checkout "${current_branch}"
  fi
done
