#!/bin/bash

# see https://github.com/scala-js/scala-js-env-selenium#headless-usage

# install xvfb and firefox
apt-get update -y
apt-get install -y xvfb
apt-get install -y firefox

# install geckodriver
export GECKODRIVER_DEST=tools/geckodriver
mkdir -p $GECKODRIVER_DEST
wget https://github.com/mozilla/geckodriver/releases/download/v0.30.0/geckodriver-v0.30.0-linux64.tar.gz -P $GECKODRIVER_DEST
tar -xvzf tools/geckodriver/geckodriver* -C $GECKODRIVER_DEST
chmod +x $GECKODRIVER_DEST/geckodriver
mv $GECKODRIVER_DEST/geckodriver .
