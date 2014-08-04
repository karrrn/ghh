(ns k-server.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes ANY]]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [hiccup.core :as hiccup]
            [clojure.data.json :as json]
            [net.cgrand.reload :as reload]))


(def sections ["projects", "publications", "cv", "contact"])
(def data (json/read-str (slurp (clojure.java.io/resource "data.json")) :key-fn keyword))



(html/deftemplate base-template "templates/index.html"
  []
  [:head :title] (html/content "karen ullrich")
  [:#projects] (html/clone-for [section sections]
                           [:h1] (html/content section)
                           [:.section](html/set-attr :id section))
  [:#projects :.content] (html/html-content
                          (apply str
                           (map #(hiccup/html
                                  [:img
                                   {:src (str "img/projects/" (:image_name %))
                                    :class "project-thumb img-responsive"}])
                                (:projects data))
                           )))



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
