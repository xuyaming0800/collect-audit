;
(function($) {
	$.widget('mongolia.mongrid', {
		options : {
			title : false,
			hidetable : false,
			striped : true, // apply odd even stripes
			tbar : false,
			colModel : [],
			dataSource : false,
			bbar : false,
			hideColumn : false,
			resizable : true, // allow table resizing
			syncResizable : true, 
			dd : true,
			minwidth : 30, // min width of columns
			minheight : 80, // min height of columns
			width : 450,
			height : 300,
			isRadio : false,
			usepager : true,
			pageNum : 10,
			rowNum : 10,
			rowList : [ 10, 20, 30, 50, 100 ],
			sortname : false,
			sortorder : false,
			autoload : true,
			onClick : false,
			onDblclick : false,
			onDataLoad : false,
			onKeydown : false,
			onKeyup : false,
			onKeypress : false,
			onScroll : false,
			subGrid : false,
			treeGrid : false,
			treeGridType : "synchronous",//synchronous or asynchronous
			treeGridCache : false,
			treeGridRowNum : 10,
			treeGridNextPage : "点击显示剩余结果",
		},
		_create : function() {
			var args = this.options;
			if(args.title) {
				this._buildhead();
			}
			this._buildbody();
			if(args.tbar) {
				this._buildtbar();
			}
			if(args.bbar) {
				this._buildbbar();
			}
			this._buildfooter();
		},
		_init : function() {
			var myself = this, args = this.options,el = this.element;
			if($.inArray(args.rowNum, args.rowList) == -1){
				alert('rowNum不存在于rowList中,请修改');
				return false;
			}else{				
				el.data('rowNum',args.rowNum);
			}
			BrowserDetect.init();
		},
		_setOption : function(key, value) {
			this.options[key] = value;
			return this;
		},
		_destroy : function() {

		},
		_f_overflowY : 25,
		_f_subgird_width : 25,
		_f_treegrid_indent : 15,
		//head
		_buildhead : function() {
			var myself = this, args = this.options,el = this.element;
			var _head = $('<div class="mongrid-head"><span class="mongrid-head-title">'+args.title+'</span></div>').width(args.width);
			if(args.hidetable){
				_head.append($('<div class="ui-state-default ui-corner-all mongrid-head-hidetable"><span class="ui-icon ui-icon-triangle-1-s"></span></div>')
					.click(function () {
						$('span.ui-icon',this).toggleClass("ui-icon-triangle-1-s").toggleClass("ui-icon-triangle-1-w");
						var showedbody = $('div.mongrid-body:visible',el);
						var hidedbody = $('div.mongrid-body:hidden',el);
						if(showedbody.size() > 0){
							showedbody.slideUp();
						} else {
							hidedbody.slideDown();
						}
					}
				));
			}
			el.append(_head);
		},
		// body部分
		_buildbody : function() {
			var myself = this, args = this.options,el = this.element;
			var rowwidth = 0;
			var _body = $('<div class="mongrid-body"><div class="mongrid-body-thead-table"><div class="mongrid-body-thead"></div></div><div class="mongrid-body-tbody-table"><div class="mongrid-body-tbody"></div></div></div>')
			.width(args.width);
			var _theadtable = _body.find('.mongrid-body-thead-table').width(args.width);
			var _tbodytable = _body.find('.mongrid-body-tbody-table').width(args.width).height(args.height)
			.scroll(function (e){
				$(e.target).prev('.mongrid-body-thead-table').scrollLeft($(e.target).scrollLeft());
				if(args.onScroll){
					args.onScroll(e);
				}
			});
			
			var _thead = _body.find('div.mongrid-body-thead');
			var _tbody = _body.find('div.mongrid-body-tbody');
			
			//初始化表头
			$.each(args.colModel,function(i,col){
				var thead_col = $('<div class="mongrid-body-thead-col" sortable="'+ col.sortable 
						+ '" dd="' + col.dd 
						+ '" coltype="' + col.type 
						+ '" axis="' + col.name 
						+ '" style="width:' + col.width
						+ 'px;" sortorder="' + (col.name == args.sortname ? args.sortorder : 'asc') + '" title="' + col.display 
						+ '"><div class="mongrid-body-thead-col-display">' + col.display + '</div></div>')
						.css('text-align',col.align)
						.mousedown(function(){
							$(this).addClass('mongrid-body-thead-col-active');
						})
						.mouseup(function(){
							$(this).removeClass('mongrid-body-thead-col-active');
						});
				if(col.hide){
					thead_col
					.attr("hide",true)
					.hide();
				}
				//子表格
				var subGrid_col = null;
				if(args.subGrid && i==0){
					subGrid_col = $('<div class="mongrid-body-thead-col mongrid-body-thead-subgrid"></div>').width(myself._f_subgird_width);
					_thead.append(subGrid_col);
				}
				//排序小箭头
				var sortArrow = $('<div class="mongrid-body-thead-col-sort"></div>').prependTo(thead_col);
				thead_col.data('col',col);
				if(col.name == args.sortname) {
					thead_col.addClass('mongrid-body-thead-col-hover');
					if(args.sortorder == 'asc')
						sortArrow.addClass('mongrid-body-thead-col-sort-asc');
					else if(args.sortorder == 'desc')
						sortArrow.addClass('mongrid-body-thead-col-sort-desc');
					//当thead_col加载完成后，再计算其位置
					thead_col.ready(function(){
						sortArrow.position({
							of: thead_col,
							my: 'center top',
							at: 'center top'
						});
					});
				}
				
				var isresize = false;
				if(col.sortable){//排序
					thead_col.click(function(e){
						if($(e.target).hasClass('ui-resizable-handle')) return;
						var _theadCol = $(this);
						if(isresize){
							isresize = false;
							return;
						}
						if(!el.data('data')) return;
						var _sortname = _theadCol.attr('axis');
						var _sortorder = _theadCol.attr('sortorder');
						var sortArrow = _theadCol.find('div.mongrid-body-thead-col-sort');
						
						_theadCol.siblings('div.mongrid-body-thead-col').each(function(){
							$(this).find('div.mongrid-body-thead-col-sort')
							.removeClass('mongrid-body-thead-col-sort-asc')
							.removeClass('mongrid-body-thead-col-sort-desc');
						}).removeClass('mongrid-body-thead-col-hover');
						_theadCol.addClass('mongrid-body-thead-col-hover');
						if(_sortorder == 'asc'){
							sortArrow
							.removeClass('mongrid-body-thead-col-sort-asc')
							.addClass('mongrid-body-thead-col-sort-desc')
							.position({
								of: thead_col,
								my: 'center top',
								at: 'center top'
							});
							$(this).attr('sortorder','desc');
							_sortorder = 'desc';
						} else if(_sortorder == 'desc'){
							sortArrow
							.addClass('mongrid-body-thead-col-sort-asc')
							.removeClass('mongrid-body-thead-col-sort-desc')
							.position({
								of: thead_col,
								my: 'center top',
								at: 'center top'
							});
							$(this).attr('sortorder','asc');
							_sortorder = 'asc';
						}
						_tbody.empty();
						var p = el.data('p');
						p = $.extend(p, {
							//start: p.start,
							//limit: p.limit,
							sortname: _sortname,
							sortorder: _sortorder
						});
						myself._getdata(p, function(data){
							if(data){
								var rows = data['rows'];
								myself._adddata(_body,_thead,_tbody,rows);
							}
						});
						el.data('sortname', _sortname).data('sortorder', _sortorder);
					});
				}
				if(args.resizable){//调整列宽
					thead_col.resizable({
						handles: 'e',
						minWidth:col.width,
						resize: function( event, ui ) {
							myself._t_buildTableWidth(_thead, _tbody);
							if(args.syncResizable){
								$('div[axis='+col.name+']',_tbody).width($(ui.element).width());
							}
						},
						stop: function( event, ui ) {
							isresize = true;
							if(!args.syncResizable){
								$('div[axis='+col.name+']',_tbody).width($(ui.element).width());
							}
						}
					});
				}
				if(args.hideColumn){//隐藏列
					//菜单
					var colMenu = _theadtable.find('ul.mongrid-body-thead-col-menu');
					if(colMenu.size() == 0)
						colMenu = $('<ul class="mongrid-body-thead-col-menu"></ul>')
						.hide()
						.mouseleave(function(e){
							var _colMenu = $(this);
							$( document ).not(_colMenu).one( "click", function(evt) {
								_colMenu.hide();
							});
						})
						.appendTo(_theadtable);
					$('<li><input type="checkbox" checked id="' + col.name + '"/>&nbsp;'+col.display+'</li>')
					.click(function(event){
						event.stopPropagation();
						var _target = $(event.target), theadAndtbody = $('.mongrid-body-thead-table,.mongrid-body-tbody-table', _body);
						if(_target.is(':checkbox')){//如果直接点击checkbox
							var checkboxId = _target.attr('id');
							if(_target.prop('checked')){
								theadAndtbody.find('div[axis="'+checkboxId+'"]').attr('hide',true).show("slow", function(){
									myself._t_buildTableWidth(_thead, _tbody);
								});
							} else {
								theadAndtbody.find('div[axis="'+checkboxId+'"]').attr('hide',false).hide("slow", function(){
									myself._t_buildTableWidth(_thead, _tbody);
								});
							}
						} else {//如果点击文字
							if($(this).find(':checkbox').prop('checked')){
								var checkboxId = $(this).find(':checkbox').prop('checked',false).attr('id');
								theadAndtbody.find('div[axis="'+checkboxId+'"]').attr('hide',true).hide("slow", function(){
									myself._t_buildTableWidth(_thead, _tbody);
								});
							} else {
								var checkboxId = $(this).find(':checkbox').prop('checked',true).attr('id');
								theadAndtbody.find('div[axis="'+checkboxId+'"]').attr('hide',false).show("slow", function(){
									myself._t_buildTableWidth(_thead, _tbody);
								});
							}
						}
					})
					.mousedown(function(e){
						e.stopPropagation();
						$(this).addClass('mongrid-body-thead-col-active');
					})
					.mouseup(function(e){
						e.stopPropagation();
						$(this).removeClass('mongrid-body-thead-col-active');
					})
					.appendTo(colMenu);
					
					//菜单小箭头
					var menuArrow = 
					$('<div class="mongrid-body-thead-col-menu-button">'
					+ '<div class="mongrid-body-thead-col-menu-button-border"></div>' 
					+ '<div class="mongrid-body-thead-col-menu-button-arrow"></div>' 
					+ '</div>')
					.hide()
					.click(function(e){
						e.stopPropagation();
						var cur_thead_col = $(e.target).parents('div.mongrid-body-thead-col');
						$(this).show().position({
							of: cur_thead_col,
							my: 'right top',
							at: 'right top'
						});
						colMenu.hide().slideDown().position({
							of: this,
							my: 'left top',
							at: 'left bottom'
						});
					})
					.mousedown(function(e){
						e.stopPropagation();
						$(this).addClass('mongrid-body-thead-col-active');
					})
					.mouseup(function(e){
						e.stopPropagation();
						$(this).removeClass('mongrid-body-thead-col-active');
					})
					.appendTo(thead_col);
					thead_col.hover(function() {
						menuArrow.show().position({
							of: this,
							my: 'right top',
							at: 'right top'
						});
					}, function() {
						menuArrow.hide();
					});
					
					//默认隐藏
					if(col.hide){
						colMenu.find(':checkbox[id="'+col.name+'"]').prop('checked',false);
					}
				}
				_thead.append(thead_col);
				
				//计算宽度
				rowwidth += col.hide ? 0 : col.width;
				if(args.subGrid && i==0){
					rowwidth += myself._f_subgird_width;
				}
			});
			var _theadWidth = rowwidth + myself._f_overflowY;
			if(_theadWidth < args.width)
				_theadWidth = args.width;
			_thead.width(_theadWidth);
			_tbody.width(rowwidth);
			if(args.dd) {//拖拽列
				$(_thead).sortable({
					cursor: 'move',
					axis: 'x',
					cancel:'div[dd=false]',
					stop: function(event, ui){
						var rows = new Array();
						$('div.mongrid-body-tbody-row',_tbody).each(function() {
							rows.push($(this).data('row'));
                        });
						_tbody.empty();
						myself._adddata(_body,_thead,_tbody,rows);
					}
				});
			}
			
			//分页条
			if(args.usepager) this._buildpager(_body,_thead,_tbody);
			
			//初始化表格内容
			if(args.dataSource && args.autoload){
				el.data('sortname', args.sortname).data('sortorder', args.sortorder);
				var p = el.data('p');
				p = $.extend(p, {
					start: 0,
					limit: el.data('rowNum') ? el.data('rowNum') : args.rowNum,
					sortname: args.sortname,
					sortorder: args.sortorder
				});
				myself._getdata(p, function(data){
					if(data){
						var rows = data['rows'];
						el.data('pageGroupNum', 1);
						myself._adddata(_body,_thead,_tbody,rows);
					}
				});
			}
						
			//将编译好的_body插入el
			el.append(_body);
		},
		//向表格添加数据
		_adddata: function(_body,_thead,_tbody,rows){
			var myself = this, args = this.options,el = this.element;
			$.each(rows,function(i,row){
				myself._addrow(_body,_thead,_tbody,row);
			});
			myself._striped(_tbody);
			if(args.usepager) myself._addpager(_body,_thead,_tbody);//分页条数据
			if(args.onDataLoad) args.onDataLoad(el);
		},
		//添加一行
		_addrow : function(_body,_thead,_tbody,row,pLevelCode){
			var myself = this, args = this.options,el = this.element;
			var rowdiv = $('<div class="mongrid-body-tbody-row"></div>')
			.attr({
				axis : row.id,
				levelCode : row.id
			})
			.data('row',row)
			.click(function(e){
				if(args.isRadio){
					$('.mongrid-body-tbody-row', el).removeClass('mongrid-body-tbody-row-active');
					if($(e.target).hasClass('mongrid-body-tbody-row'))
						$(e.target).addClass('mongrid-body-tbody-row-active');
					else
						$(e.target).parent('.mongrid-body-tbody-row').addClass('mongrid-body-tbody-row-active');
				}else{
					if($(e.target).hasClass('mongrid-body-tbody-row'))
						$(e.target).toggleClass('mongrid-body-tbody-row-active');
					else
						$(e.target).parent('.mongrid-body-tbody-row').toggleClass('mongrid-body-tbody-row-active');
				}
				if(args.onClick){
					args.onClick(this,row,e);
				}
			}); 
			if(_tbody.hasClass('mongrid-body-tbody')){//如果_tbody是真正的tbody,则直接内部插到最后
				_tbody.append(rowdiv);
			} else if(args.treeGrid && args.treeGridType == 'asynchronous'){
				//如果是树表格且是异步的，_tbody是当前行对象，新加入的行需要跟在当前行的后面
				_tbody.after(rowdiv);
			}
			if(args.onDblclick){
				rowdiv.dblclick(function(e){
					args.onDblclick(rowdiv,row,e);
				});
			}
			//子表格
			if(args.subGrid){
				if($('.mongrid-body-tbody-subgird',rowdiv).size == 0){
					$('<div class="mongrid-body-tbody-col"><div class="mongrid-body-tbody-subgird mongrid-body-tbody-subgird-collapse"></div></div>')
					.width(myself._getWidth($('div.mongrid-body-thead-subgird',_thead)))
					.click(function(){
						args.subGrid(rowdiv,row);
					}).prependTo(rowdiv);
				}
			}
			$('div.mongrid-body-thead-col',_thead).each(function(i){
				var thead_col = $(this);
				var col = thead_col.data('col');
				var colname = thead_col.attr('axis');
				var coltype = thead_col.attr('coltype');
				var colvalue = row.cell[colname];
				if (coltype == "other") {
					colvalue = args.colModel[i].text;
				}
				colvalue = (colvalue && colvalue != null) ? colvalue : '';
				var coldiv = $('<div class="mongrid-body-tbody-col"></div>')
				.attr({
					axis : colname,
					title : colvalue
				})
				.append(colvalue)
				.width(myself._getWidth(thead_col))
				.css('text-align',thead_col.css('text-align'))
				.appendTo(rowdiv);
				if(col && col.ready){
					col.ready(coldiv,row);
				}
				//if($(this).is(':hidden')){
				if($(this).attr('hide') && $(this).attr('hide') == true){
					coldiv.hide();
				}
				//树表格
				if(args.treeGrid && (args.colModel[i] && args.colModel[i].treeGridCol)){
					var cLevelCode = row.id + '';
					if(pLevelCode){//计算级别码
						cLevelCode = pLevelCode + ',' + row.id;
						rowdiv.attr('levelCode',cLevelCode);
						rowdiv.hide();
					}
					var level = (pLevelCode ? pLevelCode.split(',').length : 0);//级别
					coldiv.css('padding-left',(level * myself._f_treegrid_indent) + 'px');//计算缩进
					//闭包方法-为树表格添加事件
					function __addEvent(clickHandler){
						//闭包方法-显示孩子
						function showChildren(){
							$('.mongrid-body-tbody-row[levelCode^="' + cLevelCode + '"]',el).each(function(){
								if($(this).attr('levelCode').split(',').length == ((cLevelCode ? cLevelCode.split(',').length : 0) + 1)){
									$(this)
									.show('fast');
								}
							});
						}
						coldiv
						.prepend('<div class="mongrid-body-tbody-treegrid mongrid-body-tbody-treegrid-collapse"></div>')
						.click(function(e){
							var treegridButton = $(this).find('.mongrid-body-tbody-treegrid')
							.toggleClass("mongrid-body-tbody-treegrid-collapse")
							.toggleClass("mongrid-body-tbody-treegrid-open");
							if(treegridButton.hasClass('mongrid-body-tbody-treegrid-open')){
								if(clickHandler){//异步调用的情况
									if(args.treeGridCache){//是否缓存结果
										if($('.mongrid-body-tbody-row[levelCode^="' + cLevelCode + '"]',el).size() <= 1){
											clickHandler(e,showChildren);//再添加新的行
										}
									} else {
										//先删除上一次添加的
										$('.mongrid-body-tbody-row[levelCode^="' + cLevelCode + '"]',el).each(function(){
											if($(this).attr('levelCode') != cLevelCode){//不包括自己
												$(this)
												.remove();
											}
										});
										clickHandler(e,showChildren);//再添加新的行
									}
								}
							}else if(treegridButton.hasClass('mongrid-body-tbody-treegrid-collapse')){
								if(clickHandler){//如果是异步调用,要删除分页条
									$('ul.mongrid-pagination[levelCode^="' + cLevelCode + ',"]',el)
									.next('br').remove()
									.end().remove();
								}
								//隐藏孩子
								$('.mongrid-body-tbody-row[levelCode^="' + cLevelCode + '"]',el).each(function(){
									if($(this).attr('levelCode') != cLevelCode){
										$(this)
										.hide('fast')
										.find('.mongrid-body-tbody-treegrid')
										.addClass("mongrid-body-tbody-treegrid-collapse")
										.removeClass("mongrid-body-tbody-treegrid-open");
									}
								});
							}
						});
						if(!pLevelCode) plevelCode = row.id + '';
					}
					if(args.treeGridType == 'asynchronous'){//如果是异步加载
						//为树表格添加点击事件
						__addEvent(function(e,showChildren){
							var _pageNum = rowdiv.data('pageNum');//页号
							if(!_pageNum) _pageNum = 1;
							var currentCols = $(e.target).parents('.mongrid-body-tbody-row');//当前行
							//分页条
							var _pagination = $('<ul class="mongrid-pagination"></ul>')
							.attr({
								levelCode : currentCols.attr('levelCode') + ',page'
							})
							.css('margin-left',((level + 1) * myself._f_treegrid_indent) + 'px');
							//上一页按钮
							$('<li title="上一页"><span>&#8249;上一页</span></li>')
							.click(function(e){
								_pageNum = _pageNum - 1;
								if(_pageNum < 1)_pageNum = 1;
								asynRemoveAndAddData(currentCols,_pageNum);//先删除当前数据，再添加
							})
							.appendTo(_pagination);
							//下一页按钮
							$('<li title="下一页"><span>下一页&#8250;</span></li>')
							.click(function(e){
								_pageNum = _pageNum + 1;
								asynRemoveAndAddData(currentCols,_pageNum);//先删除当前数据，再添加
							})
							.appendTo(_pagination);
							
							currentCols.after('<br/>').after(_pagination);//插入到当前行后面
							asynRemoveAndAddData(currentCols,_pageNum);//先删除当前数据，再添加
							
							//先删除当前数据，再添加
							function asynRemoveAndAddData(currentCols,_pageNum){
								currentCols.data('pageNum',_pageNum);
								//删除当前数据
								$('.mongrid-body-tbody-row[levelCode^="' + currentCols.attr('axis') + ',"]',el).remove();
								//查询后递归添加子表格
								args.dataSource({
									pid:row.id,
									pageNum:_pageNum
								},function(children){
									$.each(children,function(ci,crow){
										myself._addrow(_body,_thead,rowdiv,crow,cLevelCode);
									});
									showChildren();
								});
							}
						});
					} else if((args.treeGridType || args.treeGridType == 'synchronous') && row.children){//同步一次性返回
						__addEvent();//为树表格添加事件
						//递归添加子表格
						$.each(row.children,function(ci,crow){
							myself._addrow(_body,_thead,_tbody,crow,cLevelCode);
						});
					}

					
				}
			});

			if(args.onKeydown){
				rowdiv.keydown(function(e){
					args.onKeydown(e);
				});
			}
			if(args.onKeyup){
				rowdiv.keyup(function(e){
					args.onKeyup(e);
				});
			}
			if(args.onKeypress){
				rowdiv.keypress(function(e){
					args.onKeypress(e);
				});
			}
		},
		//斑马纹
		_striped : function(_tbody){
			var myself = this, args = this.options,el = this.element;
			if(args.striped){//斑马纹
				$(".mongrid-body-tbody-row",_tbody)
				.removeClass('mongrid-body-tbody-row-odd')
				.removeClass('mongrid-body-tbody-row-even')
				.each(function(i,n){
					if((i + 1) % 2 == 1){
						$(n).addClass('mongrid-body-tbody-row-odd');
					}else{
						$(n).addClass('mongrid-body-tbody-row-even');
					}
				});
			}
		},
		// 从后台获取数据的通用方法
		_getdata : function(p,fn) {
			var myself = this, args = this.options,el = this.element;
			args.dataSource(p,function(data){
				el
				.data('p', p)
				.data('data', data)
				.data('total',data.total);
				fn(data);
			});
		},
		//分页条部分
		_buildpager : function(_body,_thead,_tbody) {
			var myself = this, args = this.options,el = this.element;
			var pd = $('<div class="mongrid-pagination-div"></div>').width(args.width);
			var pagination = $('<ul class="mongrid-pagination"></ul>');
			var _first = $('<li name="first" class="notremove" title="转到第一页"><span>&laquo;</span></li>')
			.click(function(e){
				e.stopPropagation();
				el.data('pageGroupNum',1);
				myself.__gotopage(_body,_thead,_tbody,new Number(1));
			});
			var _prev = $('<li name="prev" class="notremove" title="转到上一页/显示上一组按钮"><span>&#8249;</span></li>')
			.click(function(e){
				if(!el.data('data')) return;
				e.stopPropagation();
				var cpage = el.data('cpage');
				if((cpage-1) > 0){
					var _li = $(e.target);
					if(e.target.tagName == 'SPAN'){
						_li = _li.parent('li');
					}
					if($('li.mongrid-pagination-active',_li.parent('ul')).attr('axis') == '1'){
						return;
					}
					var hasNextGroup;
					var _rowNum = el.data('rowNum') ? el.data('rowNum') : args.rowNum;
					var total = el.data('total');				
					if(Math.ceil(total/_rowNum) > 10){
						hasNextGroup = true;
						el.data('hasNextGroup',hasNextGroup);
					}
					var prevli = _li.siblings('li[axis="'+cpage+'"]').prev('li');
					if(prevli.hasClass('notremove') && hasNextGroup){
						var pageGroupNum = el.data('pageGroupNum');
						el.data('pageGroupNum',pageGroupNum-1);
					}else{					
						prevli.siblings('li').removeClass('mongrid-pagination-active').end().addClass('mongrid-pagination-active');
					}
					myself.__gotopage(_body,_thead,_tbody,new Number(cpage - 1));
				}
			});
			var _next = $('<li name="next" class="notremove" title="转到下一页/显示下一组按钮"><span>&#8250;</span></li>')
			.click(function(e){
				if(!el.data('data')) return;
				e.stopPropagation();
				var cpage = el.data('cpage');
				var _rowNum = el.data('rowNum') ? el.data('rowNum') : args.rowNum;
				var total = el.data('total');
				var pageNum = Math.ceil(total/_rowNum);
				if((cpage + 1) <= pageNum){
					var _li = $(e.target);
					if(e.target.tagName == 'SPAN'){
						_li = _li.parent('li');
					}
					var hasNextGroup;
					var _rowNum = el.data('rowNum') ? el.data('rowNum') : args.rowNum;
					var total = el.data('total');				
					if(Math.ceil(total/_rowNum) > 10){
						hasNextGroup = true;
						el.data('hasNextGroup',hasNextGroup);
					}
					var nextli = _li.siblings('li[axis="'+cpage+'"]').next('li');
					if(nextli.hasClass('notremove') && hasNextGroup){
						var pageGroupNum = el.data('pageGroupNum');
						el.data('pageGroupNum',pageGroupNum+1);
					}else{
						nextli.siblings('li').removeClass('mongrid-pagination-active').end().addClass('mongrid-pagination-active');
					}
					myself.__gotopage(_body,_thead,_tbody,new Number(cpage + 1));
				}
			});
			var _last = $('<li name="last" class="notremove" title="转到最后一页"><span>&raquo;</span></li>')
			.click(function(e){
				e.stopPropagation();
				var _rowNum = el.data('rowNum') ? el.data('rowNum') : args.rowNum;
				var total = el.data('total');
				var _pageNum = new Number(Math.ceil(total/_rowNum));
				el.data('pageGroupNum',new Number(Math.ceil(_pageNum/args.pageNum)));
				myself.__gotopage(_body,_thead,_tbody,_pageNum);
			});
			pagination.append(_first).append(_prev).append(_next).append(_last);
			pd.append(pagination);
			
			var rowlistselect = 
			$('<select class="mongrid-rowlistselect" title="选择每页显示的行数"><select>')
			.change(function(e){
				if(!el.data('data')) return;
				var _rowNum = $(e.target).val();
				el.data('rowNum',_rowNum);
				_tbody.empty();
				var p = el.data('p');
				p = $.extend(p, {
					start: 0,
					limit: _rowNum,
					sortname: el.data('sortname')?el.data('sortname'):args.sortname,
					sortorder: el.data('sortorder')?el.data('sortorder'):args.sortorder
				});
				myself._getdata(p ,function(data){
					if(data){
						var rows = data['rows'];
						el.data('pageGroupNum', 1);
						el.data('cpage',1);
						myself._adddata(_body,_thead,_tbody,rows);
					}
				});
			});
			$.each(args.rowList,function(i,n){
				rowlistselect.append('<option value="'+n+'">'+n+'</option>');
			});
			pd.append(rowlistselect);
			_body.append(pd);
		},
		//分页条数据
		_addpager : function(_body,_thead,_tbody){
			var myself = this, args = this.options,el = this.element;
			var pagination = $('ul.mongrid-pagination',_body);
			var nextli = $('li[name="next"]',pagination);
			var _rowNum = el.data('rowNum') ? el.data('rowNum') : args.rowNum;
			var total = el.data('total');
			var pageNum = Math.ceil(total/_rowNum);
			var hasNextGroup = false;
			var cpage = el.data('cpage');
			
			$('ul.mongrid-pagination > li:not(.notremove)',_body).remove();
			if(pageNum > 0 && (cpage?(cpage <= pageNum):true)) {
				var _pageNum = args.pageNum;
				if(pageNum > args.pageNum){
					hasNextGroup = true;
					el.data('hasNextGroup',hasNextGroup);
				}
				var pageGroupNum = el.data('pageGroupNum');			
				var curPageNum = pageNum - (pageGroupNum-1)*args.pageNum;				
				if(curPageNum < args.pageNum){
					_pageNum = curPageNum;
				}
				var i = pageGroupCount = (new Number(pageGroupNum) - 1) * args.pageNum + new Number(1);
				for(; i < (_pageNum + pageGroupCount); i++){
					if(cpage && i == cpage){
						nextli.before($('<li class="mongrid-pagination-active" axis="' + i + '" title="点击转到第' + i + '页"><span>' + i + '</span></li>'));
					} else if(!cpage && i == 1){
						nextli.before($('<li class="mongrid-pagination-active" axis="1" title="点击转到第1页"><span>1</span></li>'));	
						el.data('cpage',i);
					} else {
						nextli.before($('<li axis="' + i + '" title="点击转到第' + i + '页"><span>' + i + '</span></li>'));		
					}
				}
				$('li:not(.notremove)',pagination).click(function(e){
					if(!el.data('data')) return;
					e.stopPropagation();
					var _li = $(e.target);
					if(e.target.tagName == 'SPAN'){
						_li = _li.parent('li');
					}
					_li.siblings('li').removeClass('ui-state-highlight');
					var cpage = new Number(_li.attr('axis'));
					if(_li.prev('li').hasClass('notremove') && hasNextGroup && cpage != 1){
						_li.prev('li').addClass('ui-state-highlight');
					}
					if(_li.next('li').hasClass('notremove') && hasNextGroup){
						_li.next('li').addClass('ui-state-highlight');
					}
					_li.siblings('li').removeClass('mongrid-pagination-active').end().addClass('mongrid-pagination-active');		
					myself.__gotopage(_body,_thead,_tbody,cpage);					
				});
			}
		},		
		__gotopage : function(_body,_thead,_tbody,cpage){
			var myself = this, args = this.options,el = this.element;
			el.data('cpage',cpage);
			var start,limit;
			if(cpage == 1) {
				start = 0;
				limit = (el.data('rowNum') ? el.data('rowNum') : args.rowNum);
			} else {
				start = new Number(cpage - 1) * new Number(el.data('rowNum') ? el.data('rowNum') : args.rowNum);
				limit = new Number(start) + new Number(el.data('rowNum') ? el.data('rowNum') : args.rowNum);
			}
			
			_tbody.empty();
			var p = el.data('p');
			p = $.extend(p, {
				start: start,
				limit: limit,
				sortname: el.data('sortname')?el.data('sortname'):args.sortname,
				sortorder: el.data('sortorder')?el.data('sortorder'):args.sortorder
			});
			myself._getdata(p, function(data){
				if(data){
					var rows = data['rows'];
					myself._adddata(_body,_thead,_tbody,rows);
				}
			});
		},
		//tbar
		_buildtbar : function() {
			var myself = this, args = this.options,el = this.element;
			args.tbar($('<div class="mongrid-tbar"><div>').prependTo($('.mongrid-body',el)));
		},
		//bbar
		_buildbbar : function() {
			var myself = this, args = this.options,el = this.element;
			args.bbar($('<div class="mongrid-bbar"><div>').insertAfter($('.mongrid-body-tbody-table',el)));
		},
		//重新计算表格的宽度（用于宽度变化时，如拖拽改变表头宽度、显示/隐藏列等）
		_t_buildTableWidth : function(_thead, _tbody){
			var myself = this, args = this.options,el = this.element;
			var newwidth=0;
			$('div.mongrid-body-thead-col:visible',_thead).each(function(){
				newwidth += $(this).outerWidth();
			});
			var _theadWidth = newwidth + myself._f_overflowY;
			if(_theadWidth < args.width)
				_theadWidth = args.width;
			_thead.width(_theadWidth);
			_tbody.width(newwidth);
		},
		_buildfooter : function(){
			var myself = this, args = this.options,el = this.element;
			$('<div class="mongrid-footer"><div>')
			.width(args.width)
			.appendTo(el);
		},
		_getWidth : function(obj){
			return (BrowserDetect.browser == 'Firefox' ? (obj.width() + new Number(obj.css('border-right-width').replace('px',''))) : obj.outerWidth(true));
		},
		//对外方法-重载
		reloadGrid : function(_data) {
			var myself = this, args = this.options, el = this.element;
			_data = $.extend(_data, {
					start: 0,
					limit: el.data('rowNum') ? el.data('rowNum') : args.rowNum,
					sortname: args.sortname,
					sortorder: args.sortorder
				});
			myself._getdata(_data, function(data){
				var _body = $('div.mongrid-body',el);
				var _thead = $('div.mongrid-body-thead',el);
				var _tbody = $('div.mongrid-body-tbody',el).empty();
				if(data){
					var rows = data['rows'];
					el.data('pageGroupNum', 1);
					myself._adddata(_body,_thead,_tbody,rows);
				}
			});
		},
		//对外方法-添加多行
		addRows : function(rows){
			var myself = this, args = this.options, el = this.element;
			var _body = $('div.mongrid-body',el);
			var _thead = $('div.mongrid-body-thead',el);
			var _tbody = $('div.mongrid-body-tbody',el);
			myself._adddata(_body,_thead,_tbody,rows);
			
		},
		//对外方法-添加一行
		addRow : function(row){
			var myself = this, args = this.options, el = this.element;
			var _body = $('div.mongrid-body',el);
			var _thead = $('div.mongrid-body-thead',el);
			var _tbody = $('div.mongrid-body-tbody',el);
			myself._addrow(_body,_thead,_tbody,row);
			myself._striped(_tbody);
		},
		//对外方法-删除指定的行
		removeRow : function(rowid){
			var myself = this, args = this.options, el = this.element;
			var _tbody = $('div.mongrid-body-tbody',el);
			$('div.mongrid-body-tbody .mongrid-body-tbody-row[axis='+rowid+']',el).remove();
			myself._striped(_tbody);
		},
		//对外方法-删除当前选中的行
		removeActiveRow : function(){
			var myself = this, args = this.options, el = this.element;
			var _tbody = $('div.mongrid-body-tbody',el);
			$('div.mongrid-body-tbody .mongrid-body-tbody-row',el)
			.each(function(){
				if($(this).hasClass("mongrid-body-tbody-row-active"))
					$(this).remove();
			});
			myself._striped(_tbody);
		},
		//返回当前选中行的ID
		getActiveRow : function(){
			var myself = this, args = this.options, el = this.element;
			var rows = new Array();
			$('div.mongrid-body-tbody .mongrid-body-tbody-row-active',el)
			.each(function(){
				rows.push($(this).data('row'));
			});
			return rows;
		}
	});
})(jQuery);

