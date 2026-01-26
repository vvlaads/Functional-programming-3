(ns example.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [example.interpolation :refer [linear-algorithm newtone-algorithm]]
            [example.parse :refer [parse-point]]))

; Флаги ввода
(def flags
  [["-s"
    "--step STEP"
    "Шаг интерполяции"
    :default 1
    :parse-fn #(Double/parseDouble %)
    :validate [#(> % 0) "Шаг должен быть > 0"]]

   ["-l"
    "--linear"
    "Линейная интерполяция"]

   ["-w"
    "--newtone"
    "Метод Ньютона"]

   ["-n"
    "--newtone-count N"
    "Количество точек для метода Ньютона"
    :default 2
    :parse-fn #(Integer/parseInt %)
    :validate [#(> % 0) "Количество точек должно быть > 0"]]])

; Стартовая точка
(defn -main [& args]
  (let [{:keys [options errors]} (parse-opts args flags)
        step (:step options)]
    (if errors
      (do (println errors)
          (System/exit 1))
      (let [algorithms
            (cond-> []
              (:linear options)
              (conj {:name "linear"
                     :fn (linear-algorithm)})

              (:newtone options)
              (conj {:name "newtone"
                     :fn (newtone-algorithm (:newtone-count options))}))]
        (loop [points []]
          (if-let [line (read-line)]
            (let [pt (parse-point line)
                  points (conj points pt)]
              (doseq [{:keys [name fn]} algorithms
                      :let [out (fn points step)]
                      :when (seq out)]
                (doseq [{:keys [x y]} out]
                  (println name ":" x y)))
              (recur points))
            (println "End of input")))))))