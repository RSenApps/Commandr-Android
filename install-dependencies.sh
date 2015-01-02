#!/bin/bash

# Fix the CircleCI path
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH"

DEPS="$ANDROID_HOME/installed-dependencies"

#Check ENV
echo ANDROID_HOME = $ANDROID_HOME
echo DEPS = $DEPS

echo "List android SDK packages available for installation"
android list sdk -e

# Use android list sdk -e -a in order to get all the available packages on Android SDK Manager

if [ ! -e $DEPS ]; then

  cp -r /usr/local/android-sdk-linux $ANDROID_HOME &&
	export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH" &&
	which android &&
	
  echo y | android update sdk -u -a -t platform-tools &&
  echo y | android update sdk -u -a -t tools &&
  echo y | android update sdk -u -a -t android-21 &&
  echo y | android update sdk -u -a -t build-tools-21.1.2 &&
  echo y | android update sdk -u -a -t extra-google-google_play_services &&
  echo y | android update sdk -u -a -t extra-android-m2repository &&
  echo y | android update sdk -u -a -t extra-android-support &&
  echo y | android update sdk -u -a -t extra-google-m2repository &&

  # DO NOT CREATE EMULATOR FOR THE MOMENT
  # echo n | android create avd -n testing -f -t android-21 &&

  touch $DEPS

fi
