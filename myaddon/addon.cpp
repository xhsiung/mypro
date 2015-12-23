#include <nan.h>
#include "myobject.h"

void InitAll(v8::Local<v8::Object> target){
    MyObject::Init(target) ;
}

NODE_MODULE(addon , InitAll)
