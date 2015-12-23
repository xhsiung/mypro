var addon = require('bindings')('addon.node');

var obj = addon.MyObject();

console.log(obj.plusone(1,23));

obj.myCallback(function(data,data2){
	console.log("callback");
	console.log(data);
	console.log(data2);
});
