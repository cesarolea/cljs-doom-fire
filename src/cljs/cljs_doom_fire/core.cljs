(ns cljs-doom-fire.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def screen-dimension [320 168])

(defn- initial-state []
  {:pallete [[  7   7   7] ;; 0
             [ 31   7   7] ;; 1
             [ 47  15   7] ;; 2
             [ 71  15   7] ;; 3
             [ 87  23   7] ;; 4
             [103  31   7] ;; 5
             [119  31   7] ;; 6
             [143  39   7] ;; 7
             [159  47   7] ;; 8
             [175  63   7] ;; 9
             [191  71   7] ;; 10
             [199  71   7] ;; 11
             [223  79   7] ;; 12
             [223  87   7] ;; 13
             [223  87   7] ;; 14
             [215  95   7] ;; 15
             [215 103  15] ;; 16
             [207 111  15] ;; 17
             [207 119  15] ;; 18
             [207 127  15] ;; 19
             [207 135  23] ;; 20
             [199 135  23] ;; 21
             [199 143  23] ;; 22
             [199 151  31] ;; 23
             [191 159  31] ;; 24
             [191 159  31] ;; 25
             [191 167  39] ;; 26
             [191 167  39] ;; 27
             [191 175  47] ;; 28
             [183 175  47] ;; 29
             [183 183  47] ;; 30
             [183 183  55] ;; 31
             [207 207 111] ;; 32
             [223 223 159] ;; 33
             [239 239 199] ;; 34
             [255 255 255] ;; 35
             ]
   :fire-height (/ (get screen-dimension 1) 2) ;; maximum fire spread
   :fire-spread 1                              ;; starts at 1 due to the source of the fire line
   :fire [(into [] (repeat (get screen-dimension 0) 35))]
   :pixel-size 1})

(defn- setup
  []
  (q/smooth)
  (q/frame-rate 30)
  (q/pixel-density (q/display-density))
  (initial-state))

(defn- draw-fps
  [state]
  (q/with-fill [255 184 108]
    (q/text "Current FPS: " 10 15)
    (q/text "  Target FPS: " 10 30))

  (q/with-fill [80 250 123]
    (q/text (Math/round (q/current-frame-rate)) 90 15)
    (q/text (q/target-frame-rate) 91 30)))

(defn- draw-fire-line
  "Draws a fire line at the row represented by y-coord"
  [{:keys [pallete pixel-size] :as state} y fire-pixels im]
  (loop [idx 0]
    (when (< idx (get screen-dimension 0))
      (let [[r g b :as pixel-color] (get pallete (get fire-pixels idx))]
        (dotimes [n pixel-size]
          (q/set-pixel im idx (+ y n) (q/color r g b))))
      (recur (inc idx)))))

(defn- draw-fire-lines
  "Repeatedly calls draw-fire-line for each fire line, starting at
  height-2 and ending at fire-height. If a fire line is not present
  it is drawn as 0"
  [{:keys [pallete fire fire-height pixel-size] :as state} im]
  (loop [idx 1 y (- (get screen-dimension 1) 2)]
    (when (< idx fire-height)
      (let [fire-pixels (get fire idx)]
        (when fire-pixels
          (draw-fire-line state y fire-pixels im)))
      (recur (inc idx)
             (- y pixel-size)))))

(defn- draw
  [{:keys [pallete fire] :as state}]
  (apply q/background (get pallete 0))
  (draw-fps state)
  (let [im (apply q/create-image screen-dimension)]
    ;; draw fire source
    (draw-fire-line state (dec (get screen-dimension 1)) (get fire 0) im)
    ;; draw rest of the fire
    (draw-fire-lines state im)
    ;; blit
    (q/update-pixels im)
    (q/image im 0 0)))

(defn- spread-fire-randomized
  "Returns a fire row that is a result of subtracting up to 3 heat levels to the given
  (usually the previous) fire row."
  [fire-pixel]
  (let [random-index (bit-and (Math/round (* (Math/random) 3.0)) 3)
        fire-pixel (- fire-pixel (bit-and random-index 1))]
    (if (neg? fire-pixel) 0 fire-pixel)))

(defn- fire-scramble
  "Takes fire array and returns another fire array with the result of applying
  the given scrambler function to each row, except the first one (the fire source)"
  [fire height scrambler]
  (loop [idx 0 scrambled [(get fire 0)]]
    (if (< idx height)
      (recur (inc idx)
             (conj scrambled (into [] (map scrambler (get fire idx)))))
      scrambled)))

(defn- spread-fire
  "Continuously generates new fire rows until the fire has spread to fire-height.
  Once fire-spread has been reached, continues to scramble the fire rows to simulate
  movement."
  [{:keys [fire-height fire fire-spread] :as state}]
  (if (< fire-spread fire-height)
    (let [fire-pixels (into [] (map spread-fire-randomized (get fire (dec fire-spread))))
          fire-spread (inc fire-spread)]
      (-> state
          (update-in [:fire] #(fire-scramble (conj % fire-pixels) fire-spread spread-fire-randomized))
          (update-in [:fire-spread] (constantly fire-spread))))
    (update-in state [:fire] #(fire-scramble % fire-height spread-fire-randomized))))

(q/defsketch fire
  :host "fire"
  :draw draw
  :setup setup
  :update spread-fire
  :middleware [m/fun-mode]
  :size screen-dimension)
