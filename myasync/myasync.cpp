#include <nan.h>
#include "async.h"

void InitAll(v8::Local<v8::Object> target ){
       Async::Init(target);
}

NODE_MODULE(myasync , InitAll )
