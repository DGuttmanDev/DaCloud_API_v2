package es.pfc.business.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DescargaDTO implements Serializable {

    private Long id;
    private String nombre;
    private String base64Bytes;

}
