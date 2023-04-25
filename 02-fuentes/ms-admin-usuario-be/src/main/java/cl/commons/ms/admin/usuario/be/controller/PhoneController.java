package cl.commons.ms.admin.usuario.be.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import cl.commons.ms.admin.usuario.be.dto.PhoneDetailDTO;
import cl.commons.ms.admin.usuario.be.service.PhoneService;

@RestController
@RequestMapping(value = "/phone")
@Validated
public class PhoneController {

    private PhoneService phoneService;
    
    @Autowired
    public PhoneController(PhoneService phoneService){
        this.phoneService=phoneService;
    }

    @GetMapping(path = "/{id}",	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PhoneDetailDTO>> getPhoneUser(@PathVariable(value = "id") @Valid UUID idUser,Authentication authentication) {
		AdminValidUtil.validateUserId(authentication, idUser);
		List<PhoneDetailDTO> listPhone=phoneService.getPhoneByIdUser(idUser);
		return new ResponseEntity<>(listPhone, HttpStatus.OK);
	}

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PhoneDetailDTO> updatePhoneUser(@RequestBody @Valid PhoneDetailDTO phoneDetailDTO,Authentication authentication) {
		AdminValidUtil.validateUserId(authentication, phoneDetailDTO.getIdUser());
		PhoneDetailDTO phoneDetailDto=phoneService.updatePhoneByIdUser(phoneDetailDTO);
		return new ResponseEntity<>(phoneDetailDto,HttpStatus.OK);
	}

    @DeleteMapping(path = "/{iduser}/{id}")
	public ResponseEntity<Void> deletePhoneById(@PathVariable(value = "iduser") @Valid UUID idUser,@PathVariable(value = "id") @Valid Long id,Authentication authentication) {
		AdminValidUtil.validateUserId(authentication, idUser);
		phoneService.deletePhoneById(id,idUser);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PhoneDetailDTO> insertPhoneUser(@RequestBody @Valid PhoneDetailDTO phoneDetailDTO,Authentication authentication) {
		AdminValidUtil.validateUserId(authentication, phoneDetailDTO.getIdUser());
		PhoneDetailDTO phoneDetailDto=phoneService.savePhone(phoneDetailDTO);
		return new ResponseEntity<>(phoneDetailDto,HttpStatus.CREATED);
	}
    
}
