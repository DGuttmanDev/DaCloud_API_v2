package es.pfc.controller;

import es.pfc.business.dto.ArchivoDTO;
import es.pfc.business.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired()
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, List<ArchivoDTO>>> saveFiles(@RequestParam("files") List<MultipartFile> files, @RequestHeader("token") String token) throws SignatureException{
        if (files.isEmpty()) {
            throw new HttpMessageNotReadableException("");
        }
        return fileService.saveFiles(files, token);
    }

    @PostMapping("/upload/single")
    public ResponseEntity saveFile(@RequestParam("file") MultipartFile file) throws IOException {
        return fileService.saveFile(file);
    }

    @GetMapping("/preview")
    public ResponseEntity<List<ArchivoDTO>> getPreview( @RequestHeader("token") String token) throws SignatureException {
        return fileService.getPreview(token);
    }

    @GetMapping("/download")
    public ResponseEntity downloadFile(@RequestParam("id") Long id, @RequestHeader("token") String token ) throws SignatureException {
        return fileService.downloadFile(id, token);
    }

}