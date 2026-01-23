(ns example.parse
  (:require [clojure.string :refer [split]]))

; Преобразуем строку с координатами в пару чисел
(defn parse-point [line]
  (let [[x y] (split line #"[;\s\t]+")]
    [(Double/parseDouble x)
     (Double/parseDouble y)]))
