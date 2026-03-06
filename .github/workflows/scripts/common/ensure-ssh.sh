#!/bin/bash

ensure_ssh() {
    eval "$(ssh-agent -s)"

    # shellcheck disable=SC2016
    echo 'echo "$SSH_PASSPHRASE"' > askpass.sh
    chmod +x askpass.sh

    export SSH_ASKPASS=$PWD/askpass.sh
    export DISPLAY=:0

    setsid ssh-add ~/.ssh/id_rsa < /dev/null
}
