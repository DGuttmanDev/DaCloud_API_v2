package es.pfc.business.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    ResponseEntity saveFiles(MultipartFile file);

}
