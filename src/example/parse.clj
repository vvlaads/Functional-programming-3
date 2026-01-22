(ns example.parse)

; Преобразуем строку с координатами в пару чисел
(defn parse-point [line]
  (let [[x y] (clojure.string/split line #"[;\s\t]+")]
    [(Double/parseDouble x)
     (Double/parseDouble y)]))
