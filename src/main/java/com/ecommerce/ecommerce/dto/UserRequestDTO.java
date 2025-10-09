package com.ecommerce.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UserRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 254, message = "Email must be at most 254 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{10,14}$",
            message = "Phone must be 10–14 digits, optional leading +"
    )
    private String phon;

    // Validate the list itself (size limits) AND each UserAddressDTO element
    @Size(max = 5, message = "You can provide at most 5 addresses")
    private List<@Valid UserAddressDTO> addresses;
}
