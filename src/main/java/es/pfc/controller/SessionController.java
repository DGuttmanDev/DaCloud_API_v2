package es.pfc.controller;

import es.pfc.business.dto.LoginDTO;
import es.pfc.business.dto.RegisterDTO;
import es.pfc.business.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        System.out.println(usuario.getMail());
        System.out.println(usuario.getPassword());
        return sessionService.login(usuario);
    }

}