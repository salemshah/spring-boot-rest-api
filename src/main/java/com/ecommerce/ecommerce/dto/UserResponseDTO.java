package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.enums.UserRole;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phon;
    private UserRole userRole;
}
