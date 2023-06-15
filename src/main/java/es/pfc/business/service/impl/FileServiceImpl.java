package es.pfc.business.service.impl;

import es.pfc.business.dto.ArchivoDTO;
import es.pfc.business.dto.DescargaDTO;
import es.pfc.business.dto.NewFolderDTO;
import es.pfc.business.mapper.ArchivoMapper;
import es.pfc.business.model.Archivo;
import es.pfc.business.model.User;
import es.pfc.business.repository.ArchivoRepository;
import es.pfc.business.repository.UserRepository;
import es.pfc.business.service.FileService;
import es.pfc.business.service.FileUtil;
import es.pfc.exception.SaveFileException;
import es.pfc.security.JwtTokenProvider;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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
import java.util.*;

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
    public ResponseEntity saveFiles(List<MultipartFile> files, Long idDirectorioPadre, String token) throws SignatureException {


        if (!checkAuth(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            for (MultipartFile file : files) {

                try {

                    byte[] bytes = file.getBytes();
                    Path path;
                    if (idDirectorioPadre == 0L){
                        path = Paths.get(UPLOAD_DIR + File.separator + usuario.getNick() + File.separator + file.getOriginalFilename());
                    } else {
                        if (archivoRepository.existsById(idDirectorioPadre)){
                            Archivo directorioPadre = archivoRepository.findById(idDirectorioPadre).orElse(null);
                            assert directorioPadre != null;
                            path = Path.of(directorioPadre.getPath() + File.separator + file.getOriginalFilename());
                        } else {
                            return new ResponseEntity<>(HttpStatus.CONFLICT);
                        }
                    }

                    if (!Files.exists(path)){
                        Files.write(path, bytes);
                        long sizeBytes = Files.size(path);
                        Archivo archivo = new Archivo();
                        archivo.setNombre(file.getOriginalFilename());
                        archivo.setFileSize(sizeBytes);
                        archivo.setUser(usuario);
                        archivo.setFolder(false);
                        archivo.setDirectorioPadre(idDirectorioPadre);

                        File fileAbsolutePath = new File(path.toUri());
                        archivo.setPath(fileAbsolutePath.getAbsolutePath());
                        archivoRepository.save(archivo);
                    }

                } catch (IOException e) {
                    throw new SaveFileException();
                }

            }

            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

    @Override
    public ResponseEntity saveFile(MultipartFile file, Long idDirectorioPadre, String token) throws SignatureException {

        if (!checkAuth(token)) {
            System.out.println("no autorizado");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));

            try {

                byte[] bytes = file.getBytes();
                Path path;
                if (idDirectorioPadre == 0L){
                    path = Paths.get(UPLOAD_DIR + File.separator + usuario.getNick() + File.separator + file.getOriginalFilename());
                } else {
                    if (archivoRepository.existsById(idDirectorioPadre)){
                        Archivo directorioPadre = archivoRepository.findById(idDirectorioPadre).orElse(null);
                        assert directorioPadre != null;
                        path = Path.of(directorioPadre.getPath() + File.separator + file.getOriginalFilename());
                    } else {
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                }

                if (!Files.exists(path)){
                    System.out.println("entro a almacenar");
                    Files.write(path, bytes);
                    long sizeBytes = Files.size(path);
                    Archivo archivo = new Archivo();
                    archivo.setNombre(file.getOriginalFilename());
                    archivo.setFileSize(sizeBytes);
                    archivo.setUser(usuario);
                    archivo.setFolder(false);
                    archivo.setDirectorioPadre(idDirectorioPadre);

                    File fileAbsolutePath = new File(path.toUri());
                    archivo.setPath(fileAbsolutePath.getAbsolutePath());
                    archivoRepository.save(archivo);
                }

            } catch (IOException e) {
                throw new SaveFileException();
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Override
    public ResponseEntity createFolder(NewFolderDTO newFolderDTO, String token) throws SignatureException {
        if (!checkAuth(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            if (newFolderDTO.getIdDirectorioPadre() == 0L){
                File nuevoDirectorio = new File(UPLOAD_DIR + File.separator + usuario.getNick() + File.separator + newFolderDTO.getNombreDirectorio());
                if (nuevoDirectorio.exists()){
                    return new ResponseEntity<>("Ya existe un directorio con el nombre especificado.", HttpStatus.CONFLICT);
                } else {
                    return getResponseEntity(newFolderDTO, usuario, nuevoDirectorio);
                }
            } else {
                Archivo directorioPadre = archivoRepository.findById(newFolderDTO.getIdDirectorioPadre()).orElse(null);
                if (directorioPadre != null){
                    File nuevoDirectorio = new File(directorioPadre.getPath() + File.separator + newFolderDTO.getNombreDirectorio());
                    return getResponseEntity(newFolderDTO, usuario, nuevoDirectorio);
                }
            }

        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    private ResponseEntity<ArchivoDTO> getResponseEntity(NewFolderDTO newFolderDTO, User usuario, File nuevoDirectorio) {
        System.out.println(nuevoDirectorio.getName());
        System.out.println(nuevoDirectorio.getAbsolutePath());
        System.out.println(nuevoDirectorio.exists());
        boolean dirStatus = nuevoDirectorio.mkdir();
        System.out.println(dirStatus);
        if (dirStatus){
            Archivo archivo = new Archivo(newFolderDTO.getNombreDirectorio());
            archivo.setFolder(true);
            archivo.setUser(usuario);
            archivo.setPath(nuevoDirectorio.getAbsolutePath());
            archivo.setDirectorioPadre(newFolderDTO.getIdDirectorioPadre());
            archivoRepository.save(archivo);

            ArchivoDTO archivoDTO = archivoMapper.archivoToArchivoDTO(archivo);
            return new ResponseEntity<>(archivoDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Override
    public ResponseEntity<List<ArchivoDTO>> getPreview(String token) throws SignatureException {
        if (jwtTokenProvider.isTokenExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            List<ArchivoDTO> listaPreview = new ArrayList<>();
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            File directorio = new File(UPLOAD_DIR + File.separator + usuario.getNick());
            File[] archivos = directorio.listFiles();

            assert archivos != null;
            for (File file: archivos){
                Archivo archivo = archivoRepository.findArchivoByNombreAndUser(file.getName(), usuario);

                System.out.println(archivo.getNombre());

                ArchivoDTO archivoDTO = new ArchivoDTO(archivo.getId(), archivo.getNombre(), archivo.isFolder());
                listaPreview.add(archivoDTO);
            }
            return new ResponseEntity<>(listaPreview, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Resource> downloadFile(Long id, String token) throws SignatureException {

        if (jwtTokenProvider.isTokenExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (archivoRepository.existsById(id)) {
                Archivo archivo = archivoRepository.findById(id).orElse(null);
                User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
                assert archivo != null;
                Path path = Paths.get(UPLOAD_DIR + File.separator + usuario.getNick() + File.separator + archivo.getNombre());
                if (!Files.exists(path)) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    @Override
    public ResponseEntity<DescargaDTO> downloadFileMobile(Long id, String token) throws SignatureException, IOException {
        if (jwtTokenProvider.isTokenExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (archivoRepository.existsById(id)) {
                Archivo archivo = archivoRepository.findById(id).orElse(null);
                User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
                assert archivo != null;

                DescargaDTO descargaDTO = new DescargaDTO();
                descargaDTO.setId(archivo.getId());
                descargaDTO.setNombre(archivo.getNombre());

                File file = new File(archivo.getPath());
                byte[] bytesArray = null;

                try(FileInputStream fis = new FileInputStream(file)) {
                    bytesArray = new byte[(int) file.length()];
                    fis.read(bytesArray);
                }

                String base64String = Base64.getEncoder().encodeToString(bytesArray);


                descargaDTO.setBase64Bytes(base64String);
                return new ResponseEntity<>(descargaDTO, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

    private MultipartFile generateMultipartFile(Archivo archivo) throws FileNotFoundException {

        File file = new File(archivo.getPath());

        try {
            FileUtil fileUtil = new FileUtil();
            MultipartFile multipartFile = fileUtil.generateMultipartFile(archivo);
            return multipartFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public ResponseEntity deleteFile(Long id, String token) throws SignatureException {
        if (!checkAuth(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            if (usuario == null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Archivo archivo = archivoRepository.findById(id).orElse(null);
            if (archivo == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            try{
                File file = new File(archivo.getPath());
                if (file.isDirectory()){
                    boolean verificadoBorrado = deleteDirectory(file);
                    if (verificadoBorrado){
                        archivoRepository.delete(archivo);
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                } else{
                    boolean deleted = file.delete();
                    archivoRepository.delete(archivo);
                    return new ResponseEntity<>(HttpStatus.OK);
                }

            } catch (Exception exception){
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
    }

    private boolean deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    Archivo archivo = archivoRepository.findByPath(file.getAbsolutePath());
                    if (archivo != null){
                        System.out.println("instancia: "+archivo.getPath());
                        archivoRepository.delete(archivo);
                    }
                    System.out.println("borrar subcarpeta: "+file.getName());
                    deleteDirectory(file);
                } else {
                    Archivo archivo = archivoRepository.findByPath(file.getAbsolutePath());

                    if (archivo != null){
                        System.out.println("instancia: "+archivo.getPath());
                        archivoRepository.delete(archivo);
                    }
                    System.out.println("borrar archivo: "+file.getName());
                    file.delete();
                }
            }
        }
        // Eliminar directorio vacío
        if (directory.delete()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ResponseEntity renameFile(String token, ArchivoDTO archivoDTO) throws SignatureException {
        if (!checkAuth(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            if (usuario == null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Archivo archivo = archivoRepository.findById(archivoDTO.getIdArchivo()).orElse(null);
            if (archivo == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            try {
                File file = new File(archivo.getPath());
                File renombrado = new File(file.getParent(), archivoDTO.getNombreArchivo());
                boolean comprobacion = file.renameTo(renombrado);
                archivo.setNombre(archivoDTO.getNombreArchivo());
                archivo.setPath(renombrado.getAbsolutePath());
                archivoRepository.save(archivo);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception exception){
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
    }

    @Override
    public ResponseEntity<List<ArchivoDTO>> getFolderPreview(String token, Long idFolder) throws SignatureException {
        if (jwtTokenProvider.isTokenExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            List<ArchivoDTO> listaPreview = new ArrayList<>();
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));

            List<Archivo> listaArchivos = archivoRepository.findArchivoByDirectorioPadre(idFolder);

            for (Archivo archivo:listaArchivos){
                ArchivoDTO archivoDTO = new ArchivoDTO(archivo.getId(), archivo.getNombre(), archivo.isFolder());
                listaPreview.add(archivoDTO);
            }

            return new ResponseEntity<>(listaPreview, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity getDirectorioName(String token, Long idDirectorio) throws SignatureException {
        if (jwtTokenProvider.isTokenExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            Archivo archivo = archivoRepository.findById(idDirectorio).orElse(null);
            if (archivo == null){
                return new ResponseEntity<>("", HttpStatus.OK);
            }
            return new ResponseEntity<>(archivo.getNombre(), HttpStatus.OK);
        }
    }

    private boolean checkAuth(String token) throws SignatureException {
        return !jwtTokenProvider.isTokenExpired(token) && userRepository.existsByMail(jwtTokenProvider.extractEmail(token));
    }

}
