package io.dango.repository;

import io.dango.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Null;

/**
 * Created by MainasuK on 2017-7-6.
 */
@Repository
public class JDBCUserRepository implements UserRepository {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public JDBCUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(long id) {
        return jdbcTemplate.queryForObject("select * from user u where u.id = ?", (resultSet, i) -> new User(resultSet), id);
    }

    @Override
    public User getUserByUsername(String username) {
        return jdbcTemplate.queryForObject("select * from user u where u.username = ?", (resultSet, i) -> new User(resultSet), username);
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        jdbcTemplate.update("INSERT INTO user (username, password, role) VALUES (?, ?, ?)", user.getUsername(), user.getPassword(), "ROLE_USER");
    }

    @Override
    public User verify(String username, String password) {
        try {
            return jdbcTemplate.queryForObject("select * from user u where u.username = ? AND u.password = ?", (resultSet, i) -> new User(resultSet), username, password);
        } catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public boolean removeByUsername(String username) {
        // TODO:
        return false;
    }

    @Override
    public void setUserNeedUploadFace(String username, boolean flag) {
        jdbcTemplate.update("update user u set u.needface = ? where u.username = ?", flag, username);
    }


}
