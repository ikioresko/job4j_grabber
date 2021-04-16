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
 * @version 0.1
 */
public class CacheDemo<K, V> {
    private final String path;
    private final Map<K, SoftReference<V>> map;
    private final String ln = System.lineSeparator();

    public CacheDemo(String path) {
        this.path = path;
        map = new HashMap<>();
    }

    public Map<K, SoftReference<V>> getMap() {
        return map;
    }

    /**
     * Возвращает значение из отображения по ключу, ключ является названием файла
     *
     * @param filename Имя файла в виде "filename.txt"
     * @return содержимое файла обернутое SoftReference
     * @throws IOException наследуется от getValue()
     */
    public SoftReference<V> getFromCache(String filename) throws IOException {
        SoftReference<V> ref = map.get(filename);
        if (ref == null) {
            readOne(filename);
        }
        return ref;
    }

    /**
     * Совершает единичную вставку в отображение Map<K, SoftReference<V>>
     *
     * @param file Имя файла в виде "filename.txt"
     * @throws IOException наследуется от getValue()
     */
    public void readOne(String file) throws IOException {
        map.put((K) file, getValue(file));
    }

    /**
     * Извлекает содержимое конкретного файла
     *
     * @param file Имя файла в виде "filename.txt"
     * @return содержимое файла обернутое SoftReference
     * @throws FileNotFoundException FileReader() если указанный файл не существует,
     *                               является каталогом, а не обычным файлом,
     *                               или по какой-либо другой причине не может
     *                               быть открыт для чтения.
     */
    private SoftReference getValue(String file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(path + "" + file));
        return new SoftReference(reader
                .lines()
                .collect(Collectors.joining(System.lineSeparator())));
    }

    /**
     * Обепечивает консольный интерфейс для управления методоами
     *
     * @throws IOException наследуется от getValue()
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
                if (value == null) {
                    System.out.println("File added in to cache if exist, try check again");
                } else {
                    System.out.println(value.get()
                            + ln
                            + "Are you happy with this result?");
                }
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
     * @throws IOException наследуется от getValue() или Files.walk() - если возникает
     *                     ошибка ввода-вывода при доступе к начальному файлу
     */
    public void readAll() throws IOException {
        List<Path> list = Files.walk(Path.of(path)).skip(1).collect(Collectors.toList());
        for (Path p : list) {
            SoftReference value = getValue(p.getFileName().toString());
            map.putIfAbsent((K) p.getFileName().toString(), value);
        }
    }

    public static void main(String[] args) throws IOException {
        CacheDemo<String, String> demo = new CacheDemo<>("C:\\test2\\");
        demo.run();
    }
}
