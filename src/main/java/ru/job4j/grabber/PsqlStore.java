package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection cnn;

    public PsqlStore(Connection cnn) {
        this.cnn = cnn;
    }

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        cnn = DriverManager.getConnection(cfg.getProperty("url"),
                cfg.getProperty("username"),
                cfg.getProperty("password"));
    }

    public Post postGet(ResultSet rs) throws SQLException {
        return new Post.Builder()
                .builderId(rs.getInt("id"))
                .builderName(rs.getString("name"))
                .builderText(rs.getString("text"))
                .builderLink(rs.getString("link"))
                .builderData(
                        rs.getTimestamp("created").toLocalDateTime())
                .build();
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "insert into post(name, text, link, created) values (?, ?, ?, ?)"
                        + "ON CONFLICT DO NOTHING",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getName());
            statement.setString(2, post.getText());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getData()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(postGet(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Post findById(int id) {
        Post result = null;
        try (PreparedStatement statement =
                     cnn.prepareStatement(
                             "select * from post where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result = postGet(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        Properties cfg = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            cfg.load(in);
            PsqlStore store = new PsqlStore(cfg);
            Post p = new Post.Builder()
                    .builderName("Name")
                    .builderText("Text")
                    .builderLink("http link")
                    .builderData(LocalDateTime.of(2020, 1, 1, 22, 22))
                    .build();
            store.save(p);
            int id = p.getId();
            System.out.println(store.getAll().toString());
            System.out.println(store.findById(id).toString());
        }
    }
}