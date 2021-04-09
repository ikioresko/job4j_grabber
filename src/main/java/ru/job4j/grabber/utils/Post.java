package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс является моделью данных которая описывает объявление на сайте sql.ru
 * Для создания объекта используется Билдер.
 */
public class Post {
    private int id;
    private String name;
    private String text;
    private String link;
    private LocalDateTime data;

    public static class Builder {
        private Post newPost;

        public Builder() {
            newPost = new Post();
        }

        public Builder builderId(int id) {
            newPost.id = id;
            return this;
        }

        public Builder builderName(String name) {
            newPost.name = name;
            return this;
        }

        public Builder builderText(String text) {
            newPost.text = text;
            return this;
        }

        public Builder builderLink(String link) {
            newPost.link = link;
            return this;
        }

        public Builder builderData(LocalDateTime data) {
            newPost.data = data;
            return this;
        }

        public Post build() {
            return newPost;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id == post.id
                && Objects.equals(name, post.name)
                && Objects.equals(text, post.text)
                && Objects.equals(link, post.link)
                && Objects.equals(data, post.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, text, link, data);
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", name='" + name
                + ", text='" + text
                + ", link='" + link
                + ", data=" + data
                + '}';
    }
}
