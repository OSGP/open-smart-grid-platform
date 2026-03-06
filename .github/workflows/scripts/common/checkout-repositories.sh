#!/bin/bash

ENV_FILE=$1

echo "::debug:: Executing checkout-repositories.sh with parameters:"
echo "::debug:: ENV_FILE: $ENV_FILE"


# TODO remove - Needed locally to make sure ssh is working with passphrase-protected key
source ./.github/workflows/scripts/common/ensure-ssh.sh
ensure_ssh


# shellcheck source=../../.env
source "$ENV_FILE"
mkdir repositories
echo "::debug::Home directory: $PWD/repositories"
home=$PWD/repositories
repositories=$(echo "$RELEASE_REPOSITORIES" | tr -d '[:space:]')
echo "::debug::Using git: $(which git)"
for value in ${repositories//,/ }
do
  if [[ $value =~ "b:" ]]; then
    branch=${value:2}
    echo "::debug::Checking out master branch $branch for $(git config --get remote.origin.url)"
    git checkout "$branch"
  elif [[ ! "$value" =~ ^[[:space:]]*$ ]]; then
    cd "$home" || return
    echo "::debug::Cloning git@github.com:$value.git"
    git clone git@github.com:"$value".git

    repo_name=$(echo "$value" | tr -d /)
    echo "::debug::Moving directory: ${value#*/} to unique name: $repo_name"
    mv "${value#*/}" "$repo_name"
    echo "::debug::Changing directory to repo: $repo_name"
    cd "$repo_name" || return
    git fetch
    status=$(git status 2>&1)
    echo "::debug::$status"
  fi
done
echo "repo_dir=$home" >> "$GITHUB_OUTPUT"
