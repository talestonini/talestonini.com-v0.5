#!/bin/bash

# prep files to deploy
./prep_deploy.sh temp

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
