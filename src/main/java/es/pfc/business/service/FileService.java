package es.pfc.business.service;

import es.pfc.business.dto.ArchivoDTO;
import es.pfc.business.dto.DescargaDTO;
import es.pfc.business.dto.NewFolderDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

public interface FileService {

    ResponseEntity saveFiles(List<MultipartFile> file, Long idDirectorioPadre, String token) throws SignatureException;

    ResponseEntity saveFile(MultipartFile file, Long idDirectorioPadre, String token) throws IOException, SignatureException;

    ResponseEntity createFolder(NewFolderDTO newFolderDTO, String token) throws SignatureException;

    ResponseEntity<List<ArchivoDTO>> getPreview(String token) throws SignatureException;

    ResponseEntity downloadFile(Long id, String token) throws SignatureException;

    ResponseEntity<DescargaDTO> downloadFileMobile(Long id, String token) throws SignatureException, IOException;

    ResponseEntity deleteFile(Long id, String token) throws SignatureException;

    ResponseEntity renameFile(String token, ArchivoDTO archivoDTO) throws SignatureException;

    ResponseEntity<List<ArchivoDTO>> getFolderPreview(String token, Long idFolder) throws SignatureException;

    ResponseEntity getDirectorioName(String token, Long idDirectorio) throws SignatureException;

}
