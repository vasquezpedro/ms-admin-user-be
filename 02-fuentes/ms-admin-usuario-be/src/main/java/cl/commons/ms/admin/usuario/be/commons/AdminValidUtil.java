package cl.commons.ms.admin.usuario.be.commons;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import cl.commons.ms.admin.usuario.be.exception.BusinessException;

public class AdminValidUtil {

    public static void validateUserId(Authentication authentication, UUID id) {
		UserDetails  userDetails=(UserDetails) authentication.getPrincipal();
		if(!userDetails.getUsername().equalsIgnoreCase(id.toString())){
			throw new BusinessException(HttpStatus.UNAUTHORIZED,Constantes.MENSAJE_ERROR_UNAUTHORIZED);
		}
	}

	public static String setIfANoEqualsB(String a, String b) {
		String value=b;
		if(null!=a && !a.equalsIgnoreCase(b)){
			value=a;
		}
		return value;
	}

    
}
