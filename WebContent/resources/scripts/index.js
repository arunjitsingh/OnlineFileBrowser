	/**
 * @author arunjitsingh
 */
var $APP = $APP || {VERSION:'1.a.2541'};
$APP.applicationRoot = '/OnlineFileBrowser';
$APP.resources = {};
$APP.resources['browser'] = '/browser';
$APP.resources['data-transfer'] = '/data-transfer';
$APP.resources['upload'] = '/upload';
$APP.resources['auth'] = '/auth';
$APP.LOGOUT = "logout.jsp";

$APP.user = {};

$APP.currentSelection = null;
$APP.currentColumn = null;

$APP.deletionQueue = {
	queue: [],
	add: function(elt) {
		$APP.deletionQueue.queue.push(elt);
	},
	execute: function() {
		$.each($APP.deletionQueue.queue, function(idx,elt) {
			$(elt).remove();
		});
	}
};

$APP.getResource = function(res) {
	return $APP.applicationRoot + $APP.resources[res] + '/';
};

$APP.asResource = function(resource,request) {
	if (request && request.match(/:root/i)) request='';
	return ($APP.getResource(resource) + request);
};

$APP.fetchData = function(uri, callback){
	FI.log(uri, "Fetching");
	var resource = $APP.asResource('browser', uri);
	$.read(resource, {}, callback);
};

$APP.didFetchData = function(response) {
	if (response && response.status) {
		if(response.content) {
			var file = response.content;
			if (file.isDirectory) {
				$APP.renderFetchedData(file.children);
			} else {
				$APP.renderDetails(file);
			}			
		}
	}
};

$APP.renderFetchedData = function(content) {
	var list = FI.ListView.createSimpleList({template:$APP.ListItemTemplate});
	list.updateContent(content);
	$APP.columns.newColumn().addSubview(list.view());
	$APP.columns.renderColumns();
};

$APP.renderDetails = function(file) {
	$('#overlay').css({'visibility':'visible'});
	var elt = $('#details').css({'visibility':'visible'});
	elt.data(file);
	FI.View.renderView(elt, {VIEW:$APP.DetailsViewAttributes});
};

$APP.deleteResource = function(uri, callback) {
	FI.log(uri, "Deleting");
	var resource = $APP.asResource('browser', uri);
	//$.destroy(resource, {}, callback);//DOES NOT WORK.. sends POST
	$.ajax({
		url: resource,
		type: 'DELETE',
		dataType: 'json',
		success: callback
	});
};

$APP.didDelete = function(response) {
	if (response.status) {
		$APP.deletionQueue.execute();
		$APP.currentSelection = $('.selected').last();
		if ($APP.currentSelection.length < 1) {
			FI.log("nothing selected", "$APP.didDelete");
			$APP.currentSelection = $('#homedata');
			$APP.columns.selectColumn(0);
		} else {
			$APP.currentColumn = $APP.currentSelection.first().parents('.column');
			var vi = $APP.currentColumn.data().viewIndex;
			$APP.columns.selectColumn(vi);
		}
	}
};

$APP.Transformers = {};
$APP.Transformers.ID = function(value) {
	return value.substring(value.lastIndexOf('/')+1);
};
$APP.Transformers.Date = function(value) {
	var date =  new Date(value);
		str = date.getMonthName() + " " + date.getDate() + " " + date.getFullYear();
	return str;
};
$APP.Transformers.Size = function(value) {
	var units = ['B', 'KB', 'MB', 'GB', 'TB', 'EB'];
	var size = value,
		i;
	for (i=0;;++i) {
		if (size > 1024) size /= 1024;
		else break;
	}
	return Math.round(size)+" "+units[i];
};

