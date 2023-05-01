package es.pfc.business.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class RegisterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6283328612164788999L;
    private String nombre;
    private String apellidos;
    private String password;
    private String nick;
    private String mail;

}
