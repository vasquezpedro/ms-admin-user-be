package cl.commons.ms.admin.usuario.be.advice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import cl.commons.ms.admin.usuario.be.dto.DetailErrorDTO;
import cl.commons.ms.admin.usuario.be.dto.ErrorDTO;
import cl.commons.ms.admin.usuario.be.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ErrorHandlerAdvice {

	public static final String ERROR_PREFIX = "ERROR: ";

	@ExceptionHandler(value = {BusinessException.class})
	public ResponseEntity<ErrorDTO> errorHandlerException(BusinessException ex) {
		log.error(ERROR_PREFIX + ex.getMessage(), ex);
		return new ResponseEntity<>(new ErrorDTO(List.of(new DetailErrorDTO(LocalDateTime.now(), ex.getStatus().value(), ex.getMessage()))), ex.getStatus());
	}

	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<ErrorDTO> errorHandlerException(MethodArgumentNotValidException ex) {
		List<DetailErrorDTO>list= new ArrayList<>();
		String errorMessage ="Error de validacion";
		log.error(ERROR_PREFIX + ex.getMessage(), ex);
		LocalDateTime ldateTime= LocalDateTime.now();
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			if (error instanceof FieldError) {
				FieldError fieldError = (FieldError) error;
						errorMessage = fieldError.getDefaultMessage();
						list.add(new DetailErrorDTO(ldateTime, HttpStatus.BAD_REQUEST.value(), errorMessage));
				}
			}
		return new ResponseEntity<>(new ErrorDTO(list), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<ErrorDTO> errorHandlerException(Exception ex) {
		log.error(ERROR_PREFIX + ex.getMessage(), ex);
		return new ResponseEntity<>(new ErrorDTO(List.of(new DetailErrorDTO(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()))), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
