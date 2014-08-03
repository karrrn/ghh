(defproject k-server "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [liberator "0.10.0"]
                 [compojure "1.1.3"]
		             [ring/ring-core "1.2.1"]
                 [enlive "1.1.5"]
                 [hiccup "1.0.5"]
                 [markdown-clj "0.9.47"]
                 [ring/ring-jetty-adapter "1.1.0"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler k-server.core/handler
         :auto-reload? true})
