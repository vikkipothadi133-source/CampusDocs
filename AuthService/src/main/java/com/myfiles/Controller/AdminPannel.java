package com.myfiles.Controller;

import com.myfiles.Dtos.RoleUpdateDto;
import com.myfiles.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminPannel {

    @Autowired
    private Services services;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/users")
    public String updateRole(@RequestBody RoleUpdateDto dto) {
        return services.updateRole(dto);
    }
}
