package es.pfc.business.service;

import es.pfc.business.dto.DatosDTO;
import es.pfc.business.dto.LoginDTO;
import es.pfc.business.dto.RegisterDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SignatureException;

@Service
public interface SessionService {

    ResponseEntity register(RegisterDTO usuario);

    ResponseEntity registerWeb(RegisterDTO usuario);

    ResponseEntity login(LoginDTO usuario);

    ResponseEntity loginWeb(LoginDTO usuario);

    ResponseEntity suscripcion(String token, String suscripcion) throws SignatureException;

    ResponseEntity suscripcionActual(String token) throws SignatureException;

    ResponseEntity datosActuales(String token) throws SignatureException;

    ResponseEntity actualizarDatos(String token, DatosDTO datosDTO) throws SignatureException;

}
