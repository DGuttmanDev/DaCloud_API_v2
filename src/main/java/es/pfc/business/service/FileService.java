package es.pfc.business.service;

import es.pfc.business.dto.ArchivoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;

public interface FileService {

    ResponseEntity<Map<String, List<ArchivoDTO>>> saveFiles(List<MultipartFile> file, String token) throws SignatureException;
    ResponseEntity<List<ArchivoDTO>> replaceFiles(List<MultipartFile> file);
    ResponseEntity<List<ArchivoDTO>> duplicateFiles(List<MultipartFile> file);

}
