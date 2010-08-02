## GET /browser
01. Accept *GET* `request`
02. `uri` = path info for `request`
03. verify `path` for `uri` can be retrieved
04. if `path` is valid then
05.		use `ajs.files.FileController` to find `info` for `path`
06.		serialize and print `info` into `response`
07. else
08.		print error into `response`

## POST /browser
01. Accept *POST* `request`
02. `uri` = path info for `request`
03. verify `path` for `uri` can be retrieved
04. if `path` is valid then
05.		use `ajs.files.FileController` to create new directory at `path`
06.		if success then
07.			serialize and print info for path into `response`
08.		else
09.			print error into `response`
10. else
11. 	print error into `response`

## DELETE /browser
01. Accept *DELETE* `request`
02. `uri` = path info for `request`
03. verify `path` for `uri` can be retrieved
04. if `path` is valid then
05.		use `ajs.files.FileController` to delete file at `path`
06.		if success then
07.			print success into `response`
08.		else
09.			print error into `response`
10. else
11. 	print error into `response`

## GET /data-transfer
01. Accept *GET* `request`
02. `uri` = path info for `request`
03. verify `path` for `uri` can be retrieved
04. if `path` is valid then
05.		stream file at `path` to output stream of `response`
06. else
07.		print error into `response`

## GET | POST /upload
01. Accept `request`
02. `uri` = path info for `request`
03. verify `path` for `uri` can be retrieved
04. if `path` is valid then
05.		use `org.apache.commons.servlet.ServletFileUpload` to load request `payload`
06. 	use `ajs.files.FileData` to save `payload` to a file at `path`
07.		redirect to success page
08. else
09.		redirect to error page