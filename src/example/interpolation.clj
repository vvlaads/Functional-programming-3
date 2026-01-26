(ns example.interpolation)

; Сгенерировать координаты x точек, для которых необходимо вычислить промежуточные значения 
(defn generate-x [start-x end-x step x0]
  (if (nil? start-x)
    (range x0 end-x step)
    (rest (range start-x end-x step))))

; Формула линейной интерполяции
(defn linear-y [x0 y0 x1 y1 x]
  (+ y0 (* (- x x0) (/ (- y1 y0) (- x1 x0)))))

; Фабрика алогиритма линейной интерполяция
(defn linear-algorithm []
  (let [last-x (atom nil)]
    (fn [points step]
      (when (>= (count points) 2)
        (let [p0 (nth points (- (count points) 2))
              p1 (last points)
              x0 (:x p0)
              y0 (:y p0)
              x1 (:x p1)
              y1 (:y p1)
              xs (generate-x @last-x x1 step x0)]
          (when-not (= x0 x1)
            (let [result
                  (map (fn [x]
                         {:x x
                          :y (linear-y x0 y0 x1 y1 x)})
                       xs)]
              (when (seq result)
                (reset! last-x (:x (last result))))
              result)))))))

; Разделённые разности для метода Ньютона
(defn divided-diff [points]
  (let [xs (map :x points)
        ys (map :y points)]
    (loop [level 0
           column ys
           result []]
      (if (= level (dec (count points)))
        (conj result (first column))
        (let [next-column
              (map (fn [f1 f0 x1 x0]
                     (/ (- f1 f0) (- x1 x0)))
                   (rest column)
                   (butlast column)
                   (drop (inc level) xs)
                   xs)]
          (recur (inc level)
                 next-column
                 (conj result (first column))))))))

; Формула метода Ньютона
(defn newtone-y [points coeffs x]
  (reduce
   (fn [sum [i c]]
     (let [xs (map :x (subvec points 0 i))
           term (reduce * 1 (map #(- x %) xs))]
       (+ sum (* c term))))
   0
   (map-indexed vector coeffs)))

; Фабрика алгоритма интепроляции методом Ньютона
(defn newtone-algorithm [n]
  (let [last-x (atom nil)]
    (fn [points step]
      (when (>= (count points) n)
        (let [window (subvec points (- (count points) n))
              x0 (:x (first window))
              x1 (:x (last window))
              xs (generate-x @last-x x1 step x0)
              coeffs (divided-diff window)
              result
              (map (fn [x]
                     {:x x
                      :y (newtone-y window coeffs x)})
                   xs)]
          (when (seq result)
            (reset! last-x (:x (last result))))
          result)))))
