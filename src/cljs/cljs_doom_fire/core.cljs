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
   :fire-pixels []
   :fire-width (get screen-dimension 0)
   :fire-height (get screen-dimension 1)})

(defn- setup
  []
  (q/smooth)
  (q/frame-rate 27)
  (q/pixel-density (q/display-density))
  (let [state (initial-state)
        [width height] screen-dimension]
    (-> state
        (update-in [:fire-pixels]
                   #(reduce (fn [px i]
                              (conj px (if (< i (* (dec height) width)) 0 36)))
                            [] (range (* width height)))))))

(defn- draw-fps
  [state]
  (q/with-fill [255 184 108]
    (q/text "Current FPS: " 10 15)
    (q/text "  Target FPS: " 10 30))

  (q/with-fill [80 250 123]
    (q/text (Math/round (q/current-frame-rate)) 90 15)
    (q/text (q/target-frame-rate) 91 30)))

(defn- draw
  [{:keys [pallete fire] :as state}]
  (draw-fps state))

(q/defsketch fire
  :host "fire"
  :draw draw
  :setup setup
  :middleware [m/fun-mode]
  :size screen-dimension)
