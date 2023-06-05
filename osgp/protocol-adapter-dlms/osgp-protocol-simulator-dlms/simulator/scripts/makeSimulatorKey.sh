#!/bin/bash

# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

PSQL_USER=osp_admin
PSQL_DB=osgp_adapter_protocol_dlms
TMP_DIR=/tmp/keys

if [ $# -eq 0 ];then
    echo "The first argument must the 'device_identification' that should be used, see..."
	psql --username=$PSQL_USER -d $PSQL_DB -f showDlmsDevices.sql
    exit
fi

echo "- creating/cleaning temporary directory ..."
if [ ! -d $TMP_DIR ];then
	mkdir $TMP_DIR
	chmod o+w $TMP_DIR/
else
	rm -rf $TMP_DIR/*
fi

echo "- preparing sql file to retrieve the security keys ..."
cp retrieveSecurityKeysTemplate.sql $TMP_DIR/retrieveSecurityKeys.sql
cd $TMP_DIR/
sed -i "s/DEVICE_IDENTIFICATION/$1/" retrieveSecurityKeys.sql

echo "- reading security keys from database ..."
psql --username=$PSQL_USER -d $PSQL_DB -f retrieveSecurityKeys.sql

ls -al

echo "- converting keys ..."

xxd -p -r hexauth binauth
openssl enc -d -aes-128-cbc -in binauth -out authkeydecrypted -iv 000102030405060708090a0b0c0d0e0f -K $(xxd -p /etc/ssl/certs/secret.aes|tr -d \\n)
tail -c +17 authkeydecrypted > weg; mv weg authkeydecrypted

xxd -p -r hexenc binenc
openssl enc -d -aes-128-cbc -in binenc -out enckeydecrypted -iv 000102030405060708090a0b0c0d0e0f -K $(xxd -p /etc/ssl/certs/secret.aes|tr -d \\n)
tail -c +17 enckeydecrypted > weg; mv weg enckeydecrypted

xxd -p -r hexmaster binmaster
openssl enc -d -aes-128-cbc -in binmaster -out masterkeydecrypted -iv 000102030405060708090a0b0c0d0e0f -K $(xxd -p /etc/ssl/certs/secret.aes|tr -d \\n)
tail -c +17 masterkeydecrypted > weg; mv weg masterkeydecrypted

echo "- content of $TMP_DIR ..."
ls -al $TMP_DIR/*

echo "- copying generated keys to $HOME/keys ..."
if [ -f $TMP_DIR/authkeydecrypted ]; then cp $TMP_DIR/authkeydecrypted $HOME/keys/authkeydecrypted; fi
if [ -f $TMP_DIR/enckeydecrypted ]; then cp $TMP_DIR/enckeydecrypted $HOME/keys/enckeydecrypted; fi
if [ -f $TMP_DIR/masterkeydecrypted ]; then cp $TMP_DIR/masterkeydecrypted $HOME/keys/masterkeydecrypted; fi

echo "- content of $HOME/keys/* ..."
ls -al $HOME/keys/*

echo "Done."
