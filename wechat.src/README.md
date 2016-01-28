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
                    notifyTitle: "news",			//show  notification  titile
		    notifyTicker: "message",			//show  notification  ticker
                    hasVibrate: true,			        //vibrate  open or not
                    hasSound: true,		 	        //sound    open  or  not
                    hasRecieve: false,			        // message   recieved  or not
                    key: "mykeypair",			        // key auth 
		    hasSaveEl : true,			        //save electricity
		    connErrTimesStop : 5 } ; 			//set connErrTimesStop
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
                    notifyTitle: "news",
		    notifyTicker: "message",
                    key: "mykeypair",
                    hasVibrate: true,
                    hasSound: true,
                    hasRecieve: false,
		    hasSaveEl : true , 		        
		    connErrTimesStop : 5 } 
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

* **v1.2.12** : 2016-01-288
  * fix EBusService.class initConf 
* **v1.2.1** : 2016-01-06
  * add config connErrTimesStop param to stop connect
* **v1.2.0** : 2016-01-04
  * add config hasSaveEl param to save eletricity
* **v1.1.0** : 2014-11-06
  * add config keypar auth
* **v1.0.3** : 2014-11-05
  * add config notifyTicker field
  * ebus.1.0.3.jar     initConnect will subscirbe only.
  * ebus.1.0.3-dev.jar initConnect will unsubscribe and subscribe.
* **v1.0.2** : 2014-11-03 
    ebus
