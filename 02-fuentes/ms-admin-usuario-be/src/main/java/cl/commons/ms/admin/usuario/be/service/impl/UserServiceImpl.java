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
import cl.commons.ms.admin.usuario.be.commons.EncryptorDecryptor;
import cl.commons.ms.admin.usuario.be.commons.JwtTokenProvider;
import cl.commons.ms.admin.usuario.be.dto.PhoneDTO;
import cl.commons.ms.admin.usuario.be.dto.UpdateUserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserCreateResponseDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDTO;
import cl.commons.ms.admin.usuario.be.dto.UserDetailDTO;
import cl.commons.ms.admin.usuario.be.entity.Phone;
import cl.commons.ms.admin.usuario.be.entity.User;
import cl.commons.ms.admin.usuario.be.exception.BusinessException;
import cl.commons.ms.admin.usuario.be.repository.UserRepository;
import cl.commons.ms.admin.usuario.be.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	private UserRepository userRepository;
	private JwtTokenProvider jwtTokenProvider;
	private EncryptorDecryptor encryptorDecryptor;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository,
	JwtTokenProvider jwtTokenProvider,
	EncryptorDecryptor encryptorDecryptor) {
		this.userRepository=userRepository;
		this.jwtTokenProvider=jwtTokenProvider;
		this.encryptorDecryptor=encryptorDecryptor;
	}


	@Override
	public UserCreateResponseDTO create(UserDTO userDTO) {
		validaCorreo(userDTO.getEmail());
		User user=mapUser(userDTO);
		user=userRepository.save(user);
		if(null!=userDTO.getPhones()){
			List<Phone> phoneList=mapPhoneList(userDTO,user);
			user.setPhones(phoneList);
			user=userRepository.save(user);
		}
		
		return mapEntityToDTO(user);
	}


	@Override
	public UserDetailDTO getUserForID(UUID userID) {
		User user= userRepository.findById(userID)
					.orElseThrow(()-> new BusinessException(HttpStatus.BAD_REQUEST,Constantes.MENSAJE_ERROR_ID_USER_INEXISTE));
		user.setLastLogin(LocalDateTime.now());
		user.setToken(encryptorDecryptor.encrypt(generarToken(user.getEmail())));
		user=userRepository.save(user);
		UserDetailDTO userDetailDTO= mapUserDetail(user);
		return userDetailDTO;
	}

	@Override
	public UserDetailDTO update(UpdateUserDTO updateUserDto) {
		
		User user= userRepository.findById(updateUserDto.getId())
					.orElseThrow(()-> new BusinessException(HttpStatus.BAD_REQUEST,Constantes.MENSAJE_ERROR_ID_USER_INEXISTE));
			
			if(null!=updateUserDto.getEmail() && !updateUserDto.getEmail().equalsIgnoreCase(user.getEmail())){
				validaCorreo(updateUserDto.getEmail());
				user.setEmail(updateUserDto.getEmail());
			}
			user.setName(AdminValidUtil.setIfANoEqualsB(updateUserDto.getName(), user.getName()));
			if(null!=updateUserDto.getPassword()){
				user.setPassword(encryptorDecryptor.encrypt(AdminValidUtil.setIfANoEqualsB(updateUserDto.getPassword(), encryptorDecryptor.decrypt(user.getPassword()))));
			}
			
			if(null!=updateUserDto.getPhones() && !updateUserDto.getPhones().isEmpty()){
				List<PhoneDTO> listPhoneDto=eliminarDuplicados(updateUserDto.getPhones());
				mergeListPhone(listPhoneDto,user);
			}
			user.setToken(encryptorDecryptor.encrypt(generarToken(user.getEmail())));
			user.setModified(LocalDateTime.now());
			user=userRepository.save(user);
			return mapUserDetail(user);
	}





	private void mergeListPhone(List<PhoneDTO> listPhoneDto,User user) {
		if(null==user.getPhones() || user.getPhones().isEmpty()){
			
			user.setPhones(listPhoneDto.stream()
			.map(dto-> new Phone(dto.getNumber(),dto.getCityCode(),dto.getContryCode(),user))
			.collect(Collectors.toList()));

		}else{

			user.getPhones().addAll(
				listPhoneDto.stream()
        .filter(dto -> user.getPhones().stream()
                        .noneMatch(p -> dto.getCityCode()==p.getCityCode() 
									&& dto.getContryCode().equals(p.getContryCode())
									&& dto.getNumber()==p.getNumber() 
								)
				)
		.map(dto-> new Phone(dto.getNumber(),dto.getCityCode(),dto.getContryCode(),user))
        .collect(Collectors.toList())
		);
		}
	}


	private List<PhoneDTO> eliminarDuplicados(List<PhoneDTO> phones) {
		return phones.stream()
		.distinct().collect(Collectors.toList());
	}


	@Override
	public void delete(UUID userID) {
		User user=userRepository.findById(userID).get();
		userRepository.delete(user);
	}


	private void validaCorreo(String email) throws BusinessException{
		userRepository.findByEmail(email).ifPresent(u -> {
			throw new BusinessException(HttpStatus.BAD_REQUEST,Constantes.MENSAJE_ERROR_EMAIL_EXISTE);
		});
	}


	private UserCreateResponseDTO mapEntityToDTO(User user) {
		UserCreateResponseDTO salidaDTO= new UserCreateResponseDTO();
		salidaDTO.setCreated(user.getCreated());
		salidaDTO.setId(user.getId());
		salidaDTO.setIsactive(user.isIsactive());
		salidaDTO.setLastLogin(user.getLastLogin());
		salidaDTO.setModified(user.getModified());
		salidaDTO.setToken(encryptorDecryptor.decrypt(user.getToken()));
		return salidaDTO;
	}


	private List<Phone> mapPhoneList(UserDTO userDTO,User user) {
		List<PhoneDTO> phoneDtoList=eliminarDuplicados(userDTO.getPhones());
		List<Phone> ls=phoneDtoList.stream()
		.map(phoneDTO->
			new Phone(phoneDTO.getNumber(),
			phoneDTO.getCityCode(),
			phoneDTO.getContryCode(),
			user))
		.collect(Collectors.toList());

		return ls;
	}

	
	private User mapUser(UserDTO userDTO) {
		User usr = new User();
		usr.setEmail(userDTO.getEmail());
		usr.setName(userDTO.getName());
		usr.setPassword(encryptorDecryptor.encrypt(userDTO.getPassword()));
		usr.setCreated(LocalDateTime.now());
		usr.setLastLogin(LocalDateTime.now());
		usr.setToken(encryptorDecryptor.encrypt(generarToken(userDTO.getEmail())));
		usr.setIsactive(true);
		return usr;
	}


	private String generarToken(String userName) {
			return jwtTokenProvider.generateToken(userName, 518400000);
	}

	private UserDetailDTO mapUserDetail(User user) {
		UserDetailDTO userDetailDTO= new UserDetailDTO();
		userDetailDTO.setId(user.getId());
		userDetailDTO.setCreated(user.getCreated()); 
		userDetailDTO.setIsactive(user.isIsactive()); 
		userDetailDTO.setLastLogin(user.getLastLogin()); 
		userDetailDTO.setModified(user.getModified());
		userDetailDTO.setName(user.getName());
		userDetailDTO.setEmail(user.getEmail());
		userDetailDTO.setPassword(encryptorDecryptor.decrypt(user.getPassword()));
		if(null!=user.getPhones()){
			List<PhoneDTO> phones=user.getPhones().stream()
			.map( phone ->new PhoneDTO(phone.getNumber(),
								phone.getCityCode(),
								phone.getContryCode()))
			.collect(Collectors.toList());
			userDetailDTO.setPhones(phones);
		}
		
		userDetailDTO.setToken(encryptorDecryptor.decrypt(user.getToken()));
		return userDetailDTO;
	}

}
