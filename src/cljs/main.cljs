(ns hist.core
  (:require [kioo.om :refer [do-> set-attr content html-content]]
            [kioo.core]
            [om.core :as om :refer-macros []]
            [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [ankha.core :as ankha]
            [clojure.string :refer [join split replace lower-case]])
  (:require-macros [kioo.om :refer [defsnippet deftemplate]])
  (:import goog.History))

(declare without-sub with-sub)

(def nested-data
  (atom
    {:1 {:name "Top" :sub [:2 :3]}
     :2 {:name "Top 1"}
     :3 {:name "Top 2"}

     :4 {:name "Section" :sub [:5]}
     :5 {:name "Subsection" :sub [:6 :7]}
     :6 {:name "Item 1"}
     :7 {:name "Item 2"}

     :8 {:name "Simple" :sub [:9]}
     :9 {:name "History"}}))

(def history-state
  (atom
    [[{:1 true}]]))



(defn node-from-key [k data]
  {k (k data)})

(defn subs-from-key [k data]
  (if-let [subs (:sub (k data))]
    (conj (map #(node-from-key % data) subs))))

;; -
;; History Widgets
;; -

(defn concat-path [current full-path]
  (let [before (first (split-with (partial not= current) full-path))
        path (str "#/"
                  (if (empty? before)
                    ""
                    (str (join "/" (map #(name (first (keys %))) before)) "/"))
                  (name (first (keys current)))
                  "/")]
    path))

(defsnippet hist__item "templates/history.html"
  [:.inactive] [data path]
  {[:.hist__item] (content "")})

(defsnippet hist__item--active "templates/history.html"
  [:.active] [data path]
  {[:a] (set-attr :href (concat-path data path)
                  :title (:name ((first (keys data)) @nested-data)))
   [:.hist__node__label] (content (:name ((first (keys data)) @nested-data)))})

(defn check-node [data]
   (map (fn [item]
         (cond
           (first (vals item)) (hist__item--active item data)
           :else (hist__item item data)))
       data))

(defsnippet hist__column "templates/history.html"
            [:.hist__column] [data]
            {[:.hist__column] (content (check-node data))})

(defsnippet hist "templates/history.html"
  [:.hist] [data]
  {[:.hist__inner] (content (map hist__column (reverse data)))})


;; -
;; Build App State UI 'Tree'
;; -

(defsnippet acco2 "templates/accordion.html" [:.accordion] [])

;; -
;; Core App Template
;; -

(deftemplate main "templates/canvas.html"
  [data]
  {[:.acco-holder] (content (acco2))
   [:.jhist-one]   (content (hist data))
   [:.hist-holder] (content (hist data))})


;; -
;; It's all history
;; -

(defn babs [n]
  (cond
    (neg? n) 0
    :else n))

(defn adjust-state [raw-path last-path]
  (loop [i (- (count last-path) 1) items []]
    (if (zero? i)
      (conj items (last last-path))
      (recur (dec i)
             (if (= (keys (nth raw-path (- i 1)))
                    (keys (nth last-path (- i 1))))
               (conj items {(-> last-path (nth (- i 1)) keys first) false})
               (conj items {(-> last-path (nth (- i 1)) keys first) true }))))))

(defn prep-path [raw-path last-path]
  (println "prep-path")
  (if (and (= (keys (first raw-path)) (keys (first last-path)))
           (or (= (- (count raw-path) 1) (count last-path))
               (= (- (count last-path) 1) (count raw-path))
               (= (count last-path) (count raw-path))))
    (adjust-state raw-path last-path)
    raw-path))

(defn cut [depth raw-path last-hist]
  (let [path (prep-path raw-path (last @history-state))
        log (println "cut")]
    (if (not= path raw-path)
      (swap! history-state assoc (babs (- (count last-hist) 1)) path))
    (swap! history-state conj raw-path)))

(defn bump [depth path]
  (let [hist @history-state
        last-depth (- (count (last hist)) 1)
        last-item (last hist)
        log (println "bump")]
    (if (= (keys (nth last-item last-depth))
           (keys (nth path last-depth)))
      (do
        (println "bump A")
        (swap! history-state assoc (babs (- (count hist) 1)) path))
      (do
        (println "bump B")
        (cut depth path hist)))))

(defn mod-path [raw-path]
  (println "mod-path")
  (mapv #(into {} {% true}) raw-path))

(defn cnc [raw-path]
  (let [depth (count raw-path)
        path (mod-path raw-path)
        log (println "cnc")]
    (if (<= depth (count (last @history-state)))
      (cut depth path @history-state)
      (bump depth path))))

;; -
;; It's a rout
;; -

(secretary/set-config! :prefix "#")

(defroute first-path #"((/[a-z0-9-]+)+)/" [path _ query-params]
  (let [params (mapv keyword (rest (split path #"/")))
        print (println params)]
    (if query-params
      (let [items (js/document.querySelectorAll ".hist")]
        (amap items idx ret (set! (.-scrollLeft (aget items idx)) 3000))))
    (cnc params)))

(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (doto h (.setEnabled true)))


;; -
;; App State and OM Init
;; -

(defn init [data] (om/component (main data)))

(om/root init history-state {:target  (. js/document (getElementById "wrapper"))})

(om/root
  ankha/inspector
  history-state
  {:target (js/document.getElementById "example")})

(secretary/dispatch! "/1/2/?first=true")