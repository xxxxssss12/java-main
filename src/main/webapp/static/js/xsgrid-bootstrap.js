/**
 * 第一个控件！分页。
 * Created by hasee on 2017/1/25.
 */
// $(function() {
    $.fn.xsGrid = function(options) {
        var $this= $(this);
        var url = options.url;
        var title = options.title;
        var gridid = options.gridid;
        var name = options.name;
        var columns = options.columns;
        var params = options.params;
        var orderStr = options.params.orderStr;
        //封装表格head信息
        var str = "<div>"
        if (title ){
            str += "<h2 class='sub-header' style=\"font-family: 'Microsoft YaHei UI Light'\">" +title+ "</h2>"
        }
        str += "<table class='table' id='"+gridid+"'>";
        str += "<thead id='" +gridid+ "_h'>";
        str += "<tr id='" +gridid+ "_hr'>";
        var index = 0;
        for (var i=0;i<columns.length; i++) {
            var type = columns[i].type;
            var hide = false;
            if (type == 'hide') hide = true;
            var name = columns[i].name;
            var display = columns[i].display;
            str += "<th name='" + name + "' id='" +gridid + "_hr_" +i+ "'" +
                (hide?" style='display:none' ":'') + ">" + display + "</th>"
        }
        str += "</tr></thead><tbody id='" +gridid+ "_b'></tbody></table>";
        str += "<div id='"+gridid+"_page' class='form-inline'>"
        str += "<button id='"+gridid+"_btn_first' class='btn btn-info'>第一页</button>&nbsp;";
        str += "<button id='"+gridid+"_btn_back' class='btn btn-info'>上一页</button>&nbsp;";
        str += "<input typr='text' id='"+gridid+"_txt_pgnum' class='form-control' style='width:50px' value='1'>";
        str += "<label id='" + gridid+ "_total' >/0</label>";
        str += "<button id='"+gridid+"_btn_pgnum' class='btn btn-info'>go</button>&nbsp;";
        str += "<button id='"+gridid+"_btn_next' class='btn btn-info'>下一页</button>&nbsp;";
        str += "<button id='"+gridid+"_btn_last' class='btn btn-info'>末页</button>&nbsp;";
        str += "</div>";
        $this.append(str);
        $this.extend({
            pagevo:{
                url: url,
                pagenum:1,
                pagesize:10,
                list:[],
                total:0,
                tatalpage:0,
                params:{},
                orderStr:""
            },
            gridid:gridid,
            xsQuery: function (options) {
                var pagevo = $this.pagevo;
                if (options.url) pagevo.url = options.url;
                if (options.pagenum) pagevo.pagenum = options.pagenum;
                if (options.pagesize) pagevo.pagesize = options.pagesize;
                var params = options.data;
                if (params && params.orderStr) pagevo.orderStr = params.orderStr;
                var search = options.search;
                if (search == 'first') {
                    pagevo.pagenum = 1;
                } else if (search == 'back') {
                    if (pagevo.pagenum!=1) {
                        pagevo.pagenum -= 1;
                    } else {
                        return;
                    }
                } else if (search == 'next') {
                    if (pagevo.total > ((pagevo.pagenum)*pagevo.pagesize)) {
                        pagevo.pagenum += 1;
                    } else {
                        return;
                    }
                } else if (search == 'last') {
                    pagevo.pagenum = parseInt((pagevo.total-1)/pagevo.pagesize)+1;
                } else {
                    var reg = new RegExp("^[0-9]*$");
                    if(reg.test(search)){
                        if (pagevo.total > ((search)*pagevo.pagesize)) {
                            if (pagevo.pagenum == 0)
                                pagevo.pagenum = search;
                        } else {
                            pagevo.pagenum = parseInt((pagevo.total - 1) /pagevo.pagesize) + 1;
                        }
                    } else {
                        return;
                    }
                }
                if (params && params instanceof Object) {
                    params["orderStr"] = pagevo.orderStr;
                    params["pager.pageNum"]=pagevo.pagenum;
                    params["pager.pageSize"]=pagevo.pagesize;
                    $this.pagevo.params = params;
                } else {
                    $this.pagevo.params["pager.pageNum"]=pagevo.pagenum;
                    $this.pagevo.params["pager.pageSize"]=pagevo.pagesize;
                }
                $.ajax({
                    url:pagevo.url,
                    type:"get",
                    dataType:'json',
                    data:$this.pagevo.params,
                    success : function(rs) {
                        if (rs && rs.code && rs.code==1) {
                            $this.pagevo.pagenum = rs.pageNum;
                            $this.pagevo.pagesize = rs.pageSize;
                            $this.pagevo.total = rs.total;
                            $this.pagevo.totalpage = parseInt((rs.total - 1) /rs.pageSize) + 1;
                            $this.pagevo.list = rs.data;
                            $("#" + $this.gridid + "_txt_pgnum").val($this.pagevo.pagenum);
                            $("#" + $this.gridid + "_total").text("/" + $this.pagevo.totalpage);
                            xsrefresh($this.gridid, $this.pagevo.list, columns);
                        }
                    }
                })
            }
        })
        var xsrefresh = function(gridid, list, columns) {
            $tbody = $("#" + gridid + "_b");
            var html_str = "";
            if (list) {
                for (var i=0; i<list.length; i++) {
                    html_str += "<tr id'" + gridid + "_l_" + i + "'>";
                    for (var j=0;j<columns.length; j++) {
                        var type = columns[j].type;
                        var hide = false;
                        if (type == 'hide') hide = true;
                        var name = columns[j].name;
                        var value = list[i][columns[j].name];
                        var render = columns[j].render;
                        if (render) {
                            value = render(list[i], i);
                        }
                        if (value == null || value == undefined) {
                            value = "";
                        }
                        html_str += "<td name='" + name + "' id='" + gridid + "_l_" + i + "_" + j + "'" +
                            (hide?" style='display:none' ":'') + ">" + value + "</td>";
                    }
                    html_str += "</tr>";
                }
            }
            $tbody.html(html_str)
        }
        $this.xsQuery({
            search: 'first',
            pageSize: 10,
            forceDo: true,
            data: params,
        })
        $("#" + gridid + "_btn_first").click(function() {
            $this.xsQuery({
                search:'first'
            })
        })
        $("#" + gridid + "_btn_back").click(function() {
            $this.xsQuery({
                search:'back'
            })
        })
        $("#" + gridid + "_btn_pgnum").click(function() {
            $this.xsQuery({
                search: $("#" + gridid + "_txt_pgnum").val()
            })
        })
        $("#" + gridid + "_btn_next").click(function() {
            $this.xsQuery({
                search: "next"
            })
        })
        $("#" + gridid + "_btn_last").click(function() {
            $this.xsQuery({
                search:"last"
            })
        })
        return $this;
    }
// })
