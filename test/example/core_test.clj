(ns example.core-test
  (:require [clojure.test :refer [deftest is]]
            [example.interpolation :as interp]
            [example.parse :as parse]))

; Вспомогательная функция для проверки результатов интерполяции с указанной точностью
(defn equal-with-accuracy [a b eps]
  (< (Math/abs (- a b)) eps))

; Проверка конвертации строки в точку
(deftest test-parse-point
  (let [{:keys [x y]} (parse/parse-point "1.5 2.5")]
    (is (equal-with-accuracy 1.5 x 1e-9))
    (is (equal-with-accuracy 2.5 y 1e-9)))

  (let [{:keys [x y]} (parse/parse-point "3.1;-5.5")]
    (is (equal-with-accuracy 3.1 x 1e-9))
    (is (equal-with-accuracy -5.5 y 1e-9)))

  (let [{:keys [x y]} (parse/parse-point "-1.3\t1.23")]
    (is (equal-with-accuracy -1.3 x 1e-9))
    (is (equal-with-accuracy 1.23 y 1e-9))))

; Проверка генератора точек
(deftest test-generate-x
  (is (= '(0 1 2 3)
         (interp/generate-x nil 4 1 0)))
  (is (= '(2 3)
         (interp/generate-x 1 4 1 0))))

; Проверка формулы линейной интерполяции
(deftest test-linear-y
  (is (equal-with-accuracy 5.0
                           (interp/linear-y 0 0 10 10 5)
                           1e-9))
  (is (equal-with-accuracy 2.5
                           (interp/linear-y 0 0 4 5 2)
                           1e-9)))

; Проверка разделенных разностей
(deftest test-divided-diff
  (let [points (mapv (fn [x] {:x x
                              :y x}) [0 1 2])
        coeffs (interp/divided-diff points)]
    (is (= 3 (count coeffs)))
    (is (equal-with-accuracy 0 (nth coeffs 0) 1e-9))
    (is (equal-with-accuracy 1 (nth coeffs 1) 1e-9))
    (is (equal-with-accuracy 0 (nth coeffs 2) 1e-9))))

; Проверка формулы метода Ньютона
(deftest test-newtone-y
  (let [points (mapv (fn [x] {:x x
                              :y x}) [0 1 2])
        coeffs (interp/divided-diff points)
        y (interp/newtone-y points coeffs 1.5)]
    (is (equal-with-accuracy 1.5 y 1e-9))))

; Проверка линейной интерполяции через замыкание
(deftest test-linear-algorithm
  (let [points (mapv (fn [x] {:x x
                              :y x}) [0 10])
        algo (interp/linear-algorithm)
        res (algo points 2)  ;; вызываем замыкание с шагом 2
        expected (mapv (fn [x] {:x x
                                :y x}) [0 2 4 6 8])]
    (is (= (count expected) (count res)))
    (doseq [[e r] (map vector expected res)]
      (is (= (:x e) (:x r)))
      (is (equal-with-accuracy (:y e) (:y r) 1e-9)))))

; Проверка метода Ньютона через замыкание
(deftest test-newtone-algorithm
  (let [points (mapv (fn [x] {:x x
                              :y x}) [0 1 2])
        algo (interp/newtone-algorithm 3) ;; создаём замыкание с окном 3 точек
        res (algo points 1)]              ;; вызываем замыкание с шагом 1
    (is (= 2 (count res)))
    (let [[{x1 :x
            y1 :y}
           {x2 :x
            y2 :y}] res]
      (is (= 0 x1))
      (is (= 1 x2))
      (is (equal-with-accuracy 0 y1 1e-9))
      (is (equal-with-accuracy 1 y2 1e-9)))))
