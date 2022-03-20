package com.caesar;

import com.caesar.crypto.CaesarCypher;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CaesarCypher()).execute(args);
        System.exit(exitCode);
    }
}
