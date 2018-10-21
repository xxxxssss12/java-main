/**
 * Created by hasee on 2017/1/25.
 */

var baseUrl;
var basepath=function(){
    var location = (window.location+"").split("/");
    var basePath = location[0]+"//"+location[2]+"/"+location[3]+"/";
    return basePath;
};
baseUrl = basepath();

var urls = {
    index: baseUrl + "static/manage.html",
    login: baseUrl + "static/login.html"
}
$(document).ajaxComplete(function(event, xhr, settings) {
    // var timeout = xhr.getResponseHeader("session-timeout");
    // if (timeout && timeout == 'true') {
    if (xhr.status == 403) {
        top.location.href = urls.login;
    }
});
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
        + " " + date.getHours() + seperator2 + date.getMinutes()
        + seperator2 + date.getSeconds();
    return currentdate;
}
var requestParams;
function getRequestParams() {
    if (requestParams) return requestParams;
    var url = location.search;
    var request = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(url.indexOf("?") + 1);
        var strs = str.split("&");
        for (var i=0; i<strs.length; i++) {
            paramArr = strs[i].split("=");
            if (paramArr.length > 1)
                request[paramArr[0].trim()] = paramArr[1].trim();
        }
        requestParams = request;
        return request;
    }
}

var logout = function() {
    $.ajax({
        url : baseUrl + "auth/doLogout",
        type : "post",
        dataType: "json",
        success: function(ri) {
            if (ri && ri.code >= 1) {
                top.location.href = urls.login;
            }
        }
    })
}