#!/bin/bash

# prep files to deploy
mkdir -p temp/favicom
mkdir -p temp/target/scala-2.12
cp index.html temp
cp styles.css temp
cp -r favicom/* temp/favicom
cp target/scala-2.12/talestonini-com-fastopt.js temp/target/scala-2.12

lftp -c "
#debug;
set ssl:verify-certificate false;
open ftp://u557325774:Lunedi03#@talestonini.com
lcd temp;
cd /domains/talestonini.com/public_html;
mirror --only-newer \
       --ignore-time \
       --reverse \
       --parallel=5 \
       --verbose" || exit $?

rm -rf temp
