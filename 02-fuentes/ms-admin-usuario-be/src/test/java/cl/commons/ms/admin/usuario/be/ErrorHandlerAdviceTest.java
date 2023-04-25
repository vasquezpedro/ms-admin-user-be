package cl.commons.ms.admin.usuario.be;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.commons.ms.admin.usuario.be.advice.ErrorHandlerAdvice;
import cl.commons.ms.admin.usuario.be.dto.ErrorDTO;
import cl.commons.ms.admin.usuario.be.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerAdviceTest {

    
    private ErrorHandlerAdvice errorHandlerAdvice;

    @BeforeEach
    void setUp(){
        errorHandlerAdvice = new ErrorHandlerAdvice();
    }

    @Test
    void testBusinessException(){
        String mensajeError = "Business exception test";
        BusinessException businessException = new BusinessException(HttpStatus.BAD_REQUEST, mensajeError);

        ResponseEntity<ErrorDTO> errorHandlerExceptionBusiness = errorHandlerAdvice.errorHandlerException(businessException);

        Assertions.assertNotNull(errorHandlerExceptionBusiness);
        Assertions.assertNotNull(errorHandlerExceptionBusiness.getBody());

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorHandlerExceptionBusiness.getStatusCode());
        Assertions.assertEquals(mensajeError, errorHandlerExceptionBusiness.getBody().getError().get(0).getDetail());
    }



    @Test
    void testException(){
        String mensajeError = "Exception test";

        Exception exception = new Exception(mensajeError);

        ResponseEntity<ErrorDTO> errorHandlerExceptionBusiness = errorHandlerAdvice.errorHandlerException(exception);

        Assertions.assertNotNull(errorHandlerExceptionBusiness);
        Assertions.assertNotNull(errorHandlerExceptionBusiness.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorHandlerExceptionBusiness.getStatusCode());
        Assertions.assertEquals( mensajeError, errorHandlerExceptionBusiness.getBody().getError().get(0).getDetail());

    }
    
}
