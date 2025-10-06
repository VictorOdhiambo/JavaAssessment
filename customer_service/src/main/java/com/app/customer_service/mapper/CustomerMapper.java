package com.app.customer_service.mapper;

import com.app.customer_service.dto.CustomerDto;
import com.app.customer_service.entity.Customer;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDto toDto(Customer customer);

    @InheritInverseConfiguration
    Customer toEntity(CustomerDto customerDto);
}
