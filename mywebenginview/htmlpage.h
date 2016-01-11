#ifndef HTMLPAGE_H
#define HTMLPAGE_H
#include <QWebEngineView>
#include <QtWebChannel/QtWebChannel>
#include <QJsonDocument>

class HtmlPage : public QWebEngineView
{
    Q_OBJECT

private:
    QWebChannel *channel ;

public:
    explicit HtmlPage(QWidget *parent = 0);

public slots:
    void  call(QVariantMap  msg);
};

#endif // HTMLPAGE_H
