package es.pfc.business.service.impl;

import es.pfc.business.dto.ArchivoDTO;
import es.pfc.business.dto.NewFolderDTO;
import es.pfc.business.dto.PreviewDTO;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    public ResponseEntity<Map<String, List<ArchivoDTO>>> saveFiles(List<MultipartFile> files, String token) throws SignatureException {

        if (jwtTokenProvider.isTokenExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {

            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            List<ArchivoDTO> archivosGuardados = new ArrayList<>();
            List<ArchivoDTO> archivosExistentes = new ArrayList<>();

            for (MultipartFile file : files) {

                try {

                    Archivo archivoExistente = archivoRepository.findArchivoByNombreAndUser(file.getOriginalFilename(), usuario);
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

                            File fileAbsolutePath = new File(path.toUri());
                            archivo.setPath(fileAbsolutePath.getAbsolutePath());
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
    public ResponseEntity saveFile(MultipartFile file) {

        try {

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR + File.separator + file.getOriginalFilename());

            if (Files.exists(path)) {
                System.err.println("Error, archivo existente en el sistema pero no registrado en base de datos.");
                throw new SaveFileException();
            } else {
                Files.write(path, bytes);
                long sizeBytes = Files.size(path);
                Archivo archivo = new Archivo();
                archivo.setNombre(file.getOriginalFilename());
                archivo.setFileSize(sizeBytes);
                Archivo archivoGuardado = archivoRepository.save(archivo);
            }

        } catch (IOException e) {
            throw new SaveFileException();
        }

        return ResponseEntity.status(HttpStatus.OK).body("");

    }

    @Override
    public ResponseEntity createFolder(NewFolderDTO newFolderDTO, String token) throws SignatureException {
        if (!checkAuth(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            if (newFolderDTO.getIdDirectorioPadre() == 0L) {
                int contador = 0;
                File nuevoDirectorio = new File(UPLOAD_DIR + File.separator + usuario.getNick() + File.separator + newFolderDTO.getNombreDirectorio());
                if (nuevoDirectorio.exists()) {
                    return new ResponseEntity<>("Ya existe un directorio con el nombre especificado.", HttpStatus.CONFLICT);
                } else {
                    boolean dirStatus = nuevoDirectorio.mkdir();
                    if (dirStatus) {
                        Archivo archivo = new Archivo(newFolderDTO.getNombreDirectorio());
                        archivo.setUser(usuario);
                        archivo.setPath(nuevoDirectorio.getAbsolutePath());
                        archivoRepository.save(archivo);

                        ArchivoDTO archivoDTO = archivoMapper.archivoToArchivoDTO(archivo);
                        return new ResponseEntity(archivoDTO, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                }
            } else {
                Archivo directorioPadre = archivoRepository.findById(newFolderDTO.getIdDirectorioPadre()).orElse(null);
                if (directorioPadre != null) {
                    File nuevoDirectorio = new File(directorioPadre.getPath() + File.separator + newFolderDTO.getNombreDirectorio());
                    if (!nuevoDirectorio.exists()) {
                        boolean dirStatus = nuevoDirectorio.mkdir();
                        if (dirStatus) {
                            Archivo archivo = new Archivo(newFolderDTO.getNombreDirectorio());
                            archivo.setUser(usuario);
                            archivo.setPath(nuevoDirectorio.getAbsolutePath());
                            archivoRepository.save(archivo);

                            ArchivoDTO archivoDTO = archivoMapper.archivoToArchivoDTO(archivo);
                            return new ResponseEntity(archivoDTO, HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(HttpStatus.CONFLICT);
                        }
                    } else {
                        return new ResponseEntity<>("Ya existe un directorio con el nombre especificado.", HttpStatus.CONFLICT);
                    }

                }
            }

        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<List<PreviewDTO>> getHomePreview(String token) throws SignatureException {
        if (!checkAuth(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            List<PreviewDTO> listaPreview = new ArrayList<>();
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            File directorioRaiz = new File(UPLOAD_DIR + File.separator + usuario.getNick());
            File[] archivos = directorioRaiz.listFiles();

            for (File file : archivos) {
                Archivo archivo = archivoRepository.findArchivoByNombreAndUser(file.getName(), usuario);
                if (file.isDirectory()) {
                    PreviewDTO archivoDTO = new PreviewDTO(archivo.getId(), file.getName(), true);
                    listaPreview.add(archivoDTO);
                } else if (file.isFile()) {
                    PreviewDTO archivoDTO = new PreviewDTO(archivo.getId(), file.getName(), false);
                    listaPreview.add(archivoDTO);
                }
            }
            return new ResponseEntity<>(listaPreview, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity downloadFile(Long id, String token) throws SignatureException {

        if (!checkAuth(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (archivoRepository.existsById(id)) {
                Archivo archivo = archivoRepository.findById(id).orElse(null);
                User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
                Path path = Paths.get(UPLOAD_DIR + File.separator + usuario.getNick() + File.separator + archivo.getNombre());
                if (!Files.exists(path)) {
                    return new ResponseEntity(HttpStatus.NOT_FOUND);
                } else {
                    File archivo2 = new File(path.toUri());
                    try {
                        InputStream inputStream = new FileInputStream(archivo2);
                        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + archivo2.getName())
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .contentLength(archivo2.length())
                                .body(inputStreamResource);
                    } catch (FileNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }
            }
        }

        return null;
    }

    private boolean checkAuth(String token) throws SignatureException {
        return !jwtTokenProvider.isTokenExpired(token) && userRepository.existsByMail(jwtTokenProvider.extractEmail(token));
    }

}
