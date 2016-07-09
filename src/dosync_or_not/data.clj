(ns dosync_or_not.data
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

(defn- get-chart [loop-data atom-data dosync-data]
  (let [stats1 (get-stats-from loop-data)
        stats2 (get-stats-from atom-data)
        stats3 (get-stats-from dosync-data)
        plot (-> (xy-plot (range 0 (count stats1))
                          stats1
                          :y-label "Elapsed time (ms)"
                          :x-label "Iteration"
                          :series-label "Single-threaded loop"
                          :title "Time to process 1m events\n(lower is better)"
                          :legend true)
                 (add-lines (range 0 (count stats2))
                            stats2
                            :series-label "atom / 4 threads")
                 (add-lines (range 0 (count stats3))
                            stats3
                            :series-label "dosync / 4 threads"))]
    (view plot)
    plot))

(defn- save-chart [loop-data atom-data dosync-data png-filename]
  (-> (get-chart loop-data atom-data dosync-data)
      (save png-filename
            :width 700
            :height 560)))
