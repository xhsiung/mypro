#!/bin/bash
PWD=`pwd`
PROj=mychats
rm -rf $PROj
cordova  create $PROj tw.com.my MyChat

cd $PROj 
cordova  platform add android
cordova  plugin add  https://github.com/xhsiung/wechat.git
#cordova  plugin add  ../wechatPlugin
cat platforms/android/AndroidManifest.xml
echo ""
echo ""
echo ""
cat platforms/android/res/xml/config.xml
echo ""
echo ""
echo ""
tree platforms/android/src
cd $PWD
echo ""
