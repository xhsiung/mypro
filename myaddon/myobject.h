#ifndef MYOBJECT_H
#define MYOBJECT_H
#include <nan.h>

class MyObject : public Nan::ObjectWrap{
public:
    MyObject();
    ~MyObject();
    static NAN_MODULE_INIT(Init);
    static NAN_METHOD(New);
    static NAN_METHOD(PlusOne);
    static NAN_METHOD(MyCallback);
    static Nan::Persistent<v8::Function>  construct;
};

#endif // MYOBJECT_H
