(ns k-server.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes ANY]]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [hiccup.core :as hiccup]
            [clojure.data.json :as json]
            [markdown.core :as md]
            [net.cgrand.reload :as reload]))


(def sections ["projects", "publications", "cv", "contact"])
(def data (json/read-str
           (slurp (clojure.java.io/resource "data.json")) :key-fn keyword))

(defn get-md [path]
  (md/md-to-html-string
   (slurp
    (clojure.java.io/resource
     (str "markdown/" path)))))

(defn render-for [el-list html-func]
  (apply str
         (map (fn [[id project]]
                (->
                 (html-func id project)
                 hiccup/html))
                el-list)))

(defn project-thumbs [projects-list]
  (render-for projects-list
              (fn [id project]
                [:div {:class "project-thumb"}
                  [:h4 {:class "title"} (:title project)]
                  [:a
                   {:href (str "projects/" (name id))}
                   [:img
                    {:src (str "/img/projects/" (:image_name project))
                     :class "img-responsive"}]
                   ]
                ])))

(project-thumbs (:projects data))

(map (fn [[k v]] (pr v)) (:projects data))

(defn get-nav [sections]
  (apply str
  (map (fn [section]
         (hiccup/html
            [:li [:a
               {:href (str "#" section)}
               section]])) sections)))


(defn get-contact [contact]
      (let [{:keys [name address phone emails]} contact]
        (hiccup/html
        [:div
          [:span name]
          [:address
           [:span (:name address)]
           [:br]
           [:span (:street address)]
           [:br]
           [:span (:postal address)]
           [:span (:city address)]]
          [:span phone]
          [:br]
           (let [email (emails 1)]
            [:a {:href (str "mailto:" email)} email])
          ])))

(html/deftemplate base-template "templates/index.html"
  []
  [:ul.navbar-nav] (html/html-content (get-nav sections))
  [:#intro] (html/html-content (get-md "about.md"))
  [:#projects] (html/clone-for [section sections]
                           [:h1] (html/content section)
                           [:.section](html/set-attr :id section))
  [:#projects :.content] (html/html-content (project-thumbs (:projects data)))
  [:#publications :.content] (html/html-content (get-md "publications.md"))
  [:#contact :.content] (html/html-content (get-contact (:contact data))))

(html/deftemplate project-template "templates/index.html"
  [id]
  [:.logo] (html/set-attr :href "/")
  [:#main] (html/html-content (hiccup/html
                               [:div (get-md (get-in data [:projects (keyword id) :markdown]))])))

(defresource main
  ;; main resource
  :available-media-types ["text/html"]
  :handle-ok (fn [_] (apply str (base-template))))

(defresource project [req]
  ;; main resource
  :available-media-types ["text/html"]
  :handle-ok (fn [_] (apply str (project-template (get-in req [:route-params :id])))))

(defroutes app
  (ANY "/" [] main)
  (ANY "/projects/:id" [req] project)
  (route/resources "/"))

(def handler
  (-> app
      (wrap-params)))

(reload/auto-reload *ns*)
