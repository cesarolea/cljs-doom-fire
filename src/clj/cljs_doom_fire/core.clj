(ns cljs-doom-fire.core
    (:require [figwheel.main.api :as fw-api]
              [mount.core :refer [defstate start stop]])
    (:gen-class))

(defstate ^{:on-reload :noop} figwheel
  :start (fw-api/start {:id "dev"
                        :options {:main 'cljs-doom-fire.core}
                        :config {:target-dir "resources"
                                 :watch-dirs ["src/cljs"]
                                 :css-dirs []
                                 :open-url false
                                 :mode :serve}})
  :stop (fw-api/stop "dev"))

(defstate ^{:on-reload :noop} cljs-repl
  :start (fw-api/cljs-repl "dev"))

(defn -main
  "Starts the Figwheel process"
  [& args]
  (start))
