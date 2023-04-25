package cl.commons.ms.admin.usuario.be.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import cl.commons.ms.admin.usuario.be.commons.AdminValidUtil;
import cl.commons.ms.admin.usuario.be.commons.Constantes;
import cl.commons.ms.admin.usuario.be.dto.PhoneDetailDTO;
import cl.commons.ms.admin.usuario.be.entity.Phone;
import cl.commons.ms.admin.usuario.be.entity.User;
import cl.commons.ms.admin.usuario.be.exception.BusinessException;
import cl.commons.ms.admin.usuario.be.repository.PhoneRepository;
import cl.commons.ms.admin.usuario.be.service.PhoneService;

@Service
public class PhoneServiceImpl implements PhoneService{

    private PhoneRepository phoneRepository;

    @Autowired
    public PhoneServiceImpl(PhoneRepository phoneRepository){
        this.phoneRepository=phoneRepository;
    }

    @Override
    public List<PhoneDetailDTO> getPhoneByIdUser(UUID idUser) {
        List<Phone> list=phoneRepository.findByIdUser(idUser)
        .orElseThrow(()-> new BusinessException(HttpStatus.BAD_REQUEST, Constantes.MENSAJE_ERROR_PHONE_ID_USER));
        
        return list.stream()
            .map(phone-> new PhoneDetailDTO(phone.getId(),phone.getUser().getId(),phone.getNumber(),phone.getCityCode(),phone.getContryCode()))
            .collect(Collectors.toList());
    }

    @Override
    public PhoneDetailDTO updatePhoneByIdUser(PhoneDetailDTO phoneDetailDTO) {
        Phone phone= phoneRepository.findById(phoneDetailDTO.getId())
        .orElseThrow(()->new BusinessException(HttpStatus.BAD_REQUEST, Constantes.MENSAJE_ERROR_PHONE_ID_USER));
        
        if(phoneDetailDTO.getCityCode()!=phone.getCityCode()){
            phone.setCityCode(phoneDetailDTO.getCityCode());
        }
        if(phoneDetailDTO.getNumber()!=phone.getNumber()){
            phone.setNumber(phoneDetailDTO.getNumber());
        }

        phone.setContryCode(AdminValidUtil.setIfANoEqualsB(phoneDetailDTO.getContryCode(), phone.getContryCode()));
        phone.getUser().setModified(LocalDateTime.now());
        phone=phoneRepository.save(phone);
        return toPhoneDetailDTO(phone);
    }

    public PhoneDetailDTO toPhoneDetailDTO(Phone phone){
        return new PhoneDetailDTO(phone.getId(),phone.getUser().getId(),phone.getNumber(),phone.getCityCode(),phone.getContryCode());
    }

    @Override
    public void deletePhoneById(Long id,UUID uuId) {
        Phone phone=phoneRepository.findByIdUserIdPhone(uuId, id)
        .orElseThrow(()-> new BusinessException(HttpStatus.BAD_REQUEST, Constantes.MENSAJE_ERROR_PHONE_ID_USER));
        phone.getUser().setModified(LocalDateTime.now());
        phone=phoneRepository.save(phone);
        phoneRepository.delete(phone);
    }

    @Override
    public PhoneDetailDTO savePhone(PhoneDetailDTO phoneDetailDTO) {
      
        phoneRepository.findByIdUserIdPhoneAndNumber(phoneDetailDTO.getIdUser(),phoneDetailDTO.getNumber(),phoneDetailDTO.getCityCode(),phoneDetailDTO.getContryCode())
            .ifPresent(u -> {
                throw new BusinessException(HttpStatus.BAD_REQUEST, Constantes.MENSAJE_ERROR_PHONE_EXISTE);
            });
            
            User user= new User();
            user.setId(phoneDetailDTO.getIdUser());
            Phone phone= new Phone(phoneDetailDTO.getNumber(),phoneDetailDTO.getCityCode(),phoneDetailDTO.getContryCode(),user);
            phone=phoneRepository.save(phone);
        
        return toPhoneDetailDTO(phone);
    }
    
}
