(ns oraweb.api
  (:require [oraweb.sql :as db]
            [clojure.data.json :as json]
            ))

(extend-type java.sql.Timestamp
  json/JSONWriter
  (-write [date out]
  (json/-write (str date) out)))

(defn- wrap-error [^Exception e]
  {:error (.getMessage e)}
  )

(defn api-do-connect [url]
  (if-let [connected (db/do-connect url)]
    connected
    {:error "Cannot connected to database."}
    )
  )

(defn api-do-query [query]
  (try
    (when-let [result (db/do-query query)]
      {:columns (first result)
       :rows (rest result)
       }
      )
    (catch Exception e
      (wrap-error e)
      )
    )
  )
(defn api-get-tables []
  (when-let [result (db/get-tables)]
    result
    )
  )
(defn api-get-table [table]
  (when-let [result (db/get-table table)]
    {:columns (first result)
     :rows (rest result)
     }
    )
  )

(defn api-get-table-indexes [table]
  (when-let [result (db/get-table table)]
    {:columns (first result)
     :rows (rest result)
     }
    )
  )

(defn api-get-table-info [table]
  {:data_size 0 :total_size 0 :index_size 0 :rows_count 0}
  )

(defn- get-info-string [^com.mchange.v2.c3p0.ComboPooledDataSource ds]
  {}
  )

(defn api-get-info []
  (if (nil? @db/pooled-db)
    {:error "not connected"}
    (get-info-string @db/pooled-db)
    )
  )

(defn api-get-history []
  @db/history
  )