$APP.ListItemTemplate = {
	VIEW: {
		':root': {			
			'content': {
				'key': 'id',
				'type': 'text',
				transformedValue: $APP.Transformers.ID
			},
			'class': {
				'key': 'isDirectory',
				transformedValue: function(value) {
					return value?"DIR":"FILE";
				}
			}
		}
	},
	ACTION: {
		click: function(evt) {
			var elt = $(evt.target);
			$APP.currentColumn = elt.parents('.column');
			var data = $APP.currentColumn.data();
//			console.log("data: " + JSON.stringify(data));
			var idx = data.viewIndex;
//			FI.log("idx="+idx);
			$APP.columns.selectColumn(idx);
			if (evt.metaKey || evt.ctrlKey) {
				$(elt).removeClass('selected');
				$APP.currentSelection = $('.selected').last();
				if ($('.list .selected').length < 1) {
					// nothing is selected, refer to #homedata
					$APP.currentSelection = $('#homedata');
				}
				return true;
			}
			elt.siblings('.selected').removeClass('selected');
			elt.addClass('selected');
			$APP.currentSelection = elt;
			var id = elt.data().id;
//			FI.log(elt.data(), 'data');
			$APP.fetchData(id, $APP.didFetchData);
			return true;
		}
	}
};

$APP.DetailsViewAttributes = {
	'.file-name': {'content':{'key':'id', 'type':'text', transformedValue: $APP.Transformers.ID}},
	'.file-size': {'content':{'key':'size', 'type':'text', transformedValue: $APP.Transformers.Size}},
	'.file-date': {'content':{'key':'modifiedAt', 'type':'text', transformedValue: $APP.Transformers.Date}}
};

$APP.columnWidth = 325;

