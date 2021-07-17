#!/bin/bash

sbt fullOptJS
./prep_deploy.sh public prod
firebase deploy
