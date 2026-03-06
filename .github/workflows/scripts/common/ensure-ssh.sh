#!/bin/bash

ensure_ssh() {
    eval "$(ssh-agent -s)"

    echo "$SSH_PRIVATE_KEY" > private.key
    chmod 600 private.key

    # shellcheck disable=SC2016
    echo 'echo "$SSH_PASSPHRASE"' > askpass.sh
    chmod +x askpass.sh

    export SSH_ASKPASS=$PWD/askpass.sh
    export DISPLAY=:0

    setsid ssh-add private.key < /dev/null

    ssh-add -L > ~/public.key
}
