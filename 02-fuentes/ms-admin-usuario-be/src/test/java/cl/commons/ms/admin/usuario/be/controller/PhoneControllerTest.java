package cl.commons.ms.admin.usuario.be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cl.commons.ms.admin.usuario.be.commons.Constantes;
import cl.commons.ms.admin.usuario.be.commons.EncryptorDecryptor;
import cl.commons.ms.admin.usuario.be.commons.JwtTokenProvider;
import cl.commons.ms.admin.usuario.be.dto.ErrorDTO;
import cl.commons.ms.admin.usuario.be.dto.PhoneDetailDTO;
import cl.commons.ms.admin.usuario.be.entity.User;
import cl.commons.ms.admin.usuario.be.repository.PhoneRepository;
import cl.commons.ms.admin.usuario.be.service.impl.PhoneServiceImpl;
import cl.commons.ms.admin.usuario.be.service.impl.UserDetailsServiceImpl;

@WebMvcTest(PhoneController.class)
@Import({PhoneController.class,JwtTokenProvider.class,EncryptorDecryptor.class})
@ActiveProfiles("test")
public class PhoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhoneServiceImpl phoneServiceImpl;

    @MockBean
    private PhoneRepository phoneRepository;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Mock
    private Authentication authentication;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private EncryptorDecryptor encryptorDecryptor;

    private ObjectMapper objectMapper;
    
    private UUID idUser;
    private UUID idUserRamdon;
    private String token;

    @BeforeEach
    void setup(){
         objectMapper = new ObjectMapper();
         objectMapper.registerModule(new JavaTimeModule());
        idUser=UUID.randomUUID();
        idUserRamdon=UUID.randomUUID();
        token=jwtTokenProvider.generateToken(idUser.toString(), 50000000);
    }

    @Test
    @DisplayName("get endpoint find phones by id user - http status 200")
    public void testEnpointGetPhoneByIdUser() throws Exception {
        Optional<User> userOptional=buildUser(idUser,true);
        List<PhoneDetailDTO> phoneList=buildListoPhone(idUser);
        when(phoneServiceImpl.getPhoneByIdUser(any(UUID.class))).thenReturn(phoneList);
        when(userDetailsServiceImpl.loadUserByUsername(token)).thenReturn(buildDetailUser(userOptional.get()));
       
        byte[] jsonBytes =mockMvc.perform(get("/phone/"+idUser.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn()
                            .getResponse()
                            .getContentAsString(StandardCharsets.UTF_8)
                            .getBytes(StandardCharsets.UTF_8);
                
                List<PhoneDetailDTO> listResponse = objectMapper.readValue(jsonBytes, new TypeReference<List<PhoneDetailDTO>>(){});
                assertNotNull(listResponse);
                assertEquals(phoneList, listResponse);
                
    }

    @Test
    @DisplayName("get endpoint find phones by id user no es igual al token - http status 401")
    public void testEnpointGetPhoneByIdUserNoEqToken() throws Exception {
        Optional<User> userOptional=buildUser(idUser,true);
        List<PhoneDetailDTO> phoneList=buildListoPhone(idUser);
        when(phoneServiceImpl.getPhoneByIdUser(any(UUID.class))).thenReturn(phoneList);
        when(userDetailsServiceImpl.loadUserByUsername(token)).thenReturn(buildDetailUser(userOptional.get()));
       
        byte[] jsonBytes =mockMvc.perform(get("/phone/"+idUserRamdon.toString())
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
    @DisplayName("get endpoint find phones by id user - http status 200")
    public void testEnpointPutPhoneByIdUser() throws Exception {
        Optional<User> userOptional=buildUser(idUser,true);
        PhoneDetailDTO request=buildPhoneDetailDTO(idUser);
        List<PhoneDetailDTO> phoneList=buildListoPhone(idUser);
        when(phoneServiceImpl.updatePhoneByIdUser(any(PhoneDetailDTO.class))).thenReturn(new PhoneDetailDTO(1L,idUser,5125125L,23,"33"));
        when(userDetailsServiceImpl.loadUserByUsername(token)).thenReturn(buildDetailUser(userOptional.get()));
       
        byte[] jsonBytes =mockMvc.perform(put("/phone")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn()
                            .getResponse()
                            .getContentAsString(StandardCharsets.UTF_8)
                            .getBytes(StandardCharsets.UTF_8);
                
                PhoneDetailDTO response = objectMapper.readValue(jsonBytes, PhoneDetailDTO.class);
                assertNotNull(response);
                
    }

    @Test
    @DisplayName("delete endpoint phones by id and id user - http status 200")
    public void testEnpointDeletePhoneByIdandIdUser() throws Exception {
        Optional<User> userOptional=buildUser(idUser,true);
        Long idPhone=1L;
         when(userDetailsServiceImpl.loadUserByUsername(token)).thenReturn(buildDetailUser(userOptional.get()));
         doNothing().when(phoneServiceImpl).deletePhoneById(any(Long.class),any(UUID.class));
       
        mockMvc.perform(delete("/phone/"+idUser.toString()+"/"+idPhone)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    


    private List<PhoneDetailDTO> buildListoPhone(UUID uUID) {
        return List.of(buildPhoneDetailDTO(uUID));
    }

    private PhoneDetailDTO buildPhoneDetailDTO(UUID uUID) {
        return new PhoneDetailDTO(1L,uUID,975386428L,23,"33");
    }


    private UserDetails buildDetailUser(User user) {
        return new org.springframework.security.core.userdetails.User(user.getId().toString(),
         user.getPassword()
        ,new ArrayList<>());
    }

    private Optional<User> buildUser(UUID idUser,boolean isActive) {
        User usr=new User();
        usr.setId(idUser);
        usr.setName("Juan");
        usr.setEmail("juan@rodriguez.org");
        usr.setPassword(encryptorDecryptor.encrypt("H2unter2"));
        usr.setCreated(LocalDateTime.now());
        usr.setLastLogin(LocalDateTime.now());
        usr.setPhones(List.of());
        usr.setIsactive(isActive);
        return Optional.of(usr);
    }
    
}
