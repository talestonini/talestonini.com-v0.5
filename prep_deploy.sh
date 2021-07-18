#!/bin/bash

public=$1
cycle=$2

# ------------------------------------------------------------------------------
# prep files to deploy

mkdir -p $public/css
mkdir -p $public/favicom
mkdir -p $public/fonts
mkdir -p $public/img
mkdir -p $public/js
mkdir -p $public/target/scala-2.13
cp index.html $public
cp -r target/scala-2.13/classes/css/* $public/css
cp -r target/scala-2.13/classes/favicom/* $public/favicom
cp -r target/scala-2.13/classes/fonts/* $public/fonts
cp -r target/scala-2.13/classes/img/* $public/img
cp -r target/scala-2.13/classes/js/* $public/js

if [ $cycle = dev ]
then
  # use ...-fastopt.js
  sed -i '' 's/talestonini-com-opt\.js/talestonini-com-fastopt\.js/g' $public/index.html
  cp target/scala-2.13/talestonini-com-fastopt.js $public/target/scala-2.13
else
  # use ...-opt.js
  cp target/scala-2.13/talestonini-com-opt.js $public/target/scala-2.13
fi

# ------------------------------------------------------------------------------
# firebase files

cp firebase.json $public
