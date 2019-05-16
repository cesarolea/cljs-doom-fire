(ns cljs-doom-fire.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def pixel-ratio (.-devicePixelRatio js/window))
(def screen-dimension [(/ 160 pixel-ratio) (/ 84 pixel-ratio)])

(defn- initial-state []
  (let [pixel-row (* (q/display-density) (q/width))
        pixel-count (* pixel-row (* (q/display-density) (q/height)))]
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
     :pixel-count pixel-count
     :pixel-row pixel-row
     :pressed-keys #{}}))

(defn- setup
  []
  (q/smooth)
  (q/frame-rate 27)
  (q/pixel-density (q/display-density))
  (let [state (initial-state)
        canvas (-> js/document (.getElementById "defaultCanvas0"))
        {:keys [pixel-row pixel-count]} state]
    (set! (.-width (.-style canvas)) (str (* (q/width) (* 4 pixel-ratio)) "px"))
    (set! (.-height (.-style canvas)) (str (* (* (q/width) (* 4 pixel-ratio)) 0.64) "px"))
    (update-in state [:fire-pixels]
               #(reduce (fn [px i]
                          (conj px (if (< i (- pixel-count pixel-row)) 0 35)))
                        [] (range pixel-count)))))

(defn- draw-fps
  [state]
  (q/with-fill [255 184 108]
    (q/text "Current FPS: " 10 10)
    (q/text "  Target FPS: " 10 20))

  (q/with-fill [80 250 123]
    (q/text (Math/round (q/current-frame-rate)) 90 10)
    (q/text (q/target-frame-rate) 91 20)))

(defn- draw-pixels
  [{:keys [fire-pixels pallete] :as state}]
  (let [px (q/pixels)  ;; screen pixels
        px-count (* 4 (* (* (q/display-density) (q/width))
                         (* (q/display-density) (q/height))))
        px-row (* 4 (* (q/display-density) (q/width)))
        [width height] screen-dimension]
    (loop [i 0]
      (when (< i px-count)
        (let [pixel-value (get fire-pixels (/ i 4))
              [r g b] (get pallete pixel-value)]
          (aset px (+ i 0) r)
          (aset px (+ i 1) g)
          (aset px (+ i 2) b)
          (aset px (+ i 3) 255))
        (recur (+ i 4))))
    (q/update-pixels)))

(defn- spread-fire-random
  [{:keys [fire-pixels pixel-row] :as state} src]
  (let [pixel (get fire-pixels (+ src pixel-row))
        random-index (bit-and (Math/round (* (Math/random) 3.0)) 3)]
    (cond
      (= pixel 0) [random-index 0]
      (nil? pixel) [random-index (get fire-pixels src)]
      :else [(bit-and random-index 1) (- pixel (bit-and random-index 1))])))

(defn- do-fire
  [{:keys [fire-pixels pixel-count] :as state}]
  (update-in state [:fire-pixels]
             #(reduce (fn [px i]
                        (let [[random-index pixel] (spread-fire-random state i)
                              random-index (Math/abs (- i random-index))]
                          (assoc px random-index pixel)))
                      % (range pixel-count))))

(defn toggle-fire
  [{:keys [fire-pixels pressed-keys pixel-row pixel-count] :as state}]
  (update-in state [:fire-pixels]
             #(reduce (fn [px i] (conj px (if (< i (- pixel-count pixel-row))
                                            (get % i)
                                            (if (contains? pressed-keys "shift") 0 35))))
                      [] (range pixel-count))))

(defn key-pressed
  [{:keys [pressed-keys] :as state} {:keys [key key-code] :as event}]
  (let [pressed-keys (into #{} (conj pressed-keys (.toLowerCase (name key))))]
    (update-in state [:pressed-keys] (constantly pressed-keys))))

(defn key-released
  [{:keys [pressed-keys] :as state} {:keys [key key-code] :as event}]
  (let [pressed-keys (into #{} (disj pressed-keys (.toLowerCase (name key))))]
    (update-in state [:pressed-keys] (constantly pressed-keys))))

(defn- draw
  [state]
  (draw-pixels state)
  #_(draw-fps state))

(q/defsketch fire
  :host "fire"
  :draw draw
  :setup setup
  :update #(-> % toggle-fire do-fire)
  :key-pressed key-pressed
  :key-released key-released
  :middleware [m/fun-mode]
  :size screen-dimension)
