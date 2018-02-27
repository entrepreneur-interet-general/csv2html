(defproject
  csv2html "0.1.0"
  :url "https://github.com/entrepreneur-interet-general/csv2html"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ancient "0.6.14"]
            [lein-kibit "0.1.3"]]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [compojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [ring "1.6.3"]
                 [http-kit "2.2.0"]]
  :description "csv2html: convert plain csv into HTML datatables"
  :min-lein-version "2.0.0"
  :main csv2html.handler
  :profiles {:uberjar {:aot :all}})
