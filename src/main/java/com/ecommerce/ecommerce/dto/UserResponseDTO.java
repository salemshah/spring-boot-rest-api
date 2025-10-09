package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.enums.UserRole;
import lombok.Data;

import java.util.List;

@Data
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phon;
    private UserRole userRole;
    private List<UserAddressDTO> addresses;
}
