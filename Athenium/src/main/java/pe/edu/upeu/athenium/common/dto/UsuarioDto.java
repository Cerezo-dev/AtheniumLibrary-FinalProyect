package pe.edu.upeu.athenium.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioDto {
    String dni, nombre, apellidoPaterno, apellidoMaterno;
}
