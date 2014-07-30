(ns hist.core
  (:require [kioo.om :refer [content set-attr do->]]
            [kioo.core]
            [om.core :as om :include-macros true]
            [secretary.core :as secretary :include-macros true :refer [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [ankha.core :as ankha]
            [clojure.string :refer [join split replace lower-case]])
  (:require-macros [kioo.om :refer [defsnippet deftemplate]])
  (:import goog.History))

(declare without-sub with-sub)

(def app-state 
  (atom 
    {:nodes {
             "Top" {
                    "Terms" {
                             "Root Causes" {
                                            "Cause 1" nil
                                            "Cause 2" nil
                                            "Cause 3" nil}
                             "Nail" {
                                     "Cause 1" nil
                                     "Cause 2" nil
                                     "Cause 3" nil}
                             "Blur" {
                                     "Cause 1" nil
                                     "Cause 2" nil
                                     "Cause 3" nil}
                             "Shampoo" {
                                        "Cause 1" nil
                                        "Cause 2" nil
                                        "Cause 3" nil}
                             "Product" {
                                        "Cause 1" nil
                                        "Cause 2" nil
                                        "Cause 3" nil}
                             "Strip" {
                                      "Cause 1" nil
                                      "Cause 2" nil
                                      "Cause 3" nil}
                             "Hair" {
                                     "Cause 1" nil
                                     "Cause 2" nil
                                     "Cause 3" nil}}
                    "Top 10 Themes" nil
                    "Gender" {
                              "Male" nil
                              "Female" nil}
                    "Emotion" nil
                    "Sentiment" {
                                 "Postive" nil
                                 "Neutral" nil
                                 "Negatvive" nil}

                    "Data Source" {
                                   "Twitter" nil
                                   "Facebook" nil
                                   "LinkedIn" nil
                                   "Klour" nil
                                   }
                    }}
     :hist []
     :depth 1}))


;; -
;; History Widgets
;; -

(defn concat-path [current full-path]
  (let [before (first (split-with (partial not= current) full-path))
        path (str "#/" 
                  (if (empty? before)
                        ""
                        (str (join "/" before) "/")) 
                  current 
                  "/")]
    path))

(defsnippet hist__item "templates/history.html" 
  [:.hist__item] [data path])

(defsnippet hist__item--active "templates/history.html" 
  [:.active] [data path]
  {[:a]           (set-attr :href (concat-path data path) :title data)
   [:.hist__node__label] (content data)})

(defsnippet hist__column "templates/history.html" 
  [:.hist__column] [data]
  {[:.hist__column] (content (map #(hist__item--active % data) data))})

(defsnippet hist "templates/history.html" 
  [:.hist] [data]
  {[:.hist__inner] (content (map hist__column (reverse data)))})


;; -
;; Build App State UI 'Tree' 
;; -

(defn process-path [title] 
  (replace (lower-case title) #" " "-"))

(defn get-subs [[ky children] prev-path]
  (let [path (if prev-path 
               (str prev-path (process-path ky) "/")
               (str (process-path ky) "/"))]
   (if (empty? children)
      (without-sub ky nil path)
      (with-sub ky children path))))

(defsnippet with-sub "templates/accordion.html" [:.with-sub] [ky children path]
   {[:li :> :a] (do-> (content ky)
                      (set-attr :href (str "#/" path)))
    [:li :> :ul] (content (map #(get-subs % path) children))})


(defsnippet without-sub "templates/accordion.html" [:.without-sub] [ky _ path]
   {[:li :> :a] (do-> (content ky)
                      (set-attr :href (str "#/" path)))})

(defsnippet acco "templates/accordion.html" [:.accordion] [data]
   {[:div :> :ul] (content (map #(get-subs % nil) data))})


;; -
;; Core App Template
;; -

(deftemplate main "templates/canvas.html"
  [data]
  {[:.acco-holder] (content (acco (:nodes data)))
   [:.hist-holder] (content (hist (:hist data)))})


;; -
;; It's all history
;; - 


(defn babs [n]
  (cond
   (neg? n) 0
   :else n))

(defn slice [depth path]
  (let [last-hist (:hist @app-state)
        current-hist (conj last-hist path)]
    (swap! app-state assoc :depth depth)
    (swap! app-state conj {:hist current-hist})))

(defn bump [depth path]
  (let [last-hist (:hist @app-state)
        last-depth (- (count (last last-hist)) 1)
        last-hist-last (last last-hist)
        current-hist (assoc last-hist (babs (- (count last-hist) 1)) path)]
    (if (= (nth last-hist-last last-depth) (nth path last-depth))
      (do (swap! app-state assoc :depth depth)
          (swap! app-state conj {:hist current-hist}))
      (slice depth path))))

(defn cnc [path]
  (let [depth (count path)]
    (if (<= depth (:depth @app-state))
      (slice depth path)
      (bump depth path))))

;; -
;; It's a rout
;; -

(secretary/set-config! :prefix "#")

(defroute first-path #"((/[a-z0-9-]+)+)/" [path _ query-params]
  (let [params (rest (split path #"/"))]
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

(om/root init app-state {:target  (. js/document (getElementById "wrapper"))})

(om/root
 ankha/inspector
 app-state
 {:target (js/document.getElementById "example")})

(secretary/dispatch! "/top/?first=true")
