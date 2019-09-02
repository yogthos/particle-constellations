(ns particle-constellations.core)

(defn color
  ([dx dy] (color dx dy 1))
  ([dx dy alpha]
   (str "rgb("
        (js/Math.floor (+ dx dy)) ","
        (- 150 (js/Math.floor (* dx dy))) ","
        (js/Math.floor (+ dx dy)) ","
        alpha ")")))

(defn line [ctx threshold dx dy [x1 y1] [x2 y2]]
  (set! (.-lineWidth ctx) (/ (- (* 2 threshold) (+ dx dy)) 50))
  (set! (.-strokeStyle ctx) (color dx dy (/ (+ dx dy) 200)))
  (doto ctx (.beginPath) (.moveTo x1 y1) (.lineTo x2 y2) (.stroke)))

(defn point [ctx particle-size [x y]]
  (set! (.-fillStyle ctx) (color (rand-int 80) (rand-int 80) 0.5))
  (.fillRect ctx (- x (/ particle-size 2)) (- y (/ particle-size 2)) particle-size particle-size))

(defn paint [{:keys [ctx threshold particles particle-size canvas]}]
  (set! (.-fillStyle ctx) "black")
  (.fillRect ctx 0 0 (.-width canvas) (.-height canvas))
  (doseq [[x1 y1 :as p1] particles
          [x2 y2 :as p2] particles]
    (when-not (= p1 p2)
      (let [dx (js/Math.abs (- x2 x1))
            dy (js/Math.abs (- y2 y1))]
        (when (and (< dx threshold) (< dy threshold))
          (point ctx particle-size p1)
          (line ctx threshold dx dy p1 p2))))))

(defn move-particles [particles particle-size canvas]
  (for [[x y vx vy] particles]
    (let [new-x (+ x vx)
          new-y (+ y vy)]
      [new-x
       new-y
       (if (< particle-size new-x (- (.-width canvas) particle-size)) vx (- vx))
       (if (< particle-size new-y (- (.-height canvas) particle-size)) vy (- vy))])))

(defn step [{:keys [particles particle-size canvas] :as state}]
  (paint state)
  (js/window.requestAnimationFrame
   (fn [t]
     (step (update state :particles move-particles particle-size canvas)))))

(defn random-particle [canvas]
  [(* (js/Math.random) (.-width canvas))
   (* (js/Math.random) (.-height canvas))
   ((if (> (rand) 0.5) + -) (js/Math.random))
   ((if (> (rand) 0.5) + -) (js/Math.random))])

(defn init! []
  (let [canvas (js/document.getElementById "canvas")
        ctx    (.getContext canvas "2d")]
    (step
     {:canvas        canvas
      :ctx           ctx
      :particle-size 4
      :threshold     100
      :particles     (take 50 (repeatedly #(random-particle canvas)))})))
