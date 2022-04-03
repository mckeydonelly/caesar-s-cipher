Сaesar-s-cipher
=========
Краткое описание
-----------------
Утилита реализующая шифр Цезаря на java. Используемые фреймворки и библиотеки:

- Picocli

Сборка проекта:

```
$ cd caesar-s-cipher
$ mvn package
```

Запуск проекта:
```
$ java -jar ./target/caesar-s-cipher-1.0.jar
```

Ограничения
-----------
- Программа использует алфавит а-я, А-Я и знаки пунктуации ('.', ',', '«', '»', '"', '\'', ':', ';',
  '-', '!', '?', ' ').
- Ключ может быть только положительным целым числом
- Максимальный ключ ограничен ```Integer.MAX_VALUE```

Описание алгоритма
-----------
Шифр Цезаря — это шифр подстановки: в нем каждый символ в открытом тексте заменяют на символ, который находится на некотором постоянном числе позиций левее или правее него в алфавите.

Допустим, мы устанавливаем сдвиг на 3. В таком случае А заменится на Г, Б станет Д, и так далее.

Краткое описание классов
------------------------
В корневом пакете проекта `com.caesar` находится класс Main, содержащий в себе точку входа в приложение.

В пакете `crypto` содержатся классы:

`CaesarCypher` - контроллер, который принимает все команды утилиты и внутренние вспомогательные:

- `encrypt <source file> <dest file> <key>` - шифрование файла
- `decrypt <source file> <dest file> <key>` - расшифровка файла
- `brute-force <source file> <representative file> <dest file>` - брут-форс с использованием файла с репрезентативным текстом

`CryptoType` - перечисление, содержащее тип действия для вычисления смещения

Описание внешнего программного интерфейса
--------------
### Шифрование
Для шифрования текста необходимо выполнить команду:

`java -jar caesar-s-cipher-1.0.jar encrypt <source file> <dest file> <key>` 

- `<source file>` файл с текстом для шифрования
- `<dest file>` путь к файлу для сохранения зашифрованного текста
- `<key>` - ключ для шифрования

Пример запроса:

`java -jar caesar-s-cipher-1.0.jar encrypt ./testData/encrypt/testSrc.txt ./testData/encrypt/testDst.txt 158`

Пример ответа:
```
Start encryption...
Validating source or representative file...
Validating desination file...
Encryption is successfully ended.
```

При возникновении проблем с доступом к файлам программа сообщит об ошибке.

### Расшифровка
Для расшифровки текста необходимо выполнить команду:

`java -jar caesar-s-cipher-1.0.jar decrypt <source file> <dest file> <key>`

- `<source file>` файл с зашифрованным текстом
- `<dest file>` путь к файлу для сохранения расшифрованного текста
- `<key>` - ключ для расшифровки

Пример запроса:

`java -jar caesar-s-cipher-1.0.jar decrypt ./testData/encrypt/testSrc.txt ./testData/encrypt/testDst.txt 158`

Пример ответа:
```
Start decryption...
Validating source or representative file...
Validating desination file...
Decryption is successfully ended.
```

При возникновении проблем с доступом к файлам программа сообщит об ошибке.

### Brute-force

При использовании данной опции программа перебирает все возможные ключи и сравниваем кол-во вхождений на каждом круге со словарем, сформированным из репрезентативного текста.

Для выполнения взлома ключа зашифрованного текста необходимо выполнить команду:
`java -jar caesar-s-cipher-1.0.jar brute-force <source file> <representative file> <dest file>`

- `<source file>` файл с зашифрованным текстом
- `<representative file>` файл с незашифрованным репрезентативным текстом
- `<dest file>` путь к файлу для сохранения расшифрованного текста

Пример запроса:

`java -jar caesar-s-cipher-1.0.jar brute-force ./testData/brute-force/testSrc.txt ./testData/brute-force/testRepresentative.txt ./testData/brute-force/testDst.txt`

Пример ответа:
```
Start brute-force...
Validating source or representative file...
Validating source or representative file...
Validating desination file...
Fill dictionary from representative file...
Start brute-force by all keys...
Print matches by key:
Key: 0. Matches: 0
Key: 1. Matches: 0
Key: 2. Matches: 123
Key: 3. Matches: 6
Key: 4. Matches: 0
Key: 5. Matches: 0
Key: 6. Matches: 0
Key: 7. Matches: 0
Key: 8. Matches: 1
Key: 9. Matches: 0
Key: 10. Matches: 0
Key: 11. Matches: 1
Key: 12. Matches: 1
Key: 13. Matches: 0
Key: 14. Matches: 2
Key: 15. Matches: 1
Key: 16. Matches: 0
Key: 17. Matches: 0
Key: 18. Matches: 3
Key: 19. Matches: 0
Key: 20. Matches: 0
Key: 21. Matches: 1
Key: 22. Matches: 2
Key: 23. Matches: 2
Key: 24. Matches: 0
Key: 25. Matches: 0
Key: 26. Matches: 0
Key: 27. Matches: 0
Key: 28. Matches: 0
Key: 29. Matches: 0
Key: 30. Matches: 0
Key: 31. Matches: 0
Key: 32. Matches: 0
Key: 33. Matches: 0
Key: 34. Matches: 3
Key: 35. Matches: 0
Key: 36. Matches: 0
Key: 37. Matches: 0
Key: 38. Matches: 0
Key: 39. Matches: 0
Key: 40. Matches: 0
Key: 41. Matches: 0
Key: 42. Matches: 0
Key: 43. Matches: 0
Key: 44. Matches: 0
Key: 45. Matches: 0
Key: 46. Matches: 0
Key: 47. Matches: 0
Key: 48. Matches: 0
Key: 49. Matches: 0
Key: 50. Matches: 0
Key: 51. Matches: 0
Key: 52. Matches: 0
Key: 53. Matches: 0
Key: 54. Matches: 0
Key: 55. Matches: 0
Key: 56. Matches: 0
Key: 57. Matches: 0
Key: 58. Matches: 0
Key: 59. Matches: 0
Key: 60. Matches: 0
Key: 61. Matches: 0
Key: 62. Matches: 0
Key: 63. Matches: 0
Key: 64. Matches: 0
Key: 65. Matches: 0
Key: 66. Matches: 0
Key: 67. Matches: 0
Key: 68. Matches: 0
Key: 69. Matches: 0
Key: 70. Matches: 0
Key: 71. Matches: 0
Key: 72. Matches: 0
Key: 73. Matches: 0
Key: 74. Matches: 0
Key: 75. Matches: 0
Key: 76. Matches: 0
Key: 77. Matches: 0
Key: 78. Matches: 0
Best key: 2. Using this key for the final decryption...
Start decryption...
Validating source or representative file...
Validating desination file...
Decryption is successfully ended.
Brute-force is successfully ended.
```

При возникновении проблем с доступом к файлам программа сообщит об ошибке.

### Статистический анализ
// TODO