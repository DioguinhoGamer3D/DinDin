package com.diogo.dindin.exception;

public class TipoIncompativelException extends RuntimeException {
    public TipoIncompativelException() {
        super("O tipo da transação deve ser igual ao tipo da categoria");
    }
}