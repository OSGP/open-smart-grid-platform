#!/bin/bash

ENV_FILE=$1

echo "::debug::Executing configure-git.sh with parameters:"
echo "::debug::ENV_FILE: ${ENV_FILE}"

# shellcheck source=../../.env
source "${ENV_FILE}"

git config --global user.name "${GIT_USER}"
git config --global user.email "${GIT_EMAIL}"
git config --global --add safe.directory "*"
git config --global url."https://x-access-token:${TOKEN}@github.com/".insteadOf "https://github.com/"
