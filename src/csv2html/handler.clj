(ns csv2html.handler
  (:gen-class)
  (:require [org.httpkit.server :as http-kit]
            [ring.middleware.reload :as reload]
            [ring.middleware.params :as params]
            [ring.middleware.file   :as file]
            [ring.util.response :as response]
            [ring.middleware.multipart-params :as multipart-params]
            [ring.middleware.keyword-params :as keyword-params]
            [compojure.core :as compojure :refer (GET POST defroutes)]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.page :as h]
            [hiccup.element :as he]
            [clojure.java.io :as io]
            [clojure.data.csv :as data-csv]
            [csv2html.config :as config]))

(def dummy-captcha
  ["35 + 7" "32 + 10" "84 / 2" "21 * 2" "44 - 2"
   "36 + 6" "31 + 11" "126 / 3" "40 + 2" "46 - 4"
   "29 + 12" "12 + 30" "9 + 33" "14 * 3" "17 + 25"])

(defn- home-page []
  (h/html5
   {:lang "fr"}
   [:head
    [:title "csv2html - convert a .csv file as a HTML page"]
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    (h/include-css
     "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css")]
   [:body
    [:div {:class "container" :style "width:80%"}
     [:h1 "Convert a .csv file to a HTML page"]
     [:form
      {:action "/file" :method "post" :enctype "multipart/form-data"}
      [:div {:class "form-group"}
       [:p (str (rand-nth dummy-captcha) " = ")
        [:input {:name "test-field" :type "text" :size "3" :required true}]]]
      [:div {:class "form-group"}
       [:input {:name   "file" :type "file" :size "200" :required true
                :accept "application/csv"}]]
      [:div {:class "form-group"}
       [:input {:type "submit" :value "Convert" :class "btn btn-warning btn-lg"}]]]]]))

(defn- to-html [test-field filename csv-data]
  (h/html5
   {:lang "fr"}
   [:head
    [:title (:title "Titre")]
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    (h/include-css "https://cdn.datatables.net/1.10.16/css/jquery.dataTables.min.css"
                   "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css")
    (h/include-js "https://code.jquery.com/jquery-1.12.4.js"
                  "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js")]
   [:body
    [:h1 filename]
    [:br]
    [:table {:id "example" :class "display" :cellspacing "0" :width "100%"}
     [:thead [:tr (for [x (first csv-data)] [:td x])]]
     [:tfoot [:tr (for [x (first csv-data)] [:td x])]]
     [:tbody (for [row (rest csv-data)]
               [:tr (for [field row] [:td field])])]]
    (h/include-js "https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js")
    [:script
     "$(function(){$(\"#example\").dataTable({\"oLanguage\":{\"sUrl\":\"http://cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/French.json\",},\"iDisplayLength\": 15});})"]]))

(defn- handle-upload [test-field actual-file dest-file dest-dir filename]
  (.mkdir (io/file dest-dir))
  (io/copy actual-file (io/file dest-file))
  (spit (str dest-dir "index.html")
        (to-html test-field
                 filename
                 (data-csv/read-csv (slurp actual-file)))))

(defroutes app-routes
  (GET "/" [] (home-page))
  (POST "/file" [file test-field]
        (let [file-name   (:filename file)
              actual-file (:tempfile file)
              rel-dir     (str (java.util.UUID/randomUUID) "/")
              dest-file   (str (config/export-dir) rel-dir file-name)
              dest-dir    (str (config/export-dir) rel-dir)]
          (if (not (= test-field "42"))
            (response/redirect "/")
            (do
              (handle-upload test-field actual-file dest-file dest-dir file-name)
              (response/redirect (str rel-dir "index.html"))))))
  (route/resources "/")
  (route/not-found "404 error"))

(def app (-> app-routes
             reload/wrap-reload
             params/wrap-params
             ;; (config/export-dir) should point to an existing directory
             (file/wrap-file (config/export-dir))
             keyword-params/wrap-keyword-params
             multipart-params/wrap-multipart-params))

(defn -main [& args]
  (http-kit/run-server #'app {:port (config/port) :max-body 100000000}))
