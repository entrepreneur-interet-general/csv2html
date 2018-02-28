(ns csv2html.config
  (:require [aero.core :as aero]))

(def config (aero/read-config "config.edn"))

(defn export-dir []
  (or (System/getenv "CSV2HTML_EXPORTDIR")
      (get-in config [:export-dir])))

(defn max-body []
  (or (read-string (System/getenv "CSV2HTML_MAXBODY"))
      (get-in config [:max-body])))

(defn port []
  (or (read-string (System/getenv "CSV2HTML_PORT"))
      (get-in config [:port])))
