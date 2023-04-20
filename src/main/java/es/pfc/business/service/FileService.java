package es.pfc.business.service;

import es.pfc.business.dto.ArchivoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    ResponseEntity<List<ArchivoDTO>> saveFiles(List<MultipartFile> file);

}
