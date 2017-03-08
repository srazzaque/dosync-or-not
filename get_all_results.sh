#!/bin/bash

for f in *out
do
    ./extract_stats.sh $f > $f.trimmed
done
