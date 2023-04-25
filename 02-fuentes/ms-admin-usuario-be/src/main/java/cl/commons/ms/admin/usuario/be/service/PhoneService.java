package cl.commons.ms.admin.usuario.be.service;

import java.util.List;
import java.util.UUID;

import cl.commons.ms.admin.usuario.be.dto.PhoneDetailDTO;

public interface PhoneService {

    List<PhoneDetailDTO> getPhoneByIdUser(UUID idUser);

    PhoneDetailDTO updatePhoneByIdUser(PhoneDetailDTO phoneDetailDTO);

    void deletePhoneById(Long id,UUID uuId);

    PhoneDetailDTO savePhone(PhoneDetailDTO phoneDetailDTO);
    
}
