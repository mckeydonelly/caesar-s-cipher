package com.javarush.caesar;

import com.javarush.caesar.crypto.CaesarCypher;
import com.javarush.caesar.exceptions.PrintExceptionMessageHandler;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        new CommandLine(new CaesarCypher())
            .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
            .execute(args);
    }
}
