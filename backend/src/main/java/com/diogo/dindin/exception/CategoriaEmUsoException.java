package com.diogo.dindin.exception;

public class CategoriaEmUsoException extends RuntimeException {
    public CategoriaEmUsoException() {
        super("Categoria possui transações vinculadas e não pode ser removida");
    }
}