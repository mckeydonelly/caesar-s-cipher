package com.caesar.crypto;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

@Command(name = "cypher", subcommands = CommandLine.HelpCommand.class, description = "Caesar cypher command")
public class CaesarCypher implements Runnable {
    private static final String FILENAME_PATTERN = "^[\\w]+\\.[\\w]{2,4}$";
    private static final List<Character> ALPHABET = Arrays.asList('а', 'б', 'в', 'г', 'д', 'е',
            'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х',
            'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё',
            'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц',
            'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', '.', ',', '«', '»', '"', '\'', ':', ';',
            '-', '!', '?', ' ');

    @Spec
    CommandSpec spec;

    /**
     * Шифрует файл.
     *
     * @param src     файл с текстом для шифрования
     * @param dest    файл для сохранения зашифрованного текста
     * @param key     ключ для шифрования
     */
    @Command(name = "encrypt", description = "Encrypt from file to file using key")
    void encrypt(
            @Parameters(paramLabel = "<source file>", description = "source file with text to encrypt") File src,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have encrypted text") File dest,
            @Parameters(paramLabel = "<key>", description = "key for encryption") int key) {

        System.out.println("Start encryption...");

        validateSrc(src);
        validateDest(dest);

        try (Reader srcFile = new FileReader(src, Charset.defaultCharset()); Writer destFile = new FileWriter(dest, Charset.defaultCharset())) {
            int readLength;
            char[] readBuffer = new char[1000];

            key = validateKey(key);

            while (srcFile.ready()) {
                readLength = srcFile.read(readBuffer);
                for (int i = 0; i < readLength; i++) {
                    if (ALPHABET.contains(readBuffer[i])) {
                        readBuffer[i] = ALPHABET.get(calculateShift(ALPHABET.indexOf(readBuffer[i]) + key, CryptoType.ENCRYPT));
                    }
                }
                destFile.write(readBuffer, 0, readLength);
                destFile.flush();
            }
        } catch (FileNotFoundException e) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    String.format("Invalid values '%s' or '%s' for options '<source file>' or '<dest file>': " +
                            "FileNotFound", src.getName(), dest.getName()));
        } catch (IOException e) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    String.format("Invalid values '%s' or '%s' for options '<source file>' or '<dest file>': " +
                            "IOException. Please check permissions to create or write file.", src.getName(), dest.getName()));
        }

        System.out.println("Encryption is successfully ended.");
    }

    /**
     * Расшифровывает файл.
     *
     * @param src     файл с зашифрованным текстом
     * @param dest    файл для сохранения расшифрованного текста
     * @param key     ключ для расшифровки
     */
    @Command(name = "decrypt", description = "Decrypt from file to file using statistical analysis")
    void decrypt(
            @Parameters(paramLabel = "<source file>", description = "source file with encrypted text") File src,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have decrypted text") File dest,
            @Parameters(paramLabel = "<key>", description = "key for encryption") int key) {

        System.out.println("Start decryption...");

        validateSrc(src);
        validateDest(dest);

        try (Reader srcFile = new FileReader(src, Charset.defaultCharset()); Writer destFile = new FileWriter(dest, Charset.defaultCharset())) {
            int readLength;
            char[] readBuffer = new char[1000];

            key = validateKey(key);

            while (srcFile.ready()) {
                readLength = srcFile.read(readBuffer);
                for (int i = 0; i < readLength; i++) {
                    if (ALPHABET.contains(readBuffer[i])) {
                        readBuffer[i] = ALPHABET.get(calculateShift(ALPHABET.indexOf(readBuffer[i]) - key, CryptoType.DECRYPT));
                    }
                }
                destFile.write(readBuffer, 0, readLength);
                destFile.flush();
            }
        } catch (FileNotFoundException e) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    String.format("Invalid values '%s' or '%s' for options '<source file>' or '<dest file>': " +
                            "FileNotFound", src.getName(), dest.getName()));
        } catch (IOException e) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    String.format("Invalid values '%s' or '%s' for options '<source file>' or '<dest file>': " +
                            "IOException. Please check permissions to create or write file.", src.getName(), dest.getName()));
        }

        System.out.println("Decryption is successfully ended.");
    }

    /**
     * Выполняет взлом (брутфорс) текста по всем возможным ключам и сравниваем его со словарем, сформированным из representative file.
     *
     * @param src                    файл с зашифрованным текстом
     * @param representativeFile     файл с незашифрованным репрезентативным текстом
     * @param dest                   файл для сохранения расшифрованного текста
     */
    @Command(name = "brute-force", description = "Decrypt from file to file using brute force")
    void bruteForce(
            @Parameters(paramLabel = "<source file>", description = "source file with encrypted text") File src,
            @Parameters(paramLabel = "<representative file>", description = "file with unencrypted representative text") File representativeFile,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have decrypted text") File dest) {

        System.out.println("Start brute-force...");

        Set<String> dictionary = new HashSet<>();
        Map<Integer, Integer> matchesByKey = new TreeMap<>();
        int bestKey;

        validateSrc(src);
        validateSrc(representativeFile);
        validateDest(dest);
        fillDictionary(representativeFile, dictionary);

        System.out.println("Start brute-force by all keys...");
        for (int key = 0; key <= ALPHABET.size(); key++) {
            try (Reader srcFile = new FileReader(src, Charset.defaultCharset())) {
                int readLength;
                char[] readBuffer = new char[1000];
                StringBuilder checkBuffer = new StringBuilder();

                while (srcFile.ready()) {
                    readLength = srcFile.read(readBuffer);
                    for (int i = 0; i < readLength; i++) {
                        if (ALPHABET.contains(readBuffer[i])) {
                            readBuffer[i] = ALPHABET.get(calculateShift(ALPHABET.indexOf(readBuffer[i]) - key, CryptoType.DECRYPT));
                        }
                    }
                    checkBuffer.append(readBuffer);
                }

                matchesByKey.put(key, matchesByDict(checkBuffer, dictionary));

            } catch (FileNotFoundException e) {
                throw new CommandLine.ParameterException(spec.commandLine(),
                        String.format("Invalid value '%s' for options '<source file>': " +
                                "FileNotFound", src.getName()));
            } catch (IOException e) {
                throw new CommandLine.ParameterException(spec.commandLine(),
                        String.format("Invalid values '%s' for options '<source file>': " +
                                "IOException. Please check permissions to create or write file.", src.getName()));
            }
        }

        System.out.println("Print matches by key:");
        for (Map.Entry<Integer, Integer> entry : matchesByKey.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ". Matches: " + entry.getValue());
        }

        bestKey = Collections.max(matchesByKey.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
        System.out.println("Best key: " + bestKey + ". Using this key for the final decryption...");
        decrypt(src, dest, bestKey);

        System.out.println("Brute-force is successfully ended.");
    }

    /**
     * Выполняет статистический анализ текста.
     *
     * @param src                    файл с зашифрованным текстом
     * @param representativeFile     файл с незашифрованным репрезентативным текстом
     * @param dest                   файл для сохранения расшифрованного текста
     */
    @Command(name = "static-decrypt", description = "Decrypt from file to file using statistical analysis")
    void statisticalDecrypt(
            @Parameters(paramLabel = "<source file>", description = "source file with encrypted text") File src,
            @Option(names = {"-r", "--representative"}, description = "file with unencrypted representative text") File representativeFile,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have decrypted text") File dest) {
        // TODO
    }

    @Override
    public void run() {
        throw new ParameterException(spec.commandLine(), "Specify a subcommand");
    }

    /**
     * Валидация файла с исходным текстом
     *
     * @param file файл с текстом
     */
    private void validateSrc(File file) {
        System.out.println("Validating source or representative file...");

        if (!file.isFile() || !file.exists() || !file.getName().matches(FILENAME_PATTERN)) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for option '<source file>': " +
                            "value is not a valid file name.", file.getName()));
        }
    }

    /**
     * Валидация файла для сохранения текста
     *
     * @param file файл с текстом
     */
    private void validateDest(File file) {
        System.out.println("Validating desination file...");

        if (!file.isFile() || !file.getName().matches(FILENAME_PATTERN)) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for option '<source file>': " +
                            "value is not a valid file name.", file.getName()));
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new CommandLine.ParameterException(spec.commandLine(),
                        String.format("Invalid value '%s' for option '<dest file>': " +
                                "IOException. Please check permissions to create\\write file.", file.getName()));
            }
        }

        if (!file.canWrite()) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for option '<dest file>': " +
                            "IOException. Please check permissions to create\\write file.", file.getName()));
        }
    }

    /**
     * Вычисление ключа, если он превышает размер алфавита
     *
     * @param key ключ для проверки
     */
    private int validateKey(int key) {
        if (key > ALPHABET.size()) {
            return key %= ALPHABET.size();
        }
        return key;
    }

    /**
     * Вычисление смещения, если оно меньше\больше размера алфавита
     *
     * @param index индекс смещения в алфавите
     * @param type  определяет тип выполняемого действия {@link CryptoType}
     */
    private int calculateShift(int index, CryptoType type) {
        int shift = index;

        if (type == CryptoType.ENCRYPT) {
            if (index > ALPHABET.size()) {
                shift = index - ALPHABET.size();
            }
        }

        if (type == CryptoType.DECRYPT) {
            if (index < 0) {
                shift = index + ALPHABET.size();
            }
        }
        return shift;
    }

    /**
     * Заполняет словарь
     *
     * @param dictionary файл с репрезентативным текстом для заполнения словаря
     * @param dictMap    структура в которой сохраняется по словам словарь из репрезентативного текста
     */
    private void fillDictionary(File dictionary, Set<String> dictMap) {
        System.out.println("Fill dictionary from representative file...");

        try (Scanner dictFile = new Scanner(dictionary, Charset.defaultCharset())) {
            while (dictFile.hasNext()) {
                dictMap.add(dictFile.next());
            }
        } catch (FileNotFoundException e) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for options '-r (--representative)': " +
                            "FileNotFound", dictionary.getName()));
        } catch (IOException e) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for options '-r (--representative)': " +
                            "IOException", dictionary.getName()));
        }
    }

    /**
     * Получает количество совпадений расшифрованного текста со словарем
     *
     * @param checkBuffer буфер с расшифрованным текстом
     * @param dictionary  словарь
     */
    private int matchesByDict(StringBuilder checkBuffer, Set<String> dictionary) {
        int match = 0;

        try (Scanner checkBufferSc = new Scanner(String.valueOf(checkBuffer))) {
            while (checkBufferSc.hasNext()) {
                if (dictionary.contains(checkBufferSc.next())) {
                    match++;
                }
            }
        }
        return match;
    }
}
