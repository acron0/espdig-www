(ns espdig-www.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [espdig-www.core-test]))

(doo-tests 'espdig-www.core-test)
