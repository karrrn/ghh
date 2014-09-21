(defproject k-server "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [liberator "0.10.0"]
                 [compojure "1.1.3"]
		             [ring/ring-core "1.3.1"]
                 [enlive "1.1.5"]
                 [markdown-clj "0.9.47"]
                 [ring/ring-jetty-adapter "1.3.1"]
                 [prone "0.6.0"]]
  :plugins [[lein-ring "0.7.0"]]
  :ring {:handler k-server.core/handler
         :resource-path ["resources"]
         :auto-reload? true
         :auto-refresh? true})
