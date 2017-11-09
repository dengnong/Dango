package io.dango.repository;

import io.dango.entity.User;

/**
 * Created by MainasuK on 2017-7-6.
 */
public interface UserRepository {
    User getUserById(long id);
    User getUserByUsername(String username);
    void saveUser(User user);
    User verify(String username, String passworld);
    boolean removeByUsername(String username);
    void setUserNeedUploadFace(String username, boolean flag);
}
