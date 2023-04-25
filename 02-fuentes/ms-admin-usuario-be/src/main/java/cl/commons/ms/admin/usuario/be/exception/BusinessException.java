package cl.commons.ms.admin.usuario.be.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private HttpStatus status;
    public BusinessException(HttpStatus status, String message){
        super(message);
        this.status=status;
    }
}
