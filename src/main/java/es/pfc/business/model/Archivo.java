package es.pfc.business.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter @Setter @ToString
@Entity
public class Archivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne
    private User user;

    public Archivo() {
    }

    public Archivo(String nombre) {
        this.nombre = nombre;
    }

}
