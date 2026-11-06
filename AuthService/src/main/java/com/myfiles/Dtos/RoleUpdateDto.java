package com.myfiles.Dtos;

import com.myfiles.Entity.Role;
import lombok.Data;


@Data
public class RoleUpdateDto {

    private Role role;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
