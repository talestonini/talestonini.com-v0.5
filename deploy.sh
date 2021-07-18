#!/bin/bash

sbt clean
sbt test
sbt fullOptJS
./prep_deploy.sh public prod
firebase deploy
