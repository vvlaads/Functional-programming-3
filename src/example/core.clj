(ns example.core
  (:require [clojure.tools.cli :refer [parse-opts]]))

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

   [nil
    "--newtone"
    "Метод Ньютона"
    :id :newtone
    :default false]

   ["-n"
    nil
    "Количество точек для метода Ньютона"
    :id :newtone-count]])

; Стартовая точка
(defn -main [& args]
  (let [{:keys [errors]} (parse-opts args flags)]
    (if errors
      ((println errors)
       (System/exit 1))
      (println "All good"))))