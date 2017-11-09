package io.dango.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by MainasuK on 2017-7-6.
 */
public class User {

    private Long id;
    private String username;
    private String password;
    private String roleName;
    private Boolean needface;

    public User() {
    }

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.needface = user.needface;
    }

    public User(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getLong("id");
        this.username = resultSet.getString("username");
        this.password = resultSet.getString("password");
        this.roleName = resultSet.getString("role");
        this.needface = resultSet.getBoolean("needface");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Boolean getNeedface() {
        return needface;
    }

    public void setNeedface(Boolean needface) {
        this.needface = needface;
    }
}
