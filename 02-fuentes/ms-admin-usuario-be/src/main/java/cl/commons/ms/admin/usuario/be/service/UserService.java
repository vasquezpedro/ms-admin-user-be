package cl.commons.ms.admin.usuario.be.service;

import java.util.UUID;

import cl.commons.ms.admin.usuario.be.dto.UpdateUserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserCreateResponseDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDetailDTO;

public interface UserService {

	UserCreateResponseDTO create(UserDTO userDTO);

    UserDetailDTO getUserForID(UUID userID);

    UserDetailDTO update(UpdateUserDTO updateUserDto);

    void delete(UUID userID);
}
