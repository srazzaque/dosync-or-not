#!/bin/bash -e

file=$1

[[ ! -z $file ]]

cat $file | grep "^\"Elapsed" | sed 's/^.*time: //' | sed 's/ msecs.*$//'
