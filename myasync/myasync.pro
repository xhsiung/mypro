TEMPLATE = app
CONFIG += console
CONFIG -= app_bundle
CONFIG -= qt

SOURCES += \
    myasync.cpp \
    async.cpp

INCLUDEPATH += /home/xdna/Projects/myasync/node_modules/nan
INCLUDEPATH += /usr/local/include/node

HEADERS += \
    async.h
