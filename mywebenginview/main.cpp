#include "mainwindow.h"
#include <QApplication>

int main(int argc, char *argv[])
{

#ifdef QT_DEBUG
    qputenv("QTWEBENGINE_REMOTE_DEBUGGING", "23654");
#endif

    QApplication a(argc, argv);
    MainWindow w;
    w.show();

    return a.exec();
}
