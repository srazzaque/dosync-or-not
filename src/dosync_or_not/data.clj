(ns dosync-or-not.data
  (:require [incanter.core :refer :all]
            [incanter.stats :refer :all]
            [incanter.charts :refer :all]
            [clojure.string :as s]))

(defn- get-stats-from [filename]
  (let [stats-str (-> filename
                  slurp
                  (s/split #"\n"))
        as-numbers (map #(Double/parseDouble %) stats-str)]
    as-numbers))

(defn- get-chart [loop-data atom-data atom-data-1 dosync-data dosync-data-1]
  (let [loop-stats (get-stats-from loop-data)
        atom-stats (get-stats-from atom-data)
        atom-single-thread-stats (get-stats-from atom-data-1)
        dosync-stats (get-stats-from dosync-data)
        dosync-single-thread-stats (get-stats-from dosync-data-1)
        plot (-> (xy-plot (range 0 (count loop-stats))
                          loop-stats
                          :y-label "Elapsed time (ms)"
                          :x-label "Iteration"
                          :series-label "Single thread loop (no ref)"
                          :title "Time to process 1m events\n(lower is better)"
                          :legend true)
                 (add-lines (range 0 (count atom-stats))
                            atom-stats
                            :series-label "atom / 4 threads")
                 (add-lines (range 0 (count atom-single-thread-stats))
                            atom-single-thread-stats
                            :series-label "atom / 1 thread")
                 (add-lines (range 0 (count dosync-stats))
                            dosync-stats
                            :series-label "dosync / 4 threads")
                 (add-lines (range 0 (count dosync-single-thread-stats))
                            dosync-single-thread-stats
                            :series-label "dosync / 1 thread"))]
    (view plot)
    plot))

(defn- save-chart [loop-data atom-data atom-data-1 dosync-data dosync-data-1 png-filename]
  (-> (get-chart loop-data atom-data atom-data-1 dosync-data dosync-data-1)
      (save png-filename
            :width 700
            :height 560)))

(defn- make-chart []
  (save-chart "loop_test.out.trimmed"
              "atom_test.out.trimmed"
              "atom_test_single_thread.out.trimmed"
              "dosync_test.out.trimmed"
              "dosync_test_single_thread.out.trimmed"
              "the_chart.png"))
