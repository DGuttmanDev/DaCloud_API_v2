package es.pfc.controller.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import es.pfc.exception.MissingTokenHeaderException;
import es.pfc.exception.SaveFileException;
import org.springframework.boot.json.JsonParseException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.SignatureException;

@ControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingHeaderException(MissingRequestHeaderException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No esta autorizado");
    }
    @ExceptionHandler(SaveFileException.class)
    public ResponseEntity<String> handleMissingHeaderException(SaveFileException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud.");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleMissingHeaderException(DataAccessException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en el formato de la solicitud.");
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleTokenException(SignatureException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se ha podido autenticar el usuario.");
    }

    @ExceptionHandler(MissingTokenHeaderException.class)
    public ResponseEntity<String> handleMissingTokenException(MissingTokenHeaderException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se ha podido autenticar el usuario.");
    }

}
