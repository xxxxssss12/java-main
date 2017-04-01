package xs.spider.base.bean;

import java.io.Serializable;

/**
 * Created by hasee on 2016/9/3.
 */
public class ResultInfo implements Serializable {

    private Integer code;
    private String message;
    private Object data;
    public ResultInfo() {

    }
    public ResultInfo(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public void setCodeAndMsg(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "result:[code=" + code + ";message=" + message + ";data=" + (data==null?"":data.toString()) + "]";
    }
}
