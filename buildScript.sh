#!/bin/bash
clear
echo "Trying to remove classes.dex and MyInjectEventApp.jar"
rm classes.dex

echo "Building using ANT"
ant clean dist
echo "dexing classes"
dx --dex --output=classes.dex dist/MyInjectEventApp.jar
echo "adding classes.dex and .classpath to MyInjectEventApp"
aapt add dist/MyInjectEventApp.jar classes.dex
aapt add dist/MyInjectEventApp.jar .classpath
echo "cp MyInjectEventApp to AndroidScreencast folder"
cp -f dist/MyInjectEventApp.jar ../AndroidScreencast/
cp -f dist/MyInjectEventApp.jar ../AndroidScreencast/src
echo "trying to push MyInjectEventApp to /data/local/tmp"
adb push dist/MyInjectEventApp.jar /data/local/tmp
