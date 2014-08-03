(ns k-server.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes ANY]]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [net.cgrand.reload :as reload]))


(def sections ["projects", "publications", "cv", "contact"])


(html/deftemplate base-template "templates/index.html"
  []
  [:head :title] (html/content "karen ullrich")
  [:#projects] (html/clone-for [section sections]
                           [:h1] (html/content section)
                           [:.section](html/set-attr :id section)))

(defresource main
  ;; main resource
  :available-media-types ["text/html"]
  :handle-ok (fn [_] (apply str (base-template))))

(defroutes app
  (ANY "/" [] main)
  (ANY "/projects/" [] main)
  (route/resources "/"))

(def handler
  (-> app
      (wrap-params)))

(reload/auto-reload *ns*)
