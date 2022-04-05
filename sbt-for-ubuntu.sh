#!/bin/bash

export NODE_OPTIONS=--openssl-legacy-provider

Xvfb :1 &
DISPLAY=:1 sbt
