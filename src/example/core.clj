(ns example.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [example.parse :refer [parse-point]]
            [example.interpolation :refer [linear-interpolation]]))

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

   ["-nwt"
    "--newtone"
    "Метод Ньютона"]

   ["-n"
    "--newtone-count N"
    "Количество точек для метода Ньютона"
    :default 1
    :parse-fn #(Integer/parseInt %)
    :validate [#(> % 0) "Количество точек должно быть > 0"]]])

; Стартовая точка
(defn -main [& args]
  (let [{:keys [options errors]} (parse-opts args flags)
        step (:step options)
        linear? (:linear options)]
    (if errors
      (do (println errors)
          (System/exit 1))
      (do (println "Input:")
          (
            ;TODO: linear interpolation
           )))))