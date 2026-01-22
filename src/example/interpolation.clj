(ns example.interpolation)

; Получить координату x из точки
(defn get-x [point]
  (first point))

; Получить координату y из точки
(defn get-y [point]
  (last point))

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
  (let [x0 (get-x (first points))
        y0 (get-y (first points))
        x1 (get-x (last points))
        y1 (get-y (last points))
        x-list (generate-x last-x x1 step x0)]
    (if (not= x0 x1)
      (map (fn [x] [x (linear-y x0 y0 x1 y1 x)]) x-list)
      nil)))

; Разделённые разности для метода Ньютона
(defn divided-diff [points]
  (let [xs (map get-x points)
        ys (map get-y points)]
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
     (+ sum (* c (reduce * 1 (map #(- x %) (map get-x (subvec points 0 i)))))))
   0
   (map-indexed vector coeffs)))

; Интерполяция методом Ньютона
(defn newtone-interpolation [points last-x step n]
  (if (< (count points) n)
    nil
    (let [window (subvec points 0 n)
          x0 (get-x (first window))
          x1 (get-x (last window))
          x-list (generate-x last-x x1 step x0)
          coeffs (divided-diff window)]
      (map (fn [x] [x (newtone-y window coeffs x)]) x-list))))
