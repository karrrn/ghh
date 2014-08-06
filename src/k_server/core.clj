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
(def data (json/read-str
           (slurp (clojure.java.io/resource "data.json")) :key-fn keyword))

(defn render-for [el-list html-vec] render-for
  (apply str
         (map #(-> (html-vec %) hiccup/html)
                el-list)
              )
  )

(defn project-thumbs [projects-list]
  (render-for projects-list
              (fn [project]
                [:div {:class "project-thumb" :display "none"}
                  [:h4 {:class "title"} (:title project)]
                  [:a
                   {:href (str "projects/"(:id project))}
                   [:img
                    {:src (str "img/projects/" (:image_name project))
                     :class "img-responsive"}]
                   ]
                ])))

(defn get-nav [sections]
  (render-for sections
              (fn [section]
                [:li [:a
                      {:href (str "#" section)}
                      section]])))

(html/deftemplate base-template "templates/index.html"
  []
  [:head :title] (html/content "karen ullrich")
  [:#projects] (html/clone-for [section sections]
                           [:h1] (html/content section)
                           [:.section](html/set-attr :id section))
  [:#projects :.content] (html/html-content (project-thumbs (:projects data)))
  [:ul.navbar-nav] (html/html-content (get-nav sections)))



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
