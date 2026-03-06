#!/bin/bash

ENV_FILE=$1

echo "::debug:: Executing setup-git-identity.sh with parameters:"
echo "::debug:: ENV_FILE: $ENV_FILE"


# TODO remove - Needed locally to make sure ssh is working with passphrase-protected key
source ./.github/workflows/scripts/common/ensure-ssh.sh
ensure_ssh


# shellcheck source=../../.env
source "$ENV_FILE"
git config --global gpg.format ssh
#ssh-add -L > ~/public.key
git config --global user.signingkey ~/public.key
git config --global commit.gpgsign true
git config --global user.name "$GIT_USER"
git config --global user.email "$GIT_EMAIL"
git config --global --add safe.directory "*"
