#!/bin/sh

# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

echo run this script as root!

[ $1 ] || { echo provide \"stop\" or milliseconds; exit 1; }

if [ $1 = "stop" ]
then
  tc qdisc del dev lo root
else
  if [ $1 -gt 0 ]
  then
    tc qdisc add dev lo root handle 1: prio 
    tc qdisc add dev lo parent 1:1 handle 10: netem delay ${1}ms
    tc filter add dev lo protocol ip parent 1: prio 1 u32 match ip dport 4059 0xffff flowid 1:1
  fi
fi

tc -s qdisc show dev lo
