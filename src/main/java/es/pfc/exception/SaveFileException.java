package es.pfc.exception;

public class SaveFileException extends RuntimeException{

    public SaveFileException(){
        super("Error al guardar el archivo en el sistema.");
    }

}
