package es.pfc.business.service.impl;

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
            return new ResponseEntity<String>("El correo ya está siendo en uso", HttpStatus.CONFLICT);
        } else if (userRepository.existsByNick(usuario.getNick())){
            return new ResponseEntity<String>("El nick ya está siendo en uso", HttpStatus.CONFLICT);
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

}
