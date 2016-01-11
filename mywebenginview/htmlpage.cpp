#include "htmlpage.h"
#include <QDebug>
HtmlPage::HtmlPage(QWidget *parent) :
   QWebEngineView(parent)
{
    //create js global variables
    channel = new QWebChannel();
    this->page()->setWebChannel( channel );
    channel->registerObject("widget" , this);
    setUrl(QUrl("file:///home/xdna/Projects/mywebenginview/index.html") ) ;
    //setUrl( QUrl("http://localhost/index.html"));
}

//js call funtion  call
void HtmlPage::call(QVariantMap msg)
{
    //qDebug() <<  msg ;
    qDebug() << msg["name"].toString() ;
    qDebug() << msg["mobile"].toString();
    qDebug() << msg["mybool"].toBool() ;
    qDebug() << msg["name"].isValid() ;

    QJsonDocument   jsonDoc = QJsonDocument::fromVariant(msg);
    //call jsavascript  function jsFun
    QString jsFun = "jsFun(" + jsonDoc.toJson()  + " )";
    this->page()->runJavaScript( jsFun );
}
