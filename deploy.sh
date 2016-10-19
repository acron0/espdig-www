#!/usr/bin/env bash
lein clean
lein cljsbuild once min
lein garden once
lein upload-data
