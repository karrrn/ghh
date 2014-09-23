(ns k-server.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes ANY]]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [hiccup.core :as hiccup]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [markdown.core :as md]
            [net.cgrand.reload :as reload]))


(def sections ["ABOUT", "PROJECTS", "PUBLICATIONS", "CV", "CONTACT"])
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
                  [:a.title
                     {:href (str "projects/" (name id))}
                     (:title project)]
                  [:a
                   {:href (str "projects/" (name id))}
                   [:img
                    {:src (str "/img/" (:thumb_image project))
                     :class "img-responsive"}]
                   ]
                ])))

(defn get-nav [sections]
  (apply str
  (map (fn [section]
         (hiccup/html
            [:li [:a
               {:href (str "#" (string/lower-case section))}
               section]])) sections)))


(defn get-contact [contact]
      (let [{:keys [name address phone emails]} contact]
        (hiccup/html
        [:div {:class "contact"}
         [:h1 "CONTACT"]
         [:div {:class "info"}
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
          ]
        [:img {:src "/img/contact.jpg" :class "profile-pic"}]
         ]
         )))

(html/defsnippet cv-template "templates/CV.html" [html/root]
  [])

(html/deftemplate base-template "templates/index.html"
  []
  [:ul.navbar-nav] (html/html-content (get-nav sections))
  [:#projects] (html/clone-for [section sections]
                           [:.section](html/set-attr :id (string/lower-case section)))
  [:#about :.content] (html/html-content (get-md "about.md"))
  [:#projects :.content] (html/html-content (project-thumbs (:projects data)))
  [:#projects :.content] (html/prepend
                          (html/html [:h1 "PROJECTS"]))
  [:#publications :.content] (html/html-content (get-md "publications.md"))
  [:#cv :.content] (html/content (cv-template))
  [:#contact] (html/html-content (get-contact (:contact data))))

(html/deftemplate project-template "templates/index.html"
  [id]
  [:.logo] (html/set-attr :href "/")
  [:#main] (html/html-content
            (let [project (get-in data [:projects (keyword id)])]
                  (hiccup/html [:div {:class (str id " project")}
                                [:img {:src (str "/img/"(:main_image project))
                                       :class "cover"}]
                                [:div (get-md (:markdown project))]]))))

(defresource main
  ;; main resource
  :available-media-types ["text/html"]
  :handle-ok (fn [_] (apply str (base-template))))

(defresource project [req]
  ;; project resource
  :available-media-types ["text/html"]
  :handle-ok (fn [_] (apply str (project-template (get-in req [:route-params :id])))))

(defresource bibtex [req]
  ;; project resource
  :available-media-types ["text"]
  :handle-ok (fn [_] (apply str (slurp (clojure.java.io/resource (str "bibtex/" (get-in req [:route-params :id])))))))

(defroutes app
  (ANY "/" [] main)
  (ANY "/projects/:id" [req] project)
  (ANY "/bibtex/:id" [req] bibtex)
  (route/resources "/"))

(def handler
  (-> app
      (wrap-params)))

(reload/auto-reload *ns*)
