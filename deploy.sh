#!/bin/bash

sbt clean

# tests only pass with the DEV API key
#sbt test

# FIXME: It should perform fullOptJS::webpack.
#sbt fullOptJS::webpack
sbt fastOptJS::webpack

./prep_deploy.sh public prod
firebase deploy
