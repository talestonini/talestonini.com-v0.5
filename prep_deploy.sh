#!/bin/bash

public=$1

# prep files to deploy
mkdir -p $public/favicom
mkdir -p $public/target/scala-2.13
mkdir -p $public/img
cp index.html $public
cp styles.css $public
cp prism.css $public
cp -r favicom/* $public/favicom
cp target/scala-2.13/talestonini-com-fastopt.js $public/target/scala-2.13
cp -r target/scala-2.13/classes/img/* $public/img

# firebase files
cp firebase.json $public
