package cl.commons.ms.admin.usuario.be.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import cl.commons.ms.admin.usuario.be.commons.Constantes;
import cl.commons.ms.admin.usuario.be.dto.PhoneDetailDTO;
import cl.commons.ms.admin.usuario.be.entity.Phone;
import cl.commons.ms.admin.usuario.be.entity.User;
import cl.commons.ms.admin.usuario.be.exception.BusinessException;
import cl.commons.ms.admin.usuario.be.repository.PhoneRepository;
import cl.commons.ms.admin.usuario.be.service.impl.PhoneServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PhoneServiceTest {

    @Mock
    private PhoneRepository phoneRepository;
    @InjectMocks
    private PhoneServiceImpl phoneService;


    
    @Test
    @DisplayName("el usuario no registra telefonos asociados")
    public void testGetPhoneByIdUserNoPhone() throws Exception {

        when(phoneRepository.findByIdUser(any(UUID.class))).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            phoneService.getPhoneByIdUser(UUID.randomUUID());
        });
                assertNotNull(exception);
                assertEquals(Constantes.MENSAJE_ERROR_PHONE_ID_USER,exception.getMessage());
                assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
    }

    @Test
    @DisplayName("obtener telefonos asociados")
    public void testGetPhoneByIdUser() throws Exception {
        User user= new User();
        user.setId(UUID.randomUUID());
        Optional<List<Phone>> phoneOptional=Optional.of(List.of(new Phone(1l, 12345678L, 3, "34", user)));
        when(phoneRepository.findByIdUser(any(UUID.class))).thenReturn(phoneOptional);
        List<PhoneDetailDTO> ls=phoneService.getPhoneByIdUser(UUID.randomUUID());
                assertNotNull(ls);
                assertEquals(phoneOptional.get().size(),ls.size());
    }

    @Test
    @DisplayName("Modificar telefono")
    public void testUpdatePhoneByIdUser() throws Exception {
        PhoneDetailDTO phoneDetailDTO= new PhoneDetailDTO();
        phoneDetailDTO.setCityCode(1);
        phoneDetailDTO.setContryCode("2");
        phoneDetailDTO.setId(1L);
        phoneDetailDTO.setNumber(89745612L);
        User user= new User();
        user.setId(UUID.randomUUID());
        phoneDetailDTO.setIdUser(user.getId());
        Optional<Phone> phoneOptional=Optional.of(new Phone(1l, 89745612L, 3, "34", user));
        when(phoneRepository.findById(any(Long.class))).thenReturn(phoneOptional);
        when(phoneRepository.save(any(Phone.class))).thenReturn(phoneOptional.get());
        PhoneDetailDTO phoneDetailDto=phoneService.updatePhoneByIdUser(phoneDetailDTO);
        assertNotNull(phoneDetailDto);
        assertEquals(phoneDetailDTO.getNumber(),phoneDetailDto.getNumber());
    }

    @Test
    @DisplayName("el usuario no registra telefonos asociados")
    public void testUpdatePhoneByIdUserNoExistenTelefono() throws Exception {
        PhoneDetailDTO phoneDetailDTO= new PhoneDetailDTO();
        phoneDetailDTO.setCityCode(1);
        phoneDetailDTO.setContryCode("2");
        phoneDetailDTO.setId(1L);
        phoneDetailDTO.setNumber(89745612L);
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            phoneService.updatePhoneByIdUser(phoneDetailDTO);
        });
                assertNotNull(exception);
                assertEquals(Constantes.MENSAJE_ERROR_PHONE_ID_USER,exception.getMessage());
                assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
    }

    @Test
    @DisplayName("el usuario no registra telefonos asociados para eliminar")
    public void testDeletePhoneError() throws Exception {
        
        when(phoneRepository.findByIdUserIdPhone(any(UUID.class),any(Long.class))).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            phoneService.deletePhoneById(1L,UUID.randomUUID());
        });
                assertNotNull(exception);
                assertEquals(Constantes.MENSAJE_ERROR_PHONE_ID_USER,exception.getMessage());
                assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
    }

    @Test
    @DisplayName("delete phone")
    public void testDeletePhone() throws Exception {
        PhoneDetailDTO phoneDetailDTO= new PhoneDetailDTO();
        phoneDetailDTO.setCityCode(1);
        phoneDetailDTO.setContryCode("2");
        phoneDetailDTO.setId(1L);
        phoneDetailDTO.setNumber(89745612L);
        User user= new User();
        user.setId(UUID.randomUUID());
        phoneDetailDTO.setIdUser(user.getId());
        Optional<Phone> phoneOptional=Optional.of(new Phone(1l, 89745612L, 3, "34", user));
        when(phoneRepository.findByIdUserIdPhone(any(UUID.class),any(Long.class))).thenReturn(phoneOptional);
        when(phoneRepository.save(any(Phone.class))).thenReturn(phoneOptional.get());
        doNothing().when(phoneRepository).delete(any(Phone.class));
            phoneService.deletePhoneById(1L,UUID.randomUUID());
        verify(phoneRepository,times(1)).delete(any(Phone.class));
    }

    

}
