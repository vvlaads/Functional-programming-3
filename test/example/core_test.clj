(ns example.core-test
  (:require [example.core :refer :all]
            [clojure.test :refer [deftest is]]))

(deftest example
  (is (= 2 (example-method))))