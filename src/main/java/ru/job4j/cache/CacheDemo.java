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
 * @version 0.2
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
     * Возвращает значение из отображения по ключу, ключ является названием файла
     *
     * @param filename Имя файла в виде "filename.txt"
     * @return Value отображения извлеченное из SoftReference
     */
    public V getFromCache(String filename) {
        readOne(filename);
        return map.get(filename).get();
    }

    /**
     * Совершает единичную вставку в отображение Map<K, SoftReference<V>>
     *
     * @param file Имя файла в виде "filename.txt"
     */
    public void readOne(String file) {
        map.putIfAbsent((K) file, getValue(file));
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
                V value = getFromCache(input);
                System.out.println("File added in to cache"
                        + ln
                        + value
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
