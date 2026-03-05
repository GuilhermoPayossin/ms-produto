package com.guilhermopayossin.github.ms_produto.exeptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}
