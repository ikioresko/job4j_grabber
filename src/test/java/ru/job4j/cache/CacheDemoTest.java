package ru.job4j.cache;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CacheDemoTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private final String ls = System.lineSeparator();
    public final String expected = "S0C Current survivor space 0 capacity (KB)." + ls
            + "S1C Current survivor space 1 capacity (KB)." + ls
            + "S0U Survivor space 0 utilization (KB)." + ls
            + "S1U Survivor space 1 utilization (KB).";

    private File getFile() throws IOException {
        File source = folder.newFile("Source.txt");
        try (PrintWriter out = new PrintWriter(source)) {
            out.println("S0C Current survivor space 0 capacity (KB).");
            out.println("S1C Current survivor space 1 capacity (KB).");
            out.println("S0U Survivor space 0 utilization (KB).");
            out.println("S1U Survivor space 1 utilization (KB).");
        }
        return source;
    }

    @Test
    public void whenAdd1File() throws IOException {
        File source = getFile();
        CacheDemo<String, String> demo = new CacheDemo<>(
                source.getParent() + "/");
        assertThat(demo.getFromCache("Source.txt").get(), is(expected));
    }

    @Test
    public void whenAddSomeFiles() throws IOException {
        File source1 = getFile();
        File source2 = folder.newFile("Source1.txt");
        File source3 = folder.newFile("Source2.txt");
        CacheDemo<String, String> demo = new CacheDemo<>(source1.getParent() + "/");
        demo.readAll();
        assertThat(demo.getFromCache("Source.txt").get(), is(expected));
    }

    @Test
    public void whenGetValueFromCache() throws IOException {
        File source1 = getFile();
        File source2 = source1;
        File source3 = source1;
        CacheDemo<String, String> demo = new CacheDemo<>(source1.getParent() + "/");
        demo.readAll();
        assertThat(demo.getFromCache("Source.txt").get(), is(expected));
    }

    @Test
    public void isExist() throws IOException {
        File source = getFile();
        CacheDemo<String, String> demo = new CacheDemo<>(source.getParent() + "/");
        assertThat(demo.isExist("Source.txt"), is(false));
        demo.readAll();
        assertThat(demo.isExist("Source.txt"), is(true));
    }
}