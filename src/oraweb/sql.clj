(ns oraweb.sql
  (:require [clojure.java.jdbc :as j]
            [clojure.string :as str]
            )
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  )

(def history (atom []))
(def db-spec (atom []))
(def pooled-db (atom nil))

(defn connection-pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))] 
    {:datasource cpds}))

(defn db-connection [] @pooled-db)

(defn- strip-jdbc [^String spec]
  (if (.startsWith spec "jdbc:")
    (.substring spec 5)
    spec))

(defn- parse-url [url]
  (let [[left right] (str/split url #"//")]
    (case left
      "oracle:thin:" (let  [[user-pass subname] (str/split right #"@")
                                               [user pass] (str/split user-pass #":")]
                      {:classname "oracle.jdbc.driver.OracleDriver" ; must be in classpath
                      :subprotocol "oracle:thin"
                      :subname (str "@" (first (str/split subname #"\?")))
                      :user user
                      :password pass
                      }
                      )
      nil
      )
    )
  )
(comment
  (parse-url "oracle:thin://meng:hello@127.0.0.1:1521:oracle?sslmode=require")
  (do-connect "oracle:thin://meng:hello@127.0.0.1:1521:oracle?sslmode=require")
  )
(defn do-connect [url]
  (when-let [spec (parse-url url)]
    (swap! db-spec (constantly spec))
    (try
      (when (j/get-connection spec)
        (swap! pooled-db (constantly (connection-pool spec)))
        spec
        )
      (catch Exception e
        {:error (str e)}
        ))))

(defn get-schemas []
  (j/with-db-metadata [metadata (db-connection)]
      (let [schema-info (j/metadata-result (.getSchemas metadata))]
        schema-info
       )))

(defn- get-default-schema []
  (if-let [user (:user @db-spec)]
    (str/upper-case user)
    nil
    )
  )

(defn get-tables
  ([] (get-tables (get-default-schema)))
  ([schema]
   (j/with-db-metadata [metadata (db-connection)]
                       (let [tables (j/metadata-result (.getTables metadata
                                                                       nil schema nil
                                                                       (into-array ["TABLE"])))]
                         (map :table_name tables))))
  )

(defn get-table [table]
  (j/with-db-metadata [metadata (db-connection)]
      (let [table-info
            (j/metadata-result (.getColumns metadata nil nil table nil) :as-arrays? true )]
        table-info
        )))

(defn get-table-content [table]
  (take (j/query (db-connection) ["select * from ?" table] :as-arrays? true) 100)
  )

(defn do-query [query]
  (swap! history conj query)
  (j/query (db-connection) [query] :as-arrays? true)
  )

