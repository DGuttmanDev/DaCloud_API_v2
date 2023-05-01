package es.pfc.business.service.impl;

import es.pfc.business.dto.LoginDTO;
import es.pfc.business.dto.RegisterDTO;
import es.pfc.business.mapper.UserMapper;
import es.pfc.business.model.User;
import es.pfc.business.repository.UserRepository;
import es.pfc.business.service.SessionService;
import es.pfc.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {

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
            if (user != null){
                userRepository.save(user);
                return new ResponseEntity<>(jwtTokenProvider.generateToken(user.getMail()), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

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
