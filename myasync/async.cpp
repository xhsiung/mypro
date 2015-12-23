#include "async.h"
Nan::Persistent<v8::Function>  Async::constructor;
Async::Async()
{
}

 NAN_MODULE_INIT(Async::Init){
        v8::Local<v8::FunctionTemplate>  tpl = Nan::New<v8::FunctionTemplate>(New);
        tpl->SetClassName( Nan::New("AxAsync").ToLocalChecked());
        tpl->InstanceTemplate()->SetInternalFieldCount(1);

        Nan::SetPrototypeMethod(tpl, "start", Start);
        constructor.Reset(tpl->GetFunction());
        Nan::Set(target , Nan::New("AxAsync").ToLocalChecked()  , tpl->GetFunction() );
 }

NAN_METHOD(Async::New){
    if ( info.IsConstructCall()){
        Async *obj = new Async();
        obj->Wrap(info.This() );
        info.GetReturnValue().Set(info.This() );
    }else{
        const unsigned  argc = 1;
        v8::Local<v8::Value> argv[argc] = { info[0]};
        v8::Local<v8::Function> cons =  Nan::New<v8::Function>(constructor);
        info.GetReturnValue().Set( cons->NewInstance(argc ,argv));
    }
}

NAN_METHOD(Async::Start){
       //Async *obj = Async::Unwrap<Async>(info.Holder());
        Nan::Callback   *cb = new Nan::Callback(info[0].As<v8::Function>() );
        Nan::AsyncQueueWorker( new Worker(cb) );
        info.GetReturnValue().SetUndefined();
}

