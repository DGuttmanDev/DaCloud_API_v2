package es.pfc.business.service.impl;

import es.pfc.business.model.Archivo;
import es.pfc.business.repository.ArchivoRepository;
import es.pfc.business.service.FileService;
import es.pfc.exception.SaveFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Value("${storage_path}")
    private String UPLOAD_DIR;

    @Autowired
    private ArchivoRepository archivoRepository;

    @Override
    public ResponseEntity saveFiles(List<MultipartFile> files) {

        for (MultipartFile file: files){

            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + File.separator + file.getOriginalFilename());
                Files.write(path, bytes);
            } catch (IOException e) {
                throw new SaveFileException();
            }

            try {
                Archivo archivo = new Archivo();
                archivo.setNombre(file.getOriginalFilename());
                archivoRepository.save(archivo);
            } catch (DataAccessException dax) {
                throw dax;
            }

        }

        return ResponseEntity.ok("Archivo guardado correctamente");

    }

}
