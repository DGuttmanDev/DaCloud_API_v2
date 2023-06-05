package es.pfc.business.service.impl;

import es.pfc.business.dto.DatosDTO;
import es.pfc.business.dto.LoginDTO;
import es.pfc.business.dto.RegisterDTO;
import es.pfc.business.mapper.UserMapper;
import es.pfc.business.model.User;
import es.pfc.business.model.enumeration.Subscription;
import es.pfc.business.repository.UserRepository;
import es.pfc.business.service.SessionService;
import es.pfc.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.SignatureException;

@Service
public class SessionServiceImpl implements SessionService {

    @Value("${storage_path}")
    private String UPLOAD_DIR;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseEntity register(RegisterDTO usuario) {

        if (userRepository.existsByMail(usuario.getMail())){
            return new ResponseEntity<>("El correo ya está siendo en uso", HttpStatus.CONFLICT);
        } else if (userRepository.existsByNick(usuario.getNick())){
            return new ResponseEntity<>("El nick ya está siendo en uso", HttpStatus.CONFLICT);
        }else {
            User user = userMapper.RegisterDTOToUser(usuario);
            user.setSubscription(Subscription.GRATUITA);
            userRepository.save(user);
            try{
                File directorio = new File(UPLOAD_DIR + "/"+user.getNick());
                boolean resultado = directorio.mkdir();
            } catch (Exception exception){
                System.out.println("error al crear el directorio");
            }
            return new ResponseEntity<>(jwtTokenProvider.generateToken(user.getMail()), HttpStatus.CREATED);

        }
    }

    @Override
    public ResponseEntity login(LoginDTO usuario) {

        if (userRepository.existsByMail(usuario.getMail())){
            User checkPassword = userRepository.findByMail(usuario.getMail());
            if (checkPassword.getPassword().equals(usuario.getPassword())){
                return new ResponseEntity<>(jwtTokenProvider.generateToken(usuario.getMail()),HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    public ResponseEntity suscripcion(String token, String suscripcion) throws SignatureException {
        if (jwtTokenProvider.isTokenExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            if (usuario == null){
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            } else {
                usuario.setSubscription(Subscription.valueOf(suscripcion));
                userRepository.save(usuario);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
    }

    @Override
    public ResponseEntity suscripcionActual(String token) throws SignatureException {
        if (jwtTokenProvider.isTokenExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            if (usuario == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(usuario.getSubscription(), HttpStatus.OK);
            }
        }
    }

    @Override
    public ResponseEntity datosActuales(String token) throws SignatureException {
        if (jwtTokenProvider.isTokenExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            if (usuario == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            } else {
                DatosDTO datosDTO = new DatosDTO();
                datosDTO.setNombre(usuario.getNombre());
                datosDTO.setApellidos(usuario.getApellidos());
                return new ResponseEntity<>(datosDTO, HttpStatus.OK);
            }
        }
    }

    @Override
    public ResponseEntity actualizarDatos(String token, DatosDTO datosDTO) throws SignatureException {
        if (jwtTokenProvider.isTokenExpired(token)) {
            System.out.println("no autorizado");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (!userRepository.existsByMail(jwtTokenProvider.extractEmail(token))) {
            System.out.println("no autorizado");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            System.out.println("hola antes");
            User usuario = userRepository.findByMail(jwtTokenProvider.extractEmail(token));
            if (usuario == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            } else {
                System.out.println("Hola");
                usuario.setNombre(datosDTO.getNombre());
                usuario.setApellidos(datosDTO.getApellidos());
                System.out.println("actualizado:"+ usuario.getNombre());
                userRepository.save(usuario);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
    }

}
