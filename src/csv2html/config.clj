(ns csv2html.config
  (:require [aero.core :as aero]))

(def config (aero/read-config "config.edn"))

(defn export-dir []
  (get-in config [:secrets :export-dir]))

(defn port []
  (get-in config [:secrets :port]))
