;
(function($) {
	$.widget('dataup.json2tree', {
		options : {
			json : false,
			click : false,
			dblclick : false,
			before : false,
			after : false,
			hides : false	//隐藏项
		},
		_create : function() {
			this._init();
		},
		_init : function() {
			if($(this.element).find("li.json2tree_li").size() == 0)
				this._createTree();
		},
		_setOption : function(key, value) {
			this.options[key] = value;
			return this;
		},
		_destroy : function() {
			$(this.element).empty();
		},
		_createTree : function() {
			var myself = this, args = this.options, el = this.element;
			var html = $("<div></div>");
			var level = 0;
			full(args.json,html);
			function full(jo, html) {
				level++;
				if (jo) {
					var _json2tree_ul = $("<ul></ul>");
					for ( var key in jo) {
						if(typeof (jo[key]) == "function") continue;
						var arrs = args.hides.split(",");
						for ( var key_in in arrs ) {
							if(key==key_in)
								_json2tree_ul = $("<ul style='display:none;'></ul>");
						}
						var _json2tree_li = $("<li></li>").addClass("json2tree_li " + key);
						var _json2tree_li_div = $("<div></div>").addClass("json2tree_li_div");
						var _before = ((args.before&&typeof(args.before=="function"))?args.before(level, key, jo[key]):"");
						var json2tree_li_key = $("<span></span>").addClass("json2tree_li_key").append(key);

						_json2tree_li_div.append(_before);
						_json2tree_li_div.append(json2tree_li_key);
						_json2tree_li.append(_json2tree_li_div);
						
						var _after = ((args.after&&typeof(args.after=="function"))?args.after(level, key, jo[key]):"");
						
						if (typeof (jo[key]) == "object") {
							_json2tree_li_div.append(_after);
							full(jo[key], _json2tree_li);
						} else if (typeof (jo[key]) == "string") {
							var _json2tree_li_value = 
								$("<span></span>").addClass("json2tree_li_value")
								.append("<span>:</span>").append(jo[key]);
							_json2tree_li_div.append(_json2tree_li_value).append(_after);
						}
						
						_json2tree_ul.append(_json2tree_li);
					}
					html.append(_json2tree_ul);
				}
				level--;
			}
			$("li:has(ul:has(*))", html)
			.prepend("<div class='triangle triangle-bottomright'/>")
			.click(function(e) {
				showOrHide(this,e);
			});
			$("li > div.json2tree_li_div", html).click(function(e) {
				e.stopPropagation();
				if (args.click)
					args.click(e);
			}).dblclick(function(e) {
				showOrHide($(this).parent("li"),e);
				if (args.dblclick)
					args.dblclick(this,e);
			});
			function showOrHide(obj ,e){//显示或隐藏下级
				e.stopPropagation();
				if ($(obj).children("ul").is(":hidden")) {
					$(obj).children("div.triangle").addClass("triangle-bottomright").removeClass("triangle-right");
					$(obj).children("ul").slideDown("fast");
				} else {
					$(obj).children("div.triangle").addClass("triangle-right").removeClass("triangle-bottomright");
					$(obj).children("ul").slideUp("fast");
				}
			}
			$(el).append(html);
		}
	});
})(jQuery);
