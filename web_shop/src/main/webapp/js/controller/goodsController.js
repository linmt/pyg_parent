//控制层
app.controller('goodsController', function ($scope, $controller, $location, typeTemplateService, itemCatService, uploadService, goodsService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        // alert(id);
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;

                // 调用处理富文本编辑器：
                editor.html($scope.entity.goodsDesc.introduction);

                // 处理图片列表，因为图片信息保存的是JSON的字符串，让前台识别为JSON格式对象
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);

                // 处理扩展属性:
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);

                // 处理规格  [{"attributeName":"网络","attributeValue":["移动3G","联通3G"]},
                // {"attributeName":"机身内存","attributeValue":["16G","128G"]}]
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);

                // 遍历SKU的集合:
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }

    //根据规格名称和选项名称返回是否被勾选
    //ng-checked="checkAttributeValue(pojo.text,p.optionName)  机身内存:16G
    $scope.checkAttributeValue = function (specName, optionName) {
        var items = $scope.entity.goodsDesc.specificationItems;
        //baseController.js  从集合中查询某个名称的值是否存在
        var object = $scope.searchObjectByKey(items, "attributeName", specName);
        if (object != null) {
            if (object.attributeValue.indexOf(optionName) >= 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //保存
    $scope.save = function () {
        // 再添加之前，获得富文本编辑器中的内容。
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert(response.message);
                    //下面两句是讲义中的
                    $scope.entity={};
                    editor.html("");
                    location.href = "goods.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    // $scope.entity={goods:{},goodsDesc:{},itemList:[]}

    $scope.uploadFile = function () {
        // 调用uploadService的方法完成文件的上传
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                // 获得url
                $scope.image_entity.url = response.message;
            } else {
                alert(response.message);
            }
        });
    }

    // 获得了image_entity的实体的数据{"color":"褐色","url":"http://192.168.209.132/group1/M00/00/00/wKjRhFn1bH2AZAatAACXQA462ec665.jpg"}
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};

    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_iamge_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    // 查询一级分类列表:
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCat1List = response;
        });
    }

    //#watch是angularjs中的内置对象, 作用是监听$scope域中的某个变量发生了值的改变.
    // 查询二级分类列表:
    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List = response;
        });
    });

    // 查询三级分类列表:
    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List = response;
        });
    });

    // 查询模块ID
    $scope.$watch("entity.goods.category3Id", function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId;
        });
    });

    // 查询模板下的品牌列表:
    //模板id变化触发这个方法
    $scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
        // 根据模板ID查询模板的数据
        typeTemplateService.findOne(newValue).success(function (response) {
            $scope.typeTemplate = response;
            // 将品牌的字符串数据转成JSON
            $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);

            // 将扩展属性的字符串转成JSON
            if ($location.search()['id'] == null) {
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
            }
        });

        // 根据模板ID获得规格的列表的数据：
        /*
         本身模板表有存规格字段spec_ids，为什么还要去规格表查？
         因为模板表存的规格集合里面，只有id和text两个字段，这两个字段是tb_specification表的，而我们此处不仅要这两个字段，还要规格选项表tb_specification_option中的字段
         */
        typeTemplateService.findBySpecList(newValue).success(function (response) {
            $scope.specList = response;
        });
    });

    $scope.updateSpecAttribute = function ($event, name, value) {
        // 调用封装的方法判断 勾选的名称是否存在:
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, "attributeName", name);

        if (object != null) {
            // 找到了
            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
            }

            if (object.attributeValue.length == 0) {
                var idx = $scope.entity.goodsDesc.specificationItems.indexOf(object);
                $scope.entity.goodsDesc.specificationItems.splice(idx, 1);
            }
        } else {
            // 没找到
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
    }

    // 创建SKU的信息:
    $scope.createItemList = function () {
        // 初始化基础数据:
        $scope.entity.itemList = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}];

        var items = $scope.entity.goodsDesc.specificationItems;

        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }

    addColumn = function (list, columnName, columnValues) {
        // 定义一个集合用于保存生成的每行的数据:
        var newList = [];
        // 遍历该集合的数据:
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                // 对oldRow数据进行克隆:
                var newRow = JSON.parse(JSON.stringify(oldRow));
                newRow.spec[columnName] = columnValues[j];
                // 将newRow存入到newList中
                newList.push(newRow);
            }
        }
        return newList;
    }

    // 显示状态
    $scope.status = ["未审核", "审核通过", "审核未通过", "关闭"];

    $scope.itemCatList = [];
    // 显示分类:
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.itemCatList[response[i].id] = response[i].name;
            }
        });
    }
});	
