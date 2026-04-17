#!/bin/bash

ENV_FILE=$1

echo "::debug::Executing checkout-repositories.sh with parameters:"
echo "::debug::ENV_FILE: ${ENV_FILE}"

# shellcheck source=../../.env
source "${ENV_FILE}"

mkdir -p repositories
home_dir=$PWD/repositories
echo "::debug::Home directory: ${home_dir}"

repositories="$(echo "${RELEASE_REPOSITORIES}" | tr -d '[:space:]')"
echo "::debug::Using git: $(which git)"

for value in ${repositories//,/ }
do
  if [[ ${value} =~ "b:" ]]; then
    branch=${value:2}
    echo "::debug::Checking out branch $branch for $(git config --get remote.origin.url)"
    git checkout "${branch}"
  elif [[ ! "$value" =~ ^[[:space:]]*$ ]]; then
    cd "${home_dir}" || exit 1
    echo "::debug::Cloning repository: ${value}"
    git clone https://github.com/"${value}".git

    repo_name=$(echo "${value}" | tr -d /)
    echo "::debug::Moving directory: ${value#*/} to unique name: ${repo_name}"
    mv "${value#*/}" "$repo_name"
    echo "::debug::Changing directory to repo: ${repo_name}"
    cd "${repo_name}" || exit 1
    git fetch
    status=$(git status 2>&1)
    echo "::debug::${status}"
  fi
done
echo "repo_dir=${home_dir}" >> "$GITHUB_OUTPUT"
