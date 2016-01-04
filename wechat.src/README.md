# WeChat Project

WeChat Project objectives is intergrated  communication for android .

## Installation

cordova  plugin add  https://github.com/xhsiung/wechat.git

## Usage

configure
```config
var conf ={ serverip:"yourserverip" ,                           //connect server  ip
                    port: 0,				        //connect server  port
                    notifyTarget: "tw.com.my.MainActivity",     //main notification  target  MainActivity
                    notifyTitle: "你好嗎",			//show  notification  titile
                    hasVibrate: true,			        //vibrate  open or not
                    hasSound: true,		 	        //sound    open  or  not
                    hasRecieve: false};			        // message   recieved  or not
wechat.configure(conf,false);
```

connect  server initConnect
```initConnect
wechat.initConnect();
```

disconnect server
```
wechat.disConnect();
```

setUnreadRec  function  set  your  unread messages
```
wechat.setUnreadRec(num);
```

include  cordova  device  
```
device.uuid;
```

Sample
```
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Hello World</title>
</head>

<body>
<div class="app">
    <div id="deviceready" class="blink">
        <p class="event listening">Connecting to Device</p>
        <p class="event received">Device is Ready</p>
    </div>
</div>

<button type="button" onclick="initConnect()" >initConnect</button>
<button type="button" onclick="disConnect()" >disConnect</button>
<button type="button" onclick="setUnreadRec(10)" >setUnreadRec(10)</button>
<button type="button" onclick="deviceID()" >deviceID</button>

<script type="text/javascript" src="cordova.js"></script>
<script type="text/javascript" src="js/index.js"></script>

<script>
    //configure
    function initConnect(){
        var conf ={ serverip:"yourservip" ,
                    port: 3001,
                    notifyTarget: "tw.com.my.MainActivity",
                    notifyTitle: "你好嗎",
                    hasVibrate: true,
                    hasSound: true,
                    hasRecieve: false};
        wechat.configure(conf,false);
        wechat.initConnect();
    }

    //disconnect
    function disConnect(){
        wechat.disConnect();
    }

    //setUnreadRec
    function setUnreadRec(num){
        wechat.setUnreadRec(num);
    }

    //getDeviceID
    function deviceID(){
        alert(device.uuid);
    }

</script>

</body>
</html>
```

## Current status

Done  work:
* Auto  Connect  and Reconnect  your Server
* Auto Subscirbe  your  device.uuid   channel
* Always  Service  is  running

## History

* **v1.0.2** : 2014-11-03
    ebus
