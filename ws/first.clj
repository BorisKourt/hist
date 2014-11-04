;; gorilla-repl.fileformat = 1

;; **
;;; # Gorilla REPL
;;; 
;;; Welcome to gorilla :-)
;;; 
;;; Shift + enter evaluates code. Hit ctrl+g twice in quick succession or click the menu icon (upper-right corner) for more commands ...
;;; 
;;; It's a good habit to run each worksheet in its own namespace: feel free to use the declaration we've provided below if you'd like.
;; **

;; @@
(ns billowing-swamp
  (:require [gorilla-plot.core :as plot]
            [clojure.core.async :as y]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(def nested-data
  (atom
    {:1 {:name "Top" :sub [:2 :3]}
     :2 {:name "Topper"}
     :3 {:name "Tipper"}

     :4 {:name "Al" :sub [:5]}
     :5 {:name "All" :sub [:6 :7]}
     :6 {:name "Ally"}
     :7 {:name "Alle"}

     :8 {:name "Ol" :sub [:9]}
     :9 {:name "Oll"}}))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;billowing-swamp/nested-data</span>","value":"#'billowing-swamp/nested-data"}
;; <=

;; @@
(def history-state
  (atom
    [[:1]]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;billowing-swamp/history-state</span>","value":"#'billowing-swamp/history-state"}
;; <=

;; @@
(if (:sub (:4 @nested-data))
  :true
  :false)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-keyword'>:true</span>","value":":true"}
;; <=

;; @@
(defn grabsub [node data]
      (if-let [subv (:sub (node data))]
              subv))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;billowing-swamp/grabsub</span>","value":"#'billowing-swamp/grabsub"}
;; <=

;; @@
(defn babs [n]
      (cond
        (neg? n) 0
        :else n))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;billowing-swamp/babs</span>","value":"#'billowing-swamp/babs"}
;; <=

;; @@
(defn prep-path [raw-path last-path]
  (if (= (first raw-path) (first last-path))
  		(conj (mapv #(if (= %1 %2)
          false
          %1) (drop-last raw-path) (drop-last last-path)) 
        (last raw-path))
    raw-path))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;billowing-swamp/prep-path</span>","value":"#'billowing-swamp/prep-path"}
;; <=

;; @@
(defn cut [depth raw-path]
      (let [path (prep-path raw-path (last @history-state))]
           (swap! history-state conj path)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;billowing-swamp/cut</span>","value":"#'billowing-swamp/cut"}
;; <=

;; @@
(defn bump [depth path]
      (let [last-hist @history-state
            last-depth (- (count (last last-hist)) 1)
            last-item (last last-hist)]
           (if (= (nth last-item last-depth) (nth path last-depth))
             (swap! history-state assoc (babs (- (count last-hist) 1)) path)
             (cut depth path))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;billowing-swamp/bump</span>","value":"#'billowing-swamp/bump"}
;; <=

;; @@
(defn cnc [path]
      (let [depth (count path)]
           (if (< depth (count (last @history-state)))
             (cut depth path)
             (bump depth path))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;billowing-swamp/cnc</span>","value":"#'billowing-swamp/cnc"}
;; <=
