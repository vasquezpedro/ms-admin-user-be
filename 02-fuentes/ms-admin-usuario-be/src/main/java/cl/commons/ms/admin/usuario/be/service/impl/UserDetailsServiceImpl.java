package cl.commons.ms.admin.usuario.be.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import cl.commons.ms.admin.usuario.be.commons.Constantes;
import cl.commons.ms.admin.usuario.be.commons.EncryptorDecryptor;
import cl.commons.ms.admin.usuario.be.exception.BusinessException;
import cl.commons.ms.admin.usuario.be.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;
	
    @Autowired
    private EncryptorDecryptor encryptorDecryptor;

    @Override
    public UserDetails loadUserByUsername(String token) {
        String tokenEncrypt=encryptorDecryptor.encrypt(token);
        cl.commons.ms.admin.usuario.be.entity.User user=userRepository.findByToken(tokenEncrypt)
        .orElseThrow(()->new BusinessException(HttpStatus.UNAUTHORIZED,Constantes.MENSAJE_ERROR_TOKEN_INVALIDO));
        user.setLastLogin(LocalDateTime.now());
        user=userRepository.save(user);
         return new org.springframework.security.core.userdetails.User(user.getId().toString(), user.getPassword(),new ArrayList<>());
        
    }
    
}
