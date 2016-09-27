(ns espdig-www.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [espdig-www.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
