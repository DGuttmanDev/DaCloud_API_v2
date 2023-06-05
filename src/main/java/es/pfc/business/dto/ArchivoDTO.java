package es.pfc.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ArchivoDTO {

    private Long idArchivo;
    private String nombreArchivo;
    private boolean folder;

}
