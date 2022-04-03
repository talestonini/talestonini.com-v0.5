#!/bin/bash

public=$1
cycle=$2

rmdir -r $public
mkdir -p $public/css
mkdir -p $public/favicom
mkdir -p $public/fonts
mkdir -p $public/img
mkdir -p $public/js
mkdir -p $public/target/scala-2.13

cp -r target/scala-2.13/classes/css/* $public/css
cp -r target/scala-2.13/classes/favicom/* $public/favicom
cp -r target/scala-2.13/classes/fonts/* $public/fonts
cp -r target/scala-2.13/classes/img/* $public/img
cp -r target/scala-2.13/classes/js/* $public/js
cp -r target/scala-2.13/classes/legal/* $public

cp index.html $public

if [ $cycle = dev ]
then
  # use ...-fastopt.js
  sed -i '' 's/talestonini-com-opt-bundle\.js/talestonini-com-fastopt-bundle\.js/g' $public/index.html
  cp target/scala-2.13/scalajs-bundler/main/talestonini-com-fastopt-bundle.js $public/target/scala-2.13
else
  # use ...-opt.js
  # FIXME:
  # 03 Apr 2022 - Not deploying the fullOptJS bundle because it fails with this error in console:
  #               Uncaught InternalError: too much recursion
  #cp target/scala-2.13/scalajs-bundler/main/talestonini-com-opt-bundle.js $public/target/scala-2.13
  cp target/scala-2.13/scalajs-bundler/main/talestonini-com-fastopt-bundle.js $public/target/scala-2.13
fi

cp firebase.json $public
