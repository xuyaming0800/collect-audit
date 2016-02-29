function Map() {
	this.container = new Object();
}

Map.prototype.put = function(key, value) {
	this.container[key] = value;
}

Map.prototype.get = function(key) {
	return this.container[key];
}

Map.prototype.keySet = function() {
	var keyset = new Array();
	var count = 0;
	for ( var key in this.container) {
		// 跳过object的extend函数
		if (key == 'extend') {
			continue;
		}
		keyset[count] = key;
		count++;
	}
	return keyset;
}

Map.prototype.size = function() {
	var count = 0;
	for ( var key in this.container) {
		// 跳过object的extend函数
		if (key == 'extend') {
			continue;
		}
		count++;
	}
	return count;
}

Map.prototype.remove = function(key) {
	delete this.container[key];
}

//清空map   
Map.prototype.clear = function(){    
 try{    
   delete this.container;    
   this.container = {};    

  }catch(e){    
     return e;    
  }    
};

//判断map中是否包含指定的key   
Map.prototype.containsKey=function(key){   
 
 try{   
     for(var p in this.container)   
     {   
        if(this.p==key)   
           return true;   
     }   
 
     return false;   
   
 }catch(e){   
     return e;   
 }   
 
}   
 
//判断map中是否包含指定的value   
Map.prototype.containsValue = function(value){    
 try{    
 
  for(var p in this.container)   
  {    
    if(this.container[p] === value)    
       return true;    
   }    
 
   return false;    
 
}catch(e){    
   return e;    
}    
};    

Map.prototype.toString = function() {
	var str = "";
	for (var i = 0, keys = this.keySet(), len = keys.length; i < len; i++) {
		str = str + keys[i] + "=" + this.container[keys[i]] + ";\n";
	}
	return str;
}