package io.dango.repository;

import io.dango.config.DBConfig;
import io.dango.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by MainasuK on 2017-7-6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DBConfig.class })
public class JDBCUserRepositoryTest {

    @Autowired
    private JDBCUserRepository userRepository;

    @Test
    public void testRepository() {
        Assert.notNull(userRepository);
    }

    @Test
    public void getUserById() throws Exception {
        User user = userRepository.getUserById(1);
        Assert.notNull(user, "root");
    }

    @Test
    public void getUserByUsername() throws Exception {
        User user = userRepository.getUserByUsername("user");
        Assert.notNull(user, "user");
        assertEquals("user", user.getUsername());
    }

    @Test
    public void saveUser() throws Exception {
        User newUser = new User();
        newUser.setUsername("New");
        newUser.setPassword("NewP");

        userRepository.saveUser(newUser);
    }

    @Test
    public void verify() throws Exception {
        User user = userRepository.verify("user", "USER");
        assertNotNull(user);
    }

    @Test
    public void setUserUploadFace() throws Exception {
        userRepository.setUserNeedUploadFace("user", false);
        User user = userRepository.getUserByUsername("user");
        assertEquals(false, user.getNeedface());

        userRepository.setUserNeedUploadFace("user", true);
        user = userRepository.getUserByUsername("user");
        assertEquals(true, user.getNeedface());
    }

}