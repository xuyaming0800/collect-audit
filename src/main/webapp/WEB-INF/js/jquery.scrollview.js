// JavaScript Document
;
(function($) {
	$.widget('jquery.scrollview', {
		options : {
			tolerance : 0,// 容差（px）
			scrollInterval : 100,// 滚动间隔（毫秒）
			callback : false,// 回调函数
			lock : 0
		// 锁
		},
		_create : function() {
			var myself = this, args = this.options, el = this.element;
			$(el).scroll(function() {
				if (args.lock == 0) {
					var scrollHeight = $(el).data("scrollHeight");
					if (scrollHeight - $(this).scrollTop() <= args.tolerance) {
						args.lock = 1;
						if (args.callback) {
							args.callback(el, function() {
								myself.calculateScrollHeight();
								args.lock = 0;
							});
						} else {
							setInterval((function() {
								myself.calculateScrollHeight();
								args.lock = 0;
							}), 500);
						}
					}
				}
			});

		},
		_init : function() {
			this.calculateScrollHeight();
		},
		_setOption : function(key, value) {
			this.options[key] = value;
			return this;
		},
		_destroy : function() {

		},
		calculateScrollHeight : function() {
			$(this.element).data(
					"scrollHeight",
					($(this.element).prop("scrollHeight") - $(this.element)
							.height()));
		}
	})
})(jQuery);