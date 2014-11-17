(ns oraweb.handler
  (:require [oraweb.api :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [ring.util.response :as resp]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
           (GET "/" [] (resp/file-response "index.html" {:root "resources/pgweb/static"}))
           (ANY "/tables" [] (resource
                               :available-media-types ["application/json"]
                               :handle-ok (api-get-tables)
                               ))
           (ANY "/connect" [url] (resource
                                   :available-media-types ["application/json"]
                                   :allowed-methods [:post]
                                   :handle-created (api-do-connect url)
                                   ))
           (ANY "/query" [query] (resource
                                   :available-media-types ["application/json"]
                                   :allowed-methods [:post]
                                   :handle-created (api-do-query query)
                                   ))
           (ANY "/tables/:table" [table] (resource
                                           :available-media-types ["application/json"]
                                           :handle-ok (api-get-table table)
                                           ))
           (ANY "/history" [table] (resource
                                     :available-media-types ["application/json"]
                                     :handle-ok (api-get-history)
                                     ))
           (ANY "/info" [table] (resource
                                  :available-media-types ["application/json"]
                                  :handle-ok (api-get-info)
                                  ))
           (ANY "/tables/:table/indexes" [table] (resource
                                                   :available-media-types ["application/json"]
                                                   :handle-ok (api-get-table-indexes table)
                                                   ))
           (ANY "/tables/:table/info" [table] (resource
                                                :available-media-types ["application/json"]
                                                :handle-ok (api-get-table-info table)
                                                ))
           (route/files "/static" {:root "resources/pgweb/static"})
           (route/not-found "Not Found"))

(def app
  (-> app-routes wrap-params (wrap-trace :header :ui)))

