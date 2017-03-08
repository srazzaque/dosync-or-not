#!/bin/bash

num_events=1000000
num_threads=4
extra_opts="-XX:+PrintCompilation -XX:+PrintGC"

rm *.out
java $extra_opts -jar target/uberjar/dosync-or-not-0.1.0-SNAPSHOT-standalone.jar loop $num_events | tee loop_test.out
java $extra_opts -jar target/uberjar/dosync-or-not-0.1.0-SNAPSHOT-standalone.jar atom $num_events $num_threads | tee atom_test.out
java $extra_opts -jar target/uberjar/dosync-or-not-0.1.0-SNAPSHOT-standalone.jar atom $num_events 1 | tee atom_test_single_thread.out
java $extra_opts -jar target/uberjar/dosync-or-not-0.1.0-SNAPSHOT-standalone.jar dosync $num_events $num_threads | tee dosync_test.out
java $extra_opts -jar target/uberjar/dosync-or-not-0.1.0-SNAPSHOT-standalone.jar dosync $num_events 1 | tee dosync_test_single_thread.out
