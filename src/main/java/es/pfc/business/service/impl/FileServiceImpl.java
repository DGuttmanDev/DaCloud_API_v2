package es.pfc.business.service.impl;

import es.pfc.business.dto.ArchivoDTO;
import es.pfc.business.mapper.ArchivoMapper;
import es.pfc.business.model.Archivo;
import es.pfc.business.model.User;
import es.pfc.business.repository.ArchivoRepository;
import es.pfc.business.repository.UserRepository;
import es.pfc.business.service.FileService;
import es.pfc.exception.SaveFileException;
import es.pfc.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

    @Value("${storage_path}")
    private String UPLOAD_DIR;

    @Autowired
    private ArchivoRepository archivoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArchivoMapper archivoMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseEntity<Map<String, List<ArchivoDTO>>> saveFiles(List<MultipartFile> files, String token) throws SignatureException{

        if (jwtTokenProvider.isTokenExpired(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {

            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            List<ArchivoDTO> archivosGuardados = new ArrayList<>();
            List<ArchivoDTO> archivosExistentes = new ArrayList<>();

            for (MultipartFile file : files) {

                try {

                    Archivo archivoExistente = archivoRepository.findArchivoByNombre(file.getOriginalFilename());
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(UPLOAD_DIR + File.separator + usuario.getNick() + File.separator + file.getOriginalFilename());

                    if (archivoExistente == null) {

                        if (Files.exists(path)) {
                            System.err.println("Error, archivo existente en el sistema pero no registrado en base de datos.");
                            throw new SaveFileException();
                        } else {
                            Files.write(path, bytes);
                            long sizeBytes = Files.size(path);
                            Archivo archivo = new Archivo();
                            archivo.setNombre(file.getOriginalFilename());
                            archivo.setFileSize(sizeBytes);
                            archivo.setUser(usuario);
                            Archivo archivoGuardado = archivoRepository.save(archivo);
                            archivosGuardados.add(archivoMapper.archivoToArchivoDTO(archivoGuardado));
                        }

                    } else {

                        if (!Files.exists(path)) {
                            System.err.println("Error, archivo registrado en base de datos pero no encontrado en el sistema.");
                            throw new SaveFileException();
                        } else {
                            archivosExistentes.add(archivoMapper.archivoToArchivoDTO(archivoExistente));
                        }

                    }

                } catch (IOException e) {
                    throw new SaveFileException();
                }

            }

            Map<String, List<ArchivoDTO>> archivosMap = new HashMap<>();
            archivosMap.put("archivosGuardados", archivosGuardados);
            archivosMap.put("archivosExistentes", archivosExistentes);

            return ResponseEntity.status(HttpStatus.OK).body(archivosMap);
        }

    }

    @Override
    public ResponseEntity saveSingleFile(MultipartFile file) {

        try {

            Archivo archivoExistente = archivoRepository.findArchivoByNombre(file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR + File.separator + file.getOriginalFilename());

            if (archivoExistente == null) {

                if (Files.exists(path)) {
                    System.err.println("Error, archivo existente en el sistema pero no registrado en base de datos.");
                    throw new SaveFileException();
                } else {
                    Files.write(path, bytes);
                    Archivo archivo = new Archivo();
                    archivo.setNombre(file.getOriginalFilename());
                    Archivo archivoGuardado = archivoRepository.save(archivo);
                    return ResponseEntity.status(HttpStatus.OK).body(archivoGuardado.getId());
                }

            } else {

                if (!Files.exists(path)) {
                    System.err.println("Error, archivo registrado en base de datos pero no encontrado en el sistema.");
                    throw new SaveFileException();
                }

            }

        } catch (IOException e) {
            throw new SaveFileException();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @Override
    public ResponseEntity<List<ArchivoDTO>> replaceFiles(List<MultipartFile> file) {
        return null;
    }

    @Override
    public ResponseEntity<List<ArchivoDTO>> duplicateFiles(List<MultipartFile> file) {
        return null;
    }

}
