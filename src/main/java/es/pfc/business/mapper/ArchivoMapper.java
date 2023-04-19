package es.pfc.business.mapper;

import es.pfc.business.dto.ArchivoDTO;
import es.pfc.business.model.Archivo;
import org.springframework.stereotype.Component;

@Component
public class ArchivoMapper {

    public ArchivoDTO archivoToArchivoDTO(Archivo archivo){
        return new ArchivoDTO(archivo.getId(), archivo.getNombre());
    }

}
