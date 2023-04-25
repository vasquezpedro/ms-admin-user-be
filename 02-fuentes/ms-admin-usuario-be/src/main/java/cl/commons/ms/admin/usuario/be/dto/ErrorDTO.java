package cl.commons.ms.admin.usuario.be.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
	
	private List<DetailErrorDTO> error;
}
