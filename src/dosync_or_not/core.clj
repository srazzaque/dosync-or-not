(ns dosync-or-not.core
  (:require [clojure.core.async :refer [go-loop go thread >! <! >!! <!! timeout]])
  (:import java.util.Random)
  (:gen-class))

;; SHARED

(defn make-event-data [^Random r n]
  {:id n
   :data (.nextInt r (inc n))})

;; END SHARED

;; SINGLE THREADED LOOP

(defn process-event
  "In this simplified example, our event is simply assoc'd into the state
   using its :id."
  [state event]
  (assoc state (:id event) event))

(defn loop-test [{:keys [num-events]}]
  (let [r (Random.)]
    (loop [state {}
           n 0]
      (if (< n num-events)
        (let [event (make-event-data r n)
              next-state (process-event state event)]
          (recur next-state (inc n)))))))

;; END SINGLE THREADED LOOP

;; DOSYNC

(defn update-state! [state-ref data]
  (dosync
   (commute state-ref assoc (:id data) data)))

(defn make-worker-thread [state-ref num-events data-offset]
  (let [p (promise)
        r (Random.)]
    (thread
      (loop [n 0]
        (if (< n num-events)
          (let [event-data (make-event-data r (+ n data-offset))]
            (update-state! state-ref event-data)
            (recur (inc n)))
          (deliver p n))))
    p))

(defn dosync-test [{:keys [num-events num-threads]}]
  {:pre [(= 0 (mod num-events num-threads))]}
  (let [state (ref {})
        batch-size (/ num-events num-threads)
        promises (for [n (range 0 num-threads)]
                   (let [offset (* n batch-size)]
                     (make-worker-thread state batch-size offset)))]
    (doall
     (for [p promises] @p))
    nil))

;; END DOSYNC

;; ATOM

(defn update-atom-state! [state-ref data]
  (swap! state-ref assoc (:id data) data))

(defn make-worker-thread-atom [state-ref num-events data-offset]
  (let [p (promise)
        r (Random.)]
    (thread
      (loop [n 0]
        (if (< n num-events)
          (let [event-data (make-event-data r (+ n data-offset))]
            (update-atom-state! state-ref event-data)
            (recur (inc n)))
          (deliver p n))))
    p))

(defn atom-test [{:keys [num-events num-threads]}]
  {:pre [(= 0 (mod num-events num-threads))]}
  (let [state (atom {})
        batch-size (/ num-events num-threads)
        promises (for [n (range 0 num-threads)]
                   (let [offset (* n batch-size)]
                     (make-worker-thread-atom state batch-size offset)))]
    (doall
     (for [p promises] @p))
    nil))

;; END ATOM

(defn run-loop-tests [num-events]
  (println "Running loop tests.")
  (dotimes [_ 100]
    (time
     (loop-test {:num-events num-events}))))

(defn run-dosync-tests [num-events num-threads]
  (println "Running dosync tests.")
  (dotimes [_ 100]
    (time
     (dosync-test {:num-events num-events :num-threads num-threads}))))

(defn run-atom-tests [num-events num-threads]
  (println "Running atom tests.")
  (dotimes [_ 100]
    (time
     (atom-test {:num-events num-events :num-threads num-threads}))))

(defn -main [& args]
  (let [argsv (vec args)
        [test-type num-events num-threads] argsv]
    (condp = test-type
      "loop" (run-loop-tests (Integer/parseInt num-events))
      "dosync" (run-dosync-tests (Integer/parseInt num-events) (Integer/parseInt num-threads))
      "atom" (run-atom-tests (Integer/parseInt num-events) (Integer/parseInt num-threads)))))
