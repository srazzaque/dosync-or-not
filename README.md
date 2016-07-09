# dosync-or-not

This is the accompanying source code to:

http://blog.sandipan.net/clojure/2016/07/09/clojure-ref.html

## Preparation

To make the uberjar:

$ lein uberjar

## Usage

$ java -jar target/uberjar/dosync-or-not-0.1.0-standalone.jar (loop|dosync|atom) num-events num-threads

## Options

loop|dosync|atom = Type of test being run
num-events = number of events to process
num-threads = number of threads (only takes effect in dosync and atom tests).

NOTE: num-events must be divisible by num-threads.