var loadApplication = function() {
	
	if ( /*Dependancies*/
		typeof($)==='undefined'			// jQuery, jquery.js
		|| typeof(jQuery)==='undefined'	// jQuery, jquery.js
		|| typeof($.json)==='undefined' // jQuery REST, jquery.rest.js
		|| typeof(Class)==='undefined' 	//OO, oo-min.js
		|| typeof(FI)==='undefined'		//FI, FileIt.js
		|| typeof(DATEJS)==='undefined' //date.js
		|| typeof($APP)==='undefined'	//this application
		) {
		alert("Application load failed. Try reloading");
		throw new Error("Application load failed");
	}
	
	var left = 0, incr = 25, wdth = 75;
	$('#toolbar .image-button').each(function(idx, elt){
		$(elt).css({'left':(left*wdth+incr)});
		left++;
	});
	
	$APP.columns = new FI.ColumnView('browser', {columnWidth: $APP.columnWidth,
												autoScrollX: false,
												columnCSS: {
													'width':$APP.columnWidth, 
													'height':'inherit'
												}
									});	
	
	$('#toolbar #logout_btn').click(function(evt) {
		if (confirm("Log out for sure?")) {
			window.location.href = $APP.LOGOUT;
		}
	});	
	
	$('#toolbar #home_btn').click(function(evt) {
		$APP.columns.selectColumn(-1);
		var req = $APP.user.home;
		$APP.currentSelection = $('#homedata').data({id:req, isDirectory:true});
		$APP.fetchData(req, $APP.didFetchData);
	});
	
	$('#toolbar #groups_btn').click(function(evt) {
		$APP.columns.selectColumn(-1);
		var content = $APP.user.groups;
		$APP.renderFetchedData(content);
	});
	
	$('#toolbar #trash_btn').click(function(evt) {
		if ($APP.currentSelection && $('.selected').length > 0) {
			if (!confirm('Deletion cannot be reversed! Continue?'))
				return false;
			var elt = $APP.currentSelection = $('.selected').last();
			var data = elt.data();
			var id = data.id ? data.id : null;
			if (id) {
				$APP.deleteResource(id, $APP.didDelete);
				$APP.deletionQueue.add(elt);
			}
			return false;
		}
		return false;
	});
	
	$('#toolbar #create_btn').click(function(evt) {
		if ($APP.currentSelection) {
			//FI.log($APP.currentSelection.data(), "$APP.currentSelection.data()");
			var eltcs = $APP.currentSelection.first();
			var id = eltcs.data().id,
				isD = eltcs.data().isDirectory;
			if (id) {
				if (isD !== undefined && isD == false) {
					$APP.currentSelection.removeClass('.selected');
					$APP.currentSelection = $('.selected').last();
					id = $APP.currentSelection.data().id;
				}
				$('#overlay').css({'visibility':'visible'});
				var elt = $('#new-dir');
				elt.css({'visibility':'visible'});
				$('.currentSelection', elt).text($APP.Transformers.ID(id)+"/");				
				$('#new-dir-ok').click(function(evt) {
					var newdir = $('#new-dir-name', elt).val();
					if (newdir && newdir !== "") {
						var uri = "";
						uri = $APP.asResource('browser', id);
						uri += "/" + newdir;
						FI.log(uri, "Creating");
						$.create(uri,{},function(response) {
							FI.log(response, "Creation response");
							// hide everything
							elt.css({'visibility':'hidden'});
							$('#overlay').css({'visibility':'hidden'});
							$('#new-dir-name').val("");
							$APP.currentColumn = $APP.currentSelection.first().parents('.column');
							if ($APP.currentColumn.length < 1) {
								$APP.currentSelection = $('#homedata');
								$APP.columns.selectColumn(-1);
							} else {
								var vi = $APP.currentColumn.data().viewIndex-1;
								$APP.columns.selectColumn(vi);
							}							
							$APP.fetchData(id, $APP.didFetchData);
						});
					} else {
						$('#new-dir-name')[0].focus();
					}
					return false;
				});
			}
		}
		return false;
	});
	
	$('#toolbar #upload_btn').click(function(evt) {
		if ($APP.currentSelection) {
			//FI.log($APP.currentSelection.data(), "$APP.currentSelection.data()");
			var eltcs = $APP.currentSelection;
			var id = eltcs.data().id,
				isD = eltcs.data().isDirectory;
			if (id) {
				if (isD !== undefined && isD == false) {
					$APP.currentSelection.removeClass('.selected');
					$APP.currentSelection = $('.selected').last();
					id = $APP.currentSelection.data().id;
				}
				$('#overlay').css({'visibility':'visible'});
				var elt = $('#upload-file');
				elt.css({'visibility':'visible'});
				$('.currentSelection', elt).text($APP.Transformers.ID(id)+"/");
				var resource = $APP.asResource('upload', id);
				$('#upload-frame', elt).attr('src', ('upload-frame.html#'+resource));
			}
		}
		return false;
	});
	
	$('#file-download').click(function() {
		if($APP.currentSelection) {
			var id = $APP.currentSelection.data().id;
			var resource = $APP.asResource('data-transfer', id) + '?do=download';
			var iframe = document.createElement("iframe");
	        iframe.src = resource;
	        iframe = $(iframe);
	        iframe.css({position:'absolute', left:-10000, top:-10000, display:'hidden'});
	        $('#dlsection').append(iframe);
		}
	});
	
	$('.close.button').click(function() {
		$(this).parent().css({'visibility':'hidden'});
		$(this).parent('#upload-file').children('#upload-frame').attr('src', "");
		$('#overlay').css({'visibility':'hidden'});
	});
	
	$.read($APP.getResource('auth'), {}, function(response) {
	    if (response && response.status) {
	    	var info = "";
		    if (typeof(response.content) === 'object'){
			    info = JSON.stringify(response.content);
		    } else {
		    	info = response.content;
		    }
	    	window.sessionStorage['user'] = info;
	    	$APP.user = JSON.parse(window.sessionStorage['user']);
	    }
	});
	
	$('body')[0].style['visibility'] = 'visible';
};

window.uploadCompleted = function(success) {
	if (success) {
		$APP.currentColumn = $APP.currentSelection.first().parents('.column');
		var vi = $APP.currentColumn.data().viewIndex;
		$APP.columns.selectColumn(vi);
		var data = $APP.currentSelection.data(),
			id = data.id;
		$APP.fetchData(id, $APP.didFetchData);
	}
};