#!/bin/bash

public=$1

# prep files to deploy
mkdir -p $public/favicom
mkdir -p $public/target/scala-2.12
cp index.html $public
cp styles.css $public
cp -r favicom/* $public/favicom
cp target/scala-2.12/talestonini-com-fastopt.js $public/target/scala-2.12

# firebase files
cp firebase.json $public
