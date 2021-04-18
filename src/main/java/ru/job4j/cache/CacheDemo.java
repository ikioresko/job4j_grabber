package ru.job4j.cache;

import java.io.*;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Класс представляет собой структуру данных типа кеш. Считывает текстовые файлы из системы,
 * и если они отсутствуют в памяти, добавляет их в отображение.
 * Все файлы находятся в одной директории которая указывается при создании
 * экземпляра данного класса в виде "C:\test\"
 *
 * @param <K> Ключом являетсся имя файла в виде "filename.txt"
 * @param <V> Значением является содержимое файла обернутое SoftReference
 * @author Kioresko Igor
 * @version 0.3
 */
public class CacheDemo<K, V> {
    private final String path;
    private final Map<K, SoftReference<V>> map;
    private final String ln = System.lineSeparator();

    public CacheDemo(String path) {
        this.path = path;
        map = new HashMap<>();
    }

    /**
     * Проверяет содержится ли ключ в отображении. Если существует - запрашивает value
     * иначе вызывает readOne() чтобы добавить Key & Value в отображение
     * Возвращает значение из отображения по ключу, ключ является названием файла
     *
     * @param filename Имя файла в виде "filename.txt"
     * @return Value отображения извлеченное из SoftReference
     */
    public SoftReference<V> getFromCache(String filename) {
        SoftReference reference = null;
        if (isExist(filename)) {
            reference = map.get(filename);
        } else {
            reference = readOne(filename);
        }
        return reference;
    }

    /**
     * Проверяет наличие Key в отображении
     *
     * @param filename Имя файла в виде "filename.txt"
     * @return true если ключ есть в кеше, иначе false
     */
    public boolean isExist(String filename) {
        return map.containsKey(filename);
    }

    /**
     * Совершает единичную вставку в отображение Map<K, SoftReference<V>>
     *
     * @param file Имя файла в виде "filename.txt"
     * @return содержимое файла обернутое SoftReference
     */
    public SoftReference<V> readOne(String file) {
        SoftReference reference = getValue(file);
        map.put((K) file, reference);
        return reference;
    }

    /**
     * Извлекает содержимое конкретного файла, оборачивает его в SoftReference
     *
     * @param file Имя файла в виде "filename.txt"
     * @return содержимое файла обернутое SoftReference
     */
    private SoftReference getValue(String file) {
        SoftReference reference = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(path + "" + file))) {
            reference = new SoftReference(reader
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reference;
    }

    /**
     * Обепечивает консольный интерфейс для управления методоами
     *
     * @throws IOException наследуется от readAll()
     */
    public void run() throws IOException {
        boolean run = true;
        Scanner scan = new Scanner(System.in);
        while (run) {
            System.out.println("Select options and enter num:"
                    + ln
                    + "1 - Check file in a cache"
                    + ln
                    + "2 - Add all from folder: " + path
                    + ln
                    + "3 - EXIT");
            String input = scan.nextLine();
            if (input.equals("1")) {
                System.out.println("Input a filename like: \"filename.txt\""
                        + ln
                        + "File will be checked in this folder: " + path);
                input = scan.nextLine();
                SoftReference value = getFromCache(input);
                System.out.println("File added in to cache"
                        + ln
                        + value.get()
                        + ln
                        + "Are you happy with this result?");
            }
            if (input.equals("2")) {
                readAll();
            }
            if (input.equals("3")) {
                run = false;
            }
        }
    }

    /**
     * Добавляет все фалйы из папки указанной в поле path, в отображение Map<K, SoftReference<V>>
     *
     * @throws IOException Files.walk() - если возникает ошибка ввода-вывода
     *                     при доступе к начальному файлу
     */
    public void readAll() throws IOException {
        List<Path> list = Files.walk(Path.of(path)).skip(1).collect(Collectors.toList());
        for (Path p : list) {
            readOne(p.getFileName().toString());
        }
    }

    public static void main(String[] args) throws IOException {
        CacheDemo<String, String> demo = new CacheDemo<>("C:\\test2\\");
        demo.run();
    }
}
