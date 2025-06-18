package com.markDev.backend_biblioteca_springboot.exception;

public class RecursoNaoEncontradoException extends RuntimeException{

	public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
	
}
