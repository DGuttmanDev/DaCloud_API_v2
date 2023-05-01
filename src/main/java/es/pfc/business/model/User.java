package es.pfc.business.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 8191313067958605573L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellidos;
    private String nick;
    private String password;
    private String mail;

    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

}
