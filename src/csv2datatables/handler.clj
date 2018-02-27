(ns csv2datatables.handler
  (:gen-class)
  (:require [org.httpkit.server :as http-kit]
            [ring.middleware.reload :as reload]
            [ring.middleware.params :as params]
            [ring.util.response :as response]
            [ring.middleware.multipart-params :as multipart-params]
            [ring.middleware.keyword-params :as keyword-params]
            [compojure.core :as compojure :refer (GET POST defroutes)]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.page :as h]
            [hiccup.element :as he]
            [clojure.java.io :as io]
            [clojure.data.csv :as data-csv]))

(def export-dir "resources/public/exports/")

(defn- home-page []
  (h/html5
   {:lang "fr"}
   [:head
    [:title "csv2datatables : export csv as datatables"]
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    (h/include-css
     "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css")]
   [:body
    [:div {:class "container"}
     [:h1 "Export .csv file to HTML page"]
     [:form
      {:action "/file" :method "post" :enctype "multipart/form-data"}
      [:div {:class "form-group"}
       [:input {:name   "file" :type "file" :size "200" :required true
                :accept "application/csv"}]]
      [:div {:class "form-group"}
       [:input {:type "submit" :value "Export" :class "btn btn-warning btn-lg"}]]]]]))

(defn- to-html [filename csv-data]
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
    [:div {:class "container"}
     [:h1 filename]
     [:br]
     [:table {:id "example" :class "display" :cellspacing "0" :width "100%"}
      [:thead [:tr (for [x (first csv-data)] [:td x])]]
      [:tfoot [:tr (for [x (first csv-data)] [:td x])]]
      [:tbody (for [row (rest csv-data)]
                [:tr (for [field row] [:td field])])]]]
    (h/include-js "https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js")
    [:script
     "$(function(){$(\"#example\").dataTable({\"oLanguage\":{\"sUrl\":\"http://cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/French.json\",},\"iDisplayLength\": 15});})"]]))

(defn- handle-upload [actual-file dest-file dest-dir filename]
  (.mkdir (io/file dest-dir))
  (io/copy actual-file (io/file dest-file))
  (spit (str dest-dir "index.html")
        (to-html filename
                 (data-csv/read-csv (slurp actual-file)))))

(defroutes app-routes
  (GET "/" [] (home-page))
  (POST "/file" [file]
        (let [file-name   (:filename file)
              actual-file (:tempfile file)
              rel-dir     (str (java.util.UUID/randomUUID) "/")
              dest-file   (str export-dir rel-dir file-name)
              dest-dir    (str export-dir rel-dir)]
          (handle-upload actual-file dest-file dest-dir file-name)
          (response/redirect (str "exports/" rel-dir "index.html"))))
  (route/resources "/")
  (route/not-found "404 error"))

(def app (-> app-routes
             reload/wrap-reload
             params/wrap-params
             keyword-params/wrap-keyword-params
             multipart-params/wrap-multipart-params))

(defn -main [& args]
  (http-kit/run-server #'app {:port 8080 :max-body 1000000000}))
