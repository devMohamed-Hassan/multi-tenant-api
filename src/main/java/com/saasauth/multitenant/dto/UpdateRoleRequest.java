package com.saasauth.multitenant.dto;

import com.saasauth.multitenant.model.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {

     @NotNull(message = "Role is required")
     private Role role;
}