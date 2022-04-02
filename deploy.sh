#!/bin/bash

sbt clean
# tests only pass with the DEV API key
#sbt test
sbt fullOptJS
./prep_deploy.sh public prod
firebase deploy
