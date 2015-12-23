#include "myobject.h"
#include <iostream>

Nan::Persistent<v8::Function>  MyObject::construct;

MyObject::MyObject()
{
}

MyObject::~MyObject()
{
}

//程式進入點
NAN_MODULE_INIT(MyObject::Init){
    v8::Local<v8::FunctionTemplate> tpl = Nan::New<v8::FunctionTemplate>(New);
    tpl->SetClassName( Nan::New("MyObject").ToLocalChecked());
    tpl->InstanceTemplate()->SetInternalFieldCount(1);

    //prototype function
    Nan::SetPrototypeMethod( tpl, "plusone", PlusOne);
    Nan::SetPrototypeMethod( tpl, "myCallback", MyCallback);

    construct.Reset(tpl->GetFunction() );
    Nan::Set(target , Nan::New("MyObject").ToLocalChecked() , tpl->GetFunction());
}

//初使化
NAN_METHOD(MyObject::New){
    if (info.IsConstructCall()){
        MyObject *obj  = new MyObject();
        obj->Wrap(info.This());
        info.GetReturnValue().Set( info.This());
    }else{
        const int argc = 1;
        v8::Local<v8::Value> argv[argc] = { info[0]};
        v8::Local<v8::Function> cons =  Nan::New<v8::Function>(construct);
        info.GetReturnValue().Set( cons->NewInstance(argc ,argv));
    }
}

//addon.plusone(1.23)
NAN_METHOD(MyObject::PlusOne){
    double  num = info[0]->IsUndefined() ? 0 :  Nan::To<double>(info[0]).FromJust() ;
    MyObject  *obj = ObjectWrap::Unwrap<MyObject>( info.Holder() );
    info.GetReturnValue().Set(  Nan::New(num));
}

//function(data , data2)
NAN_METHOD(MyObject::MyCallback){
    v8::Local<v8::Function> cb= info[0].As<v8::Function>();
    const unsigned argc = 2;
    v8::Local<v8::Value> argv[argc] ={ Nan::New("test").ToLocalChecked() ,
                                                              Nan::New("test2").ToLocalChecked()
                                                            };
    Nan::MakeCallback(Nan::GetCurrentContext()->Global() , cb , argc , argv);
}
