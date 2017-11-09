package io.dango.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by MainasuK on 2017-7-6.
 */
public class Role implements GrantedAuthority {

    private Long id;
    private RoleName roleName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getAuthority() {
        return "ROLE_USER";
    }
}
