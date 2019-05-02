(ns user
  (:require [figwheel.main.api :as fw-api]
            [mount.core :as mount]
            [cljs-doom-fire.core]))

(defn start [] (mount/start))

(defn stop [] (mount/stop-except))
