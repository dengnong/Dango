package io.dango.pojo;

/**
 * Created by MainasuK on 2017-6-30.
 */
public class DangoError extends Throwable {
    private int code;
    private String message;

    public DangoError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
