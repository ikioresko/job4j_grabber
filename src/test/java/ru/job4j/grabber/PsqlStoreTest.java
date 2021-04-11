package ru.job4j.grabber;

import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PsqlStoreTest {
    public Connection init() {
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            return DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void savePost() {
        try (PsqlStore store = new PsqlStore(ConnectionRollback.create(this.init()))) {
            Post p = new Post.Builder()
                    .builderName("Name")
                    .builderText("Text")
                    .builderLink("http link")
                    .builderData(LocalDateTime.of(2020, 1, 1, 22, 22))
                    .build();
            store.save(p);
            assertThat(store.getAll().size(), is(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findByID() {
        try (PsqlStore store = new PsqlStore(ConnectionRollback.create(this.init()))) {
            Post p = new Post.Builder()
                    .builderName("Name")
                    .builderText("Text")
                    .builderLink("http link")
                    .builderData(LocalDateTime.of(2020, 1, 1, 22, 22))
                    .build();
            store.save(p);
            int id = p.getId();
            Post expected = new Post.Builder()
                    .builderId(id)
                    .builderName("Name")
                    .builderText("Text")
                    .builderLink("http link")
                    .builderData(LocalDateTime.of(2020, 1, 1, 22, 22))
                    .build();
            assertThat(store.findById(id), is(expected));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}