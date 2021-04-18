package ru.job4j.cache;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
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
 * @version 0.4
 */
public class CacheDemo<K, V> {
    private final String path;
    private final Map<K, SoftReference<V>> map;

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
    public V getFromCache(String filename) {
        V reference;
        if (isExist(filename)) {
            reference = map.get(filename).get();
            if (reference == null) {
                reference = readOne(filename).get();
            }
        } else {
            reference = readOne(filename).get();
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
     * Извлекает содержимое конкретного файла, оборачивает его
     * в SoftReference чтобы поместить в кеш
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
}