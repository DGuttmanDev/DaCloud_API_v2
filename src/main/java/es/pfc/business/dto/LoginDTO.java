package es.pfc.business.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class LoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6792928122761986355L;
    String mail;
    String password;

}
