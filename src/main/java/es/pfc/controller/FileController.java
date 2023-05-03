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

import java.util.List;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired()
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, List<ArchivoDTO>>> saveFiles(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new HttpMessageNotReadableException("");
        }
        return fileService.saveFiles(files);
    }
    @PostMapping("/single/upload")
    public ResponseEntity<Map<String, List<ArchivoDTO>>> saveSingleFile(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            throw new HttpMessageNotReadableException("");
        }
        return fileService.saveSingleFile(file);
    }

    @GetMapping("/test")
    public ResponseEntity replaceFiles() {
        return new ResponseEntity<>("Hola mundo", HttpStatus.OK);
    }

    @PostMapping("/b")
    public ResponseEntity<List<ArchivoDTO>> duplicateFiles(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new HttpMessageNotReadableException("");
        }
        return null;
    }

}