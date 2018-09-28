/**
 * @return {string}
 */
function getUrlLocal()
{
    var location = (window.location+"").split("/");
    return location[0]+"//"+location[2]+"/"+location[3]+"/";
}
var basepath = getUrlLocal();

var myTable;
$(function() {
    myTable = $("#tba").DataTable({
        "processing": true,//刷新的那个对话框
        "serverSide": true,//服务器端获取数据
        "paging": true,//开启分页
        lengthMenu: [ //自定义分页长度
            [ 10, 20, 30 ],
            [ '10 页', '20 页', '30 页' ]
        ],
        ordering:false,
        "ajax": {
            "url": basepath + "test/getByPage",
            "type": "POST",
            "data": function (d) {

                //删除多余请求参数
                for(var key in d){
                    if(key.indexOf("columns")==0||key.indexOf("order")==0||key.indexOf("search")==0){ //以columns开头的参数删除
                        delete d[key];
                    }
                }
                var searchParams= {
                    "pagenum" : d.start / d.length + 1,
                    "pagesize" : d.length
                    // "retryType":$("#retryType").val(),
                    // "departmentCode":$("#departmentCode").val()!=""?$("#departmentCode").val():null,
                    // "projectCode":$("#projectCode").val()!=""?$("#projectCode").val():null,
                    // "serviceName":$("#serviceName").val()!=""?$("#serviceName").val():null,
                    // "csrfmiddlewaretoken":csrftoken
                };
                //附加查询参数
                if(searchParams){
                    $.extend(d,searchParams); //给d扩展参数
                }
            },
            "dataType" : "json",
            "dataFilter": function (json) {//json是服务器端返回的数据
                json = JSON.parse(json);
                var returnData = {};
                // returnData.draw = json.data.draw; // 不是页数！
                returnData.recordsTotal = json.data.total;//返回数据全部记录
                returnData.recordsFiltered = json.data.total;//后台不实现过滤功能，每次查询均视作全部结果
                returnData.data = json.data.data;//返回的数据列表
                return JSON.stringify(returnData);//这几个参数都是datatable需要的，必须要
            }
        },
        "searching" : false,
        "columns": [
            { "data": "id" },
            { "data": "testC" },
            { "data": "updateTime" },
            { "data": "createTime" }
        ],
        "oLanguage" : { // 国际化配置
            "sProcessing" : "正在获取数据，请稍后...",
            "sLengthMenu" : "显示 _MENU_ 条",
            "sZeroRecords" : "没有找到数据",
            "sInfo" : "从 _START_ 到  _END_ 条记录 总记录数为 _TOTAL_ 条",
            "sInfoEmpty" : "记录数为0",
            "sInfoFiltered" : "(全部记录数 _MAX_ 条)",
            "sInfoPostFix" : "",
            "sSearch" : "查询",
            "sUrl" : "",
            "oPaginate" : {
                "sFirst" : "第一页",
                "sPrevious" : "上一页",
                "sNext" : "下一页",
                "sLast" : "最后一页"
            }
        }
    })
})