/**
 * 将方法全部加入js中，然后BrowserDetect.init()初始化后，调用这三个方法即可
 * 
 * 1、返回浏览器名称 : BrowserDetect.browser 
 * 2、返回浏览器版本: BrowserDetect.version 
 * 3、返回运行平台: BrowserDetect.OS
 */
var BrowserDetect = {
	init : function() {
		this.browser = this.searchString(this.dataBrowser)
				|| "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
				|| this.searchVersion(navigator.appVersion)
				|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString : function(data) {
		for (var i = 0; i < data.length; i++) {
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch
					|| data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			} else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion : function(dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1)
			return;
		return parseFloat(dataString.substring(index
				+ this.versionSearchString.length + 1));
	},
	dataBrowser : [ {
		string : navigator.userAgent,
		subString : "Chrome",
		identity : "Chrome"
	}, {
		string : navigator.userAgent,
		subString : "OmniWeb",
		versionSearch : "OmniWeb/",
		identity : "OmniWeb"
	}, {
		string : navigator.vendor,
		subString : "Apple",
		identity : "Safari",
		versionSearch : "Version"
	}, {
		prop : window.opera,
		identity : "Opera",
		versionSearch : "Version"
	}, {
		string : navigator.vendor,
		subString : "iCab",
		identity : "iCab"
	}, {
		string : navigator.vendor,
		subString : "KDE",
		identity : "Konqueror"
	}, {
		string : navigator.userAgent,
		subString : "Firefox",
		identity : "Firefox"
	}, {
		string : navigator.vendor,
		subString : "Camino",
		identity : "Camino"
	}, { // for newer Netscapes (6+)
		string : navigator.userAgent,
		subString : "Netscape",
		identity : "Netscape"
	}, {
		string : navigator.userAgent,
		subString : "MSIE",
		identity : "Explorer",
		versionSearch : "MSIE"
	}, {
		string : navigator.userAgent,
		subString : "Gecko",
		identity : "Mozilla",
		versionSearch : "rv"
	}, { // for older Netscapes (4-)
		string : navigator.userAgent,
		subString : "Mozilla",
		identity : "Netscape",
		versionSearch : "Mozilla"
	} ],
	dataOS : [ {
		string : navigator.platform,
		subString : "Win",
		identity : "Windows"
	}, {
		string : navigator.platform,
		subString : "Mac",
		identity : "Mac"
	}, {
		string : navigator.userAgent,
		subString : "iPhone",
		identity : "iPhone/iPod"
	}, {
		string : navigator.platform,
		subString : "Linux",
		identity : "Linux"
	} ]
};