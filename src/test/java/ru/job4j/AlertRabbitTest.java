package ru.job4j;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class AlertRabbitTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void getTime() throws IOException {
        File source = folder.newFile("source.txt");
        try (PrintWriter out = new PrintWriter(source)) {
            out.println("rabbit.interval=1");
        }
        assertThat(AlertRabbit.getTime(source), is(1));
    }
}