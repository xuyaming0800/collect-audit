;
var setImg, setData;
(function($) {
	var _img, ishold = false;
	$(function() {
		$("img").on("tap", function(e) {
			if (ishold) {
				ishold = false;
				return;
			}
			_img = $(e.target);
			// 打开相机
			// 调用android本地方法
			javaInterface.callCamera();// 将当前照片的位置传递过去！和是近景还是远景
		}).on(
				"taphold",
				function(e) {// 监听长按事件！
					ishold = true;
					var url = $(e.target).attr("src"); // 获取图片的地址
					var eq = $(e.target).parents(".imgbox").prevAll(".imgbox")
							.size();// 获取点击的位置
					// 将照片的地址传递过去删除！
					javaInterface.DelectFile(url, eq);
					$(e.target).attr("src",
							"file:///android_asset/res/image/btn-paizhao.png");// 如果当前的照片不为空！

				});
		$("#doSave")
				.on(
						"tap",
						function(e) {// 保存按钮的点击事件！
							var isTakephone = true;
							$("img")
									.each(
											function(i, n) {// 判断照片是否全部拍摄，如果没有拍摄告诉用户！
												if ($(n).attr("src") == "file:///android_asset/res/image/btn-paizhao.png") {
													isTakephone = false;
													return false;
												}
											});
							if (!isTakephone) {
								javaInterface.IsTakePhone();
								return;
							}
							// 保存
							var json = $("body").values(true);
							var _jsonStr = JSON.stringify(json);

							if (cellName == "" || intHot == "") {// 对数据判空的操作！
								alert("不能为空哦！");
							} else {
								var findName = $("#findNes").attr("value");// 取出标题的名字
								// 调用android本地保存的方法！
								javaInterface.SaveData(_jsonStr, findName,
										true, collectClassIds);
							}
							return false;
						});
	});

	// 设置照片地址
	setImg = function(url, gpsType) {
		if (_img) {
			_img.attr("src", "file://" + url + "?flag=" + new Date().getTime());
			var _gpsStatus = _img.next("div").find("p").show();
			if (gpsType == 0) {
				$("span[name=mapMarker]", _gpsStatus).css("color", "red");
				$("span[name=label]", _gpsStatus).text("定位失败");
			} else if (gpsType == 1) {
				$("span[name=mapMarker]", _gpsStatus).css("color", "green");
				$("span[name=label]", _gpsStatus).text("定位成功");
			}
		}
	}

	// 设置界面的数据显示！
	setData = function(jsonData) {
		var _extra = jsonData.result.extra;
		for ( var key in _extra) {
			$("*[name=" + key + "]").val(_extra[key]);
		}
	}
})(jQuery);