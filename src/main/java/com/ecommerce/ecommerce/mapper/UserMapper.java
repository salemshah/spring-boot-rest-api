package com.ecommerce.ecommerce.mapper;

import com.ecommerce.ecommerce.dto.UserAddressDTO;
import com.ecommerce.ecommerce.dto.UserRequestDTO;
import com.ecommerce.ecommerce.dto.UserResponseDTO;
import com.ecommerce.ecommerce.entity.User;
import com.ecommerce.ecommerce.entity.UserAddress;
import com.ecommerce.ecommerce.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserAddressMapper userAddressMapper;

    public User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;

        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phon(dto.getPhon())
                .userRole(UserRole.CUSTOMER)
                .build();

        // Map address from DTO to Entity
        if (dto.getAddresses() != null) {
            List<UserAddress> userAddressList = dto.getAddresses()
                    .stream()
                    .map(userAddressMapper::toEntity)
                    .toList();

            // Set user reference in each address
            userAddressList.forEach(address -> address.setUser(user));

            user.setAddresses(userAddressList);
        }

        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhon(user.getPhon());
        dto.setEmail(user.getEmail());
        dto.setUserRole(user.getUserRole());

        if (user.getAddresses() != null) {
            List<UserAddressDTO> addressDTOList = user.getAddresses()
                    .stream()
                    .map(userAddressMapper::toDTO)
                    .toList();
            dto.setAddresses(addressDTOList);
        }
        return dto;
    }
}
