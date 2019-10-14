app.service("uploadService",function($http){
	
	this.uploadFile = function(){
		// 模拟一个from表单，向后台传递数据:
		var formData = new FormData();
		// 向formData中添加数据:
		/*
		为什么是file.files[0]？
		第一个file是<input type="file" id="file"/>中的id
		 <input type="file" id="file" multiple/>可以上传多个文件，files[0]表示取第一个
		 */
		formData.append("file",file.files[0]);
		
		return $http({
			method:'post',
			url:'../upload/uploadFile.do',
			data:formData,
			//anjularjs对于post和get请求默认的Content-Type header 是application/json。通过设置‘Content-Type’: undefined，这样浏览器会帮我们把Content-Type 设置为 multipart/form-data.
			headers:{'Content-Type':undefined} ,// Content-Type : text/html  text/plain
			//通过设置 transformRequest: angular.identity ，anjularjs transformRequest function 将序列化我们的formdata object.
			transformRequest: angular.identity
		});
	}
	
});