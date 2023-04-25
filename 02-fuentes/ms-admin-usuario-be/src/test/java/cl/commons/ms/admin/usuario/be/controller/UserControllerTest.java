package cl.commons.ms.admin.usuario.be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cl.commons.ms.admin.usuario.be.commons.Constantes;
import cl.commons.ms.admin.usuario.be.commons.EncryptorDecryptor;
import cl.commons.ms.admin.usuario.be.commons.JwtTokenProvider;
import cl.commons.ms.admin.usuario.be.dto.ErrorDTO;
import cl.commons.ms.admin.usuario.be.dto.PhoneDTO;
import cl.commons.ms.admin.usuario.be.dto.UpdateUserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserCreateResponseDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDetailDTO;
import cl.commons.ms.admin.usuario.be.entity.User;
import cl.commons.ms.admin.usuario.be.repository.UserRepository;
import cl.commons.ms.admin.usuario.be.service.impl.UserDetailsServiceImpl;
import cl.commons.ms.admin.usuario.be.service.impl.UserServiceImpl;

@WebMvcTest(UserController.class)
@Import({UserController.class,JwtTokenProvider.class,EncryptorDecryptor.class})
@ActiveProfiles("test")
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Mock
    private Authentication authentication;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private EncryptorDecryptor encryptorDecryptor;

    private ObjectMapper objectMapper;

    private UserDTO requestDTO;
    
    private UUID idUser;
    private UUID idUserRamdon;
    private String token;
    
    @BeforeEach
    void setup(){
         objectMapper = new ObjectMapper();
         objectMapper.registerModule(new JavaTimeModule());
        requestDTO = new UserDTO();
        requestDTO.setEmail("juan@rodriguez.org");
        requestDTO.setPassword("H2unter2");
        requestDTO.setName("Juan Rodriguez");
        requestDTO.setPhones(List.of(new PhoneDTO(975386428L, 1, "57")));
        idUser=UUID.randomUUID();
        idUserRamdon=UUID.randomUUID();
        token=jwtTokenProvider.generateToken(idUser.toString(), 50000000);
    }
    
    @Test
    @DisplayName("/sign-up crear usuario formato password invalido - http status 400")
    public void testEndpointPostUserInvalidPassword() throws Exception {
        String invalidPassword="hunter2";
        requestDTO.setPassword(invalidPassword);
        byte[] jsonBytes =mockMvc.perform(post("/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8)
                .getBytes(StandardCharsets.UTF_8);
                
                ErrorDTO erroDto = objectMapper.readValue(jsonBytes, ErrorDTO.class);
                assertEquals(Constantes.MENSAJE_ERROR_PASSWORD, erroDto.getError().get(0).getDetail());
    }

    @Test
    @DisplayName("post endpoint crear usuario formato email invalido - http status 400")
    public void testEndpointPostUserInvalidEmail() throws Exception {
        String invalidEmail="emailinvalido.com";
        requestDTO.setEmail(invalidEmail);
        byte[] jsonBytes =mockMvc.perform(post("/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8)
                .getBytes(StandardCharsets.UTF_8);
                
                ErrorDTO erroDto = objectMapper.readValue(jsonBytes, ErrorDTO.class);
                assertEquals(Constantes.MENSAJE_ERROR_EMAIL, erroDto.getError().get(0).getDetail());
    }

    @Test
    @DisplayName("post endpoint crear usuario - http status 201")
    public void testEnpointPostUserCreated() throws Exception {
        when(userService.create(any(UserDTO.class))).thenReturn(getUserCreateResponse());
        byte[] jsonBytes =mockMvc.perform(post("/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8)
                .getBytes(StandardCharsets.UTF_8);
                
                UserCreateResponseDTO responseDto = objectMapper.readValue(jsonBytes, UserCreateResponseDTO.class);
                assertNotNull(responseDto);
                assertNotNull(responseDto.getToken());
                assertNotNull(responseDto.getId());
                assertTrue(responseDto.isIsactive());
                assertEquals(responseDto.getCreated(),responseDto.getLastLogin());
    }

    @Test
    @DisplayName("get endpoint find usuario retorno - http status 200")
    public void testEnpointGetUsuario() throws Exception {
        Optional<User> userOptional=buildUser(idUser,true);
        when(userRepository.findByToken(any(String.class))).thenReturn(userOptional);
        when(userService.getUserForID(any(UUID.class))).thenReturn(buildUserDetail(userOptional.get()));
        when(userDetailsServiceImpl.loadUserByUsername(token)).thenReturn(buildDetailUser(userOptional.get()));
       
        byte[] jsonBytes =mockMvc.perform(get("/user/login")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn()
                            .getResponse()
                            .getContentAsString(StandardCharsets.UTF_8)
                            .getBytes(StandardCharsets.UTF_8);
                
                UserDetailDTO responseDto = objectMapper.readValue(jsonBytes, UserDetailDTO.class);
                assertNotNull(responseDto);
                
    }

    @Test
    @DisplayName("/login endpoint - token invalido - http status 403")
    public void testEnpointGetUsuarioIdNoEqToken() throws Exception {
        Optional<User> userOptional=buildUser(idUser,true);
        when(userRepository.findByToken(any(String.class))).thenReturn(userOptional);
        when(userService.getUserForID(any(UUID.class))).thenReturn(buildUserDetail(userOptional.get()));
        when(userDetailsServiceImpl.loadUserByUsername(token)).thenReturn(buildDetailUser(userOptional.get()));
       
        byte[] jsonBytes =mockMvc.perform(get("/user/login")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token+"L")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()).andReturn()
                            .getResponse()
                            .getContentAsString(StandardCharsets.UTF_8)
                            .getBytes(StandardCharsets.UTF_8);
                
    }

    @Test
    @DisplayName("delete endpoint  usuario - http status 200")
    public void testEnpointDeleteUsuario() throws Exception {
        Optional<User> userOptional=buildUser(idUser,true);
        when(userRepository.findByToken(any(String.class))).thenReturn(userOptional);
        doNothing().when(userService).delete(any(UUID.class));
        when(userDetailsServiceImpl.loadUserByUsername(token)).thenReturn(buildDetailUser(userOptional.get()));
       
        mockMvc.perform(delete("/user/"+idUser.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn()
                            .getResponse()
                            .getContentAsString(StandardCharsets.UTF_8)
                            .getBytes(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("delete endpoint  usuario id usuario no es mismo del token - http status 401")
    public void testEnpointDeleteUsuarioIdNoEqToken() throws Exception {
        Optional<User> userOptional=buildUser(idUser,true);
        when(userRepository.findByToken(any(String.class))).thenReturn(userOptional);
        doNothing().when(userService).delete(any(UUID.class));
        when(userDetailsServiceImpl.loadUserByUsername(token)).thenReturn(buildDetailUser(userOptional.get()));
       
        byte[] jsonBytes =mockMvc.perform(delete("/user/"+idUserRamdon.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn()
                            .getResponse()
                            .getContentAsString(StandardCharsets.UTF_8)
                            .getBytes(StandardCharsets.UTF_8);
        ErrorDTO erroDto = objectMapper.readValue(jsonBytes, ErrorDTO.class);
        assertEquals(Constantes.MENSAJE_ERROR_UNAUTHORIZED, erroDto.getError().get(0).getDetail());
                            
    }

    @Test
    @DisplayName("put endpoint  usuario - http status 200")
    public void testEnpointPutUsuario() throws Exception {
        Optional<User> userOptional=buildUser(idUser,true);
         UpdateUserDTO request=buildUpdateUserDTO(idUser);
        when(userRepository.findByToken(any(String.class))).thenReturn(userOptional);
        when(userDetailsServiceImpl.loadUserByUsername(token)).thenReturn(buildDetailUser(userOptional.get()));
       
        when(userService.update(any(UpdateUserDTO.class))).thenReturn(buildUserDetail(userOptional.get(),request));
       
        byte[] jsonBytes =mockMvc.perform(put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8)
                .getBytes(StandardCharsets.UTF_8);
                
                UserDetailDTO responseDto = objectMapper.readValue(jsonBytes, UserDetailDTO.class);
                assertNotNull(responseDto);
                assertEquals(request.getEmail(),responseDto.getEmail());
    }

    


    
    private UserDetails buildDetailUser(User user) {
        return new org.springframework.security.core.userdetails.User(user.getId().toString(),
         user.getPassword()
        ,new ArrayList<>());
    }

    private UserDetailDTO buildUserDetail(User user) {
        UserDetailDTO userDetailDTO= new UserDetailDTO();
		userDetailDTO.setId(user.getId());
		userDetailDTO.setCreated(user.getCreated()); 
		userDetailDTO.setIsactive(user.isIsactive()); 
		userDetailDTO.setLastLogin(user.getLastLogin()); 
		userDetailDTO.setModified(user.getModified());
		userDetailDTO.setName(user.getName());
		userDetailDTO.setEmail(user.getEmail());
		List<PhoneDTO> phones=user.getPhones().stream()
						.map( phone ->new PhoneDTO(phone.getNumber(),
											phone.getCityCode(),
											phone.getContryCode()))
						.collect(Collectors.toList());

		userDetailDTO.setPhones(phones);
		userDetailDTO.setToken(encryptorDecryptor.decrypt(user.getToken()));
		return userDetailDTO;
    }

    private UserDetailDTO buildUserDetail(User user,UpdateUserDTO udtUsrDto) {
        UserDetailDTO userDetailDTO= new UserDetailDTO();
		userDetailDTO.setId(user.getId());
		userDetailDTO.setCreated(user.getCreated()); 
		userDetailDTO.setIsactive(user.isIsactive()); 
		userDetailDTO.setLastLogin(user.getLastLogin()); 
		userDetailDTO.setModified(user.getModified());
		userDetailDTO.setName(udtUsrDto.getName());
		userDetailDTO.setEmail(udtUsrDto.getEmail());
		List<PhoneDTO> phones=udtUsrDto.getPhones().stream()
						.map( phone ->new PhoneDTO(phone.getNumber(),
											phone.getCityCode(),
											phone.getContryCode()))
						.collect(Collectors.toList());

		userDetailDTO.setPhones(phones);
		userDetailDTO.setToken(encryptorDecryptor.decrypt(user.getToken()));
		return userDetailDTO;
    }

    private Optional<User> buildUser(UUID idUser,boolean isActive) {
        User usr=new User();
        usr.setId(idUser);
        usr.setName(requestDTO.getName());
        usr.setEmail(requestDTO.getEmail());
        usr.setPassword(encryptorDecryptor.encrypt(requestDTO.getPassword()));
        usr.setCreated(LocalDateTime.now());
        usr.setLastLogin(LocalDateTime.now());
        usr.setPhones(List.of());
        usr.setIsactive(isActive);
        return Optional.of(usr);
    }

    private UpdateUserDTO buildUpdateUserDTO(UUID idUser) {
        UpdateUserDTO uptUserDto= new UpdateUserDTO();
        uptUserDto.setEmail("test@test.cl");
        uptUserDto.setId(idUser);
        uptUserDto.setName("test");
        uptUserDto.setPassword("H2unter1");
        uptUserDto.setPhones(List.of(new PhoneDTO(975386428L,25,"01")));
        return uptUserDto;
    }
    

    private UserCreateResponseDTO getUserCreateResponse() {
        UserCreateResponseDTO userResponseDto= new UserCreateResponseDTO();
        userResponseDto.setId(UUID.randomUUID());
        userResponseDto.setLastLogin(LocalDateTime.now());
        userResponseDto.setCreated(LocalDateTime.now());
        userResponseDto.setIsactive(true);
        userResponseDto.setToken(jwtTokenProvider.generateToken(requestDTO.getName(), 522240));
        return userResponseDto;
    }


}