	
	var exec = require('cordova/exec');
	var WeChat = function() {};
	WeChat.prototype.configure = function (arg0 ,arg1) {
	    cordova.exec(null, null, "WeChat", "configure", [arg0 , arg1]);
	}

	WeChat.prototype.initConnect = function () {
	    cordova.exec(null, null, "WeChat", "initConnect", []);
	}

	WeChat.prototype.disConnect = function () {
	    cordova.exec(null, null, "WeChat", "disConnect", []);
	}
	
	WeChat.prototype.setUnreadRec = function (num) {
	    cordova.exec(null, null, "WeChat", "setUnreadRec", [num]);
	}

	var wechat = new WeChat();
	module.exports = wechat;
