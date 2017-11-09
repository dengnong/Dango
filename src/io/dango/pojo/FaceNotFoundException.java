package io.dango.pojo;

/**
 * Created by MainasuK on 2017-6-30.
 */
public class FaceNotFoundException extends RuntimeException {
    private long userID;

    public FaceNotFoundException(long userID) {
        this.userID = userID;
    }

    public long getUserID() {
        return userID;
    }
}
