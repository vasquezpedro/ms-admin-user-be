package cl.commons.ms.admin.usuario.be.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.commons.ms.admin.usuario.be.commons.AdminValidUtil;
import cl.commons.ms.admin.usuario.be.dto.UpdateUserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserCreateResponseDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDetailDTO;
import cl.commons.ms.admin.usuario.be.service.UserService;

@RestController
@RequestMapping(value = "/user")
@Validated
public class UserController{
	
	private UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService=userService;
	}	
	
	@PostMapping(path="/sign-up", consumes =  MediaType.APPLICATION_JSON_VALUE,	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserCreateResponseDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
		UserCreateResponseDTO salRegUsrDTO=userService.create(userDTO);
		return new ResponseEntity<>(salRegUsrDTO, HttpStatus.CREATED);
	}

	@GetMapping(path = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDetailDTO> getUser(Authentication authentication) {
		UserDetails  userDetails=(UserDetails) authentication.getPrincipal();
		UserDetailDTO salRegUsrDTO=userService.getUserForID(UUID.fromString(userDetails.getUsername()));
		return new ResponseEntity<>(salRegUsrDTO, HttpStatus.OK);
	}

	@PutMapping(consumes =  MediaType.APPLICATION_JSON_VALUE,	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDetailDTO> updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO,Authentication authentication) {
		AdminValidUtil.validateUserId(authentication, updateUserDTO.getId());
		UserDetailDTO salRegUsrDTO=userService.update(updateUserDTO);
		return new ResponseEntity<>(salRegUsrDTO, HttpStatus.OK);
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") @Valid UUID userID,Authentication authentication) {
		AdminValidUtil.validateUserId(authentication, userID);
		userService.delete(userID);
		return new ResponseEntity<>(HttpStatus.OK);
	}



}
