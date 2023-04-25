package cl.commons.ms.admin.usuario.be.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailErrorDTO {
    @JsonFormat(pattern="MMM dd, yyyy hh:mm:ss a")
    private LocalDateTime timestamp;
    private int codigo;
    private String detail;
}