#!/bin/bash

public=$1
cycle=$2

# ------------------------------------------------------------------------------
# prep files to deploy

mkdir -p $public/favicom
mkdir -p $public/target/scala-2.13
mkdir -p $public/img
cp index.html $public
cp styles.css $public
cp prism.css $public
cp -r favicom/* $public/favicom

if [ $cycle = dev ]
then
  # use ...-fastopt.js
  sed -i '' 's/talestonini-com-opt\.js/talestonini-com-fastopt\.js/g' $public/index.html
  cp target/scala-2.13/talestonini-com-fastopt.js $public/target/scala-2.13
else
  # use ...-opt.js
  cp target/scala-2.13/talestonini-com-opt.js $public/target/scala-2.13
fi

cp -r target/scala-2.13/classes/img/* $public/img

# ------------------------------------------------------------------------------
# firebase files

cp firebase.json $public
