(ns example.interpolation)

; Сгенерировать координаты x точек, для которых необходимо вычислить промежуточные значения 
(defn generate-x [start-x end-x step x0]
  (if (nil? start-x)
    (range x0 end-x step)
    (rest (range start-x end-x step))))

; Формула линейной интерполяции
(defn linear-y [x0 y0 x1 y1 x]
  (+ y0 (* (- x x0) (/ (- y1 y0) (- x1 x0)))))

; Линейная интерполяция
(defn linear-interpolation [points last-x step]
  (let [x0 (:x (first points))
        y0 (:y (first points))
        x1 (:x (last points))
        y1 (:y (last points))
        x-list (generate-x last-x x1 step x0)]
    (if (not= x0 x1)
      (map (fn [x]
             {:x x
              :y (linear-y x0 y0 x1 y1 x)})
           x-list)
      nil)))

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

; Интерполяция методом Ньютона
(defn newtone-interpolation [points last-x step n]
  (if (< (count points) n)
    nil
    (let [window (subvec points 0 n)
          x0 (:x (first window))
          x1 (:x (last window))
          x-list (generate-x last-x x1 step x0)
          coeffs (divided-diff window)]
      (map (fn [x]
             {:x x
              :y (newtone-y window coeffs x)})
           x-list))))
