package cl.commons.ms.admin.usuario.be.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import cl.commons.ms.admin.usuario.be.commons.Constantes;
import cl.commons.ms.admin.usuario.be.commons.EncryptorDecryptor;
import cl.commons.ms.admin.usuario.be.commons.JwtTokenProvider;
import cl.commons.ms.admin.usuario.be.dto.PhoneDTO;
import cl.commons.ms.admin.usuario.be.dto.UpdateUserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserCreateResponseDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDetailDTO;
import cl.commons.ms.admin.usuario.be.entity.User;
import cl.commons.ms.admin.usuario.be.exception.BusinessException;
import cl.commons.ms.admin.usuario.be.repository.UserRepository;
import cl.commons.ms.admin.usuario.be.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
	private EncryptorDecryptor encryptorDecryptor;

    @InjectMocks
    private UserServiceImpl userService;
    
    private UserDTO userDto;
    
    private User usr;
    private String token;
    @BeforeEach
    void setup(){
        token="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKdWFuIFJvZHJpZ3VleiIsImlhdCI6MTY4MTE4NjQ1OSwiZXhwIjoxNjgxNzA0ODU5fQ.P_gF8Y83ATM2GDC15PD49vD97OxE8dRsVFsGaKumLQY";
        userDto = new UserDTO();
        userDto.setEmail("juan@rodriguez.org");
        userDto.setPassword("H2unter2");
        userDto.setName("Juan Rodriguez");
        userDto.setPhones(List.of(new PhoneDTO(1234567L, 1, "57")));
        usr = new User();
        usr.setId(UUID.randomUUID());
		usr.setEmail(userDto.getEmail());
		usr.setName(userDto.getName());
		usr.setPassword("r80QWAp+54+YkqHdVMQOvw==");
		usr.setCreated(LocalDateTime.now());
		usr.setLastLogin(LocalDateTime.now());
		usr.setToken("///TN8whBGIE9qFw+y37tVPPzUkRcp9fX1MCt8/XqagSp");
		usr.setIsactive(true);

    }
    


    @Test
    @DisplayName("error al crear usuario con email existente")
    public void testUserCreatedEmailExiste() throws Exception {

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(new User()));
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.create(userDto);
        });
                assertNotNull(exception);
                assertEquals(Constantes.MENSAJE_ERROR_EMAIL_EXISTE,exception.getMessage());
                assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
    }

    @Test
    @DisplayName("crear usuario")
    public void testUserCreated() throws Exception {
        
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(usr);
           UserCreateResponseDTO response= userService.create(userDto);
           assertEquals(usr.getId(),response.getId());
           assertTrue(response.isIsactive());
    }

    @Test
    @DisplayName("buscar usuario")
    public void testUserFind() throws Exception {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(usr));
        when(userRepository.save(any(User.class))).thenReturn(usr);
        UserDetailDTO response= userService.getUserForID(usr.getId());
            assertNotNull(response);
           assertEquals(usr.getId(),response.getId());
    }

    @Test
    @DisplayName("buscar usuario con id inexistente")
   public void testUserFindEmpty() throws Exception {
       when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
       BusinessException exception = assertThrows(BusinessException.class, () -> {
           userService.getUserForID(usr.getId());
       });
               assertNotNull(exception);
               assertEquals(Constantes.MENSAJE_ERROR_ID_USER_INEXISTE,exception.getMessage());
               assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
   }

   @Test
   @DisplayName("update usuario")
   public void testUserUpdate() throws Exception {
    UpdateUserDTO usrUpdate= buildUpdateUserDTO(usr);
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(usr));
    when(userRepository.save(any(User.class))).thenReturn(usr);
       UserDetailDTO response= userService.update(usrUpdate);
        assertNotNull(response);
        assertEquals(usrUpdate.getId(),response.getId());
   }

 



@Test
   @DisplayName("id usuario inexistente lanza excepcion")
  public void testUpdateUserFindEmpty() throws Exception {
      when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
      UpdateUserDTO usrUpdate= buildUpdateUserDTO(usr);
      BusinessException exception = assertThrows(BusinessException.class, () -> {
          userService.update(usrUpdate);
      });
              assertNotNull(exception);
              assertEquals(Constantes.MENSAJE_ERROR_ID_USER_INEXISTE,exception.getMessage());
              assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
  }

  @Test
  @DisplayName("delete usuario")
  public void testUserDelete() throws Exception {
   when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(usr));
   doNothing().when(userRepository).delete(any(User.class));
    userService.delete(usr.getId());
    verify(userRepository, times(1)).delete(any(User.class));
  }

  private UpdateUserDTO buildUpdateUserDTO(User usr2) {
    UpdateUserDTO updateUsrDto= new UpdateUserDTO(usr2.getId(), "updateuser@gmail.com", "2Hgeswe3", "user", List.of(new PhoneDTO(98574215L,56,"35")));
    return updateUsrDto;
}
    


}