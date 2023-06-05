package es.pfc.controller;

import es.pfc.business.dto.DatosDTO;
import es.pfc.business.dto.LoginDTO;
import es.pfc.business.dto.RegisterDTO;
import es.pfc.business.model.User;
import es.pfc.business.repository.UserRepository;
import es.pfc.business.service.SessionService;
import es.pfc.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;

@Controller
@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO usuario) {
        return sessionService.register(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO usuario) {
        return sessionService.login(usuario);
    }

    @PutMapping("/suscripcion")
    public ResponseEntity suscripcion(@RequestHeader("token") String token, @RequestBody String suscripcion) throws SignatureException {
        return sessionService.suscripcion(token, suscripcion);
    }

    @GetMapping("/suscripcion/actual")
    public ResponseEntity suscripcion(@RequestHeader("token") String token) throws SignatureException {
        return sessionService.suscripcionActual(token);
    }

    @GetMapping("/datos/actual")
    public ResponseEntity datosActuales(@RequestHeader("token") String token) throws SignatureException {
        return sessionService.datosActuales(token);
    }

    @PostMapping("/datos/actualizar")
    public ResponseEntity actualizarDatos(@RequestHeader("token") String token, @RequestBody DatosDTO datosDTO) throws SignatureException {
        System.out.println(datosDTO.getNombre());
        return sessionService.actualizarDatos(token, datosDTO);
    }

}