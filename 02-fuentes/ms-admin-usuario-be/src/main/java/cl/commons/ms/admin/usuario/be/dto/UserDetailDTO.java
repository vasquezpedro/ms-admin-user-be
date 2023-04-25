package cl.commons.ms.admin.usuario.be.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.commons.ms.admin.usuario.be.commons.Constantes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDTO {

    private UUID id;
	@JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a")
	private LocalDateTime created;
	@JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a")
	private LocalDateTime modified;
	@JsonProperty("last_login")
	@JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a")
	private LocalDateTime lastLogin;
	private String token;
	private boolean isactive;
    private List<PhoneDTO> phones;
	@Email(message = Constantes.MENSAJE_ERROR_EMAIL)
	@Pattern(regexp = Constantes.EMAIL_REGEX, message = Constantes.MENSAJE_ERROR_EMAIL)
	private String email;
	@Pattern(regexp = Constantes.PASS_REGEX, message = Constantes.MENSAJE_ERROR_PASSWORD)
    private String password;
	private String name;
}
