package es.pfc.controller;

import es.pfc.business.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity saveFiles(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new HttpMessageNotReadableException("");
        }
        return fileService.saveFiles(file);
    }

}
