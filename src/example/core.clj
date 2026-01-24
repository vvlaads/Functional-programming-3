(ns example.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [example.interpolation :refer [linear-interpolation newtone-interpolation]]
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
    :default 1
    :parse-fn #(Integer/parseInt %)
    :validate [#(> % 0) "Количество точек должно быть > 0"]]])

; Вычислить линейной интерполяцией
(defn process-linear [points last-x step]
  (when (>= (count points) 2)
    (linear-interpolation (subvec points (- (count points) 2)) last-x step)))

; Вычислить методом Ньютона
(defn process-newtone [points last-x step n]
  (when (>= (count points) n)
    (newtone-interpolation (subvec points (- (count points) n)) last-x step n)))

; Стартовая точка
(defn -main [& args]
  (let [{:keys [options errors]} (parse-opts args flags)
        step (:step options)
        linear? (:linear options)
        newtone? (:newtone options)
        newtone-n (:newtone-count options)]
    (if errors
      (do (println errors)
          (System/exit 1))
      (loop [points []
             last-lin-x nil
             last-nwt-x nil]
        (let [line (read-line)]
          (if (nil? line)
            (println "End of input")
            (let [pt (parse-point line)
                  points (conj points pt)
                      ;; линейная интерполяция
                  lin-out (when linear?
                            (process-linear points last-lin-x step))
                  last-lin-x (if lin-out (:x (last lin-out)) last-lin-x)
                      ;; метод Ньютона
                  nwt-out (when newtone?
                            (process-newtone points last-nwt-x step newtone-n))
                  last-nwt-x (if nwt-out (:x (last nwt-out)) last-nwt-x)]
              (doseq [{:keys [x y]} (or lin-out [])]
                (println "linear:" x y))
              (doseq [{:keys [x y]} (or nwt-out [])]
                (println "newtone:" x y))
              (recur points last-lin-x last-nwt-x))))))))