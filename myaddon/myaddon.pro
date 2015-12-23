TEMPLATE = app
CONFIG += console
CONFIG -= app_bundle
CONFIG -= qt

SOURCES += \
    addon.cpp \
    myobject.cpp
INCLUDEPATH += /home/xdna/Projects/myaddon/node_modules/nan
INCLUDEPATH += /usr/local/include/node

HEADERS += \
    myobject.h

OTHER_FILES += \
    binding.gyp


