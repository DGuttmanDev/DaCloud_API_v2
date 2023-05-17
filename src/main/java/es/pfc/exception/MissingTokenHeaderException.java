package es.pfc.exception;

public class MissingTokenHeaderException extends RuntimeException{

    public MissingTokenHeaderException(){
        super((""));
    }
}
