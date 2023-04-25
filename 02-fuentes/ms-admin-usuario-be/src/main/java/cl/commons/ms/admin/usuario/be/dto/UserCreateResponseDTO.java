package cl.commons.ms.admin.usuario.be.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreateResponseDTO {

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

}
