#ifndef ASYNC_H
#define ASYNC_H
#include <nan.h>
#include <iostream>
#include <unistd.h>

class Async : public Nan::ObjectWrap
{
public:
    Async();
    static  NAN_MODULE_INIT(Init);
    static  NAN_METHOD(New);
    static  NAN_METHOD(Start);
    static Nan::Persistent<v8::Function>  constructor;
};


class Worker : public Nan::AsyncWorker
{
public:
      Worker(Nan::Callback *callback): Nan::AsyncWorker(callback){}
      ~Worker() {}

    void Execute () {
        while (1){
            usleep(1000);
            std::cout << "start" << std::endl ;
        }
  }
};

#endif // ASYNC_H
