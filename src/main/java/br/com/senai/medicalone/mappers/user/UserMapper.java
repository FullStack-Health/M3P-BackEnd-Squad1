package br.com.senai.medicalone.mappers.user;

import br.com.senai.medicalone.dtos.user.UserRequestDTO;
import br.com.senai.medicalone.dtos.user.UserResponseDTO;
import br.com.senai.medicalone.entities.user.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public User toEntity(UserRequestDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public UserResponseDTO toResponseDTO(User entity) {
        return modelMapper.map(entity, UserResponseDTO.class);
    }
}