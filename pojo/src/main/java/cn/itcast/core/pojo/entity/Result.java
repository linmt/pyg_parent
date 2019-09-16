package cn.itcast.core.pojo.entity;

import java.io.Serializable;

/**
 * 自定义封装类, 封装了返回的正确或者错误信息
 */
public class Result implements Serializable {
    //布尔值, true操作成功, FALSE 操作失败
    private boolean success;
    //成功信息或者是错误信息
    private String message;
    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
