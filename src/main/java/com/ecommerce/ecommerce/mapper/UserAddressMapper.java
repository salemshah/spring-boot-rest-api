package com.ecommerce.ecommerce.mapper;

import com.ecommerce.ecommerce.dto.UserAddressDTO;
import com.ecommerce.ecommerce.entity.UserAddress;
import org.springframework.stereotype.Component;

@Component
public class UserAddressMapper {

    public UserAddress toEntity(UserAddressDTO dto) {
        if (dto == null) return null;
        UserAddress address = new UserAddress();
        address.setId(dto.getId());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        return address;
    }

    public UserAddressDTO toDTO(UserAddress address) {
        if (address == null) return null;
        UserAddressDTO dto = new UserAddressDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());
        dto.setPostalCode(address.getPostalCode());
        return dto;
    }
}
