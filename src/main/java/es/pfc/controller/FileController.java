package es.pfc.controller;

import es.pfc.business.dto.ArchivoDTO;
import es.pfc.business.dto.DescargaDTO;
import es.pfc.business.dto.NewFolderDTO;
import es.pfc.business.service.FileService;
import es.pfc.exception.MissingTokenHeaderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.SignatureException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired()
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity saveFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("dir_id") Long idDirectorioPadre,
            @RequestHeader("token") String token) throws SignatureException {
        if (files.isEmpty()) {
            throw new HttpMessageNotReadableException("");
        }
        if (token.isEmpty()) {
            throw new MissingTokenHeaderException();
        }
        return fileService.saveFiles(files, idDirectorioPadre, token);
    }

    // TEMPORAL
    @PostMapping("/upload/single")
    public ResponseEntity saveFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("dir_id") Long idDirectorioPadre,
            @RequestHeader("token") String token
    ) throws IOException, SignatureException {
        return fileService.saveFile(file, idDirectorioPadre, token);
    }

    @PostMapping("/new/folder")
    public ResponseEntity createFolder(
            @RequestBody NewFolderDTO newFolderDTO,
            @RequestHeader("token") String token
    ) throws SignatureException {
        System.out.println("entro crear carpeta" + newFolderDTO.getNombreDirectorio() + " " + newFolderDTO.getIdDirectorioPadre());
        return fileService.createFolder(newFolderDTO, token);
    }

    @GetMapping("/home/preview")
    public ResponseEntity<List<ArchivoDTO>> getPreview(
            @RequestHeader("token") String token
    ) throws SignatureException {
        return fileService.getPreview(token);
    }

    @GetMapping("/folder/preview")
    public ResponseEntity<List<ArchivoDTO>> getPreview(
            @RequestHeader("token") String token,
            @RequestParam("dir_id") Long id
    ) throws SignatureException {
        return fileService.getFolderPreview(token, id);
    }

    @GetMapping("/download")
    public ResponseEntity downloadFile(
            @RequestParam("id") Long id,
            @RequestHeader("token") String token
    ) throws SignatureException {
        return fileService.downloadFile(id, token);
    }

    @GetMapping("/download/mobile")
    public ResponseEntity<DescargaDTO> downloadFileMobile(
            @RequestParam("id") Long id,
            @RequestHeader("token") String token
    ) throws SignatureException, IOException {
        return fileService.downloadFileMobile(id, token);
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteFile(
            @RequestParam("id") Long id,
            @RequestHeader("token") String token
    ) throws SignatureException {
        return fileService.deleteFile(id, token);
    }

    @PostMapping("/rename")
    public ResponseEntity renameFile(
            @RequestHeader("token") String token,
            @RequestBody ArchivoDTO archivoDTO
    ) throws SignatureException {
        System.out.println(archivoDTO.getNombreArchivo());
        return fileService.renameFile(token, archivoDTO);
    }

    @GetMapping("/nombre_directorio")
    public ResponseEntity getNombreDirectorio(
            @RequestParam("id") Long id,
            @RequestHeader("token") String token
    ) throws SignatureException {
        return fileService.getDirectorioName(token, id);
    }

}