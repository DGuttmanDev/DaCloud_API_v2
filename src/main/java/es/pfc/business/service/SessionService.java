package es.pfc.business.service;

import es.pfc.business.dto.LoginDTO;
import es.pfc.business.dto.RegisterDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface SessionService {

    ResponseEntity register(RegisterDTO usuario);

    ResponseEntity login(LoginDTO usuario);

}
