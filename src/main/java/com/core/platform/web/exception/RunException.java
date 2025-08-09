package com.core.platform.web.exception;

public class RunException extends  RuntimeException{
    public RunException(String message){
        super(message);
    }

    public RunException(String messmage, Throwable cause){
        super(messmage, cause);
    }
}
