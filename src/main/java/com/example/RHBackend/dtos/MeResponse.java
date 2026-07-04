package com.example.RHBackend.dtos;

import com.example.RHBackend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeResponse {
    private Long userId;
    private String username;
    private Role role;
    private Long employeId;
}
