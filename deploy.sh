#!/bin/bash

sbt clean

# tests only pass with the DEV API key
#sbt test

# FIXME: It should perform fullOptJS::webpack.
sbt fastOptJS::webpack
#sbt fullOptJS::webpack

./prep_deploy.sh public prod
firebase deploy
