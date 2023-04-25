package cl.commons.ms.admin.usuario.be.dto;

import java.util.UUID;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import cl.commons.ms.admin.usuario.be.commons.Constantes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PhoneDetailDTO {
        private Long id;
        private UUID idUser;
        @Digits(integer = 9, fraction = 0, message = Constantes.MENSAJE_ERROR_NUMBER_LENGTH)
        private Long number;
        @Digits(integer = 3, fraction = 0, message = Constantes.MENSAJE_ERROR_CITYCODE_LENGTH)
        private int cityCode;
        @JsonProperty("contrycode")
        @Pattern(regexp = Constantes.NUMBER_REGEX, message = Constantes.MENSAJE_ERROR_CONTRYCODE)
        @Size(min = 1, max = 3, message = Constantes.MENSAJE_ERROR_CONTRYCODE_LENGTH)
        private String contryCode;
}