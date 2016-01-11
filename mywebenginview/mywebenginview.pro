#-------------------------------------------------
#
# Project created by QtCreator 2016-01-08T13:09:18
#
#-------------------------------------------------

QT       += core gui webenginewidgets   webchannel

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = mywebenginview
TEMPLATE = app


SOURCES += main.cpp\
        mainwindow.cpp \
    htmlpage.cpp

HEADERS  += mainwindow.h \
    htmlpage.h

FORMS    += mainwindow.ui

OTHER_FILES += \
    index.html
