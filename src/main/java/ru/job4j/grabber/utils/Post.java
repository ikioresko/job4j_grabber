package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс является моделью данных которая описывает объявление на сайте sql.ru
 * Для создания объекта используется Билдер.
 * Методы equals и hashCode не используют поле viewCount, ввиду того что оно всегда увеличивается.
 */
public class Post {
    private String link;
    private String topicName;
    private String author;
    private String description;
    private int answers;
    private int viewCount;
    private LocalDateTime data;

    public static class Builder {
        private Post newPost;

        public Builder() {
            newPost = new Post();
        }

        public Builder builderLink(String link) {
            newPost.link = link;
            return this;
        }

        public Builder builderTopicName(String topicName) {
            newPost.topicName = topicName;
            return this;
        }

        public Builder builderAuthor(String author) {
            newPost.author = author;
            return this;
        }

        public Builder builderDescription(String description) {
            newPost.description = description;
            return this;
        }

        public Builder builderAnswers(int answers) {
            newPost.answers = answers;
            return this;
        }

        public Builder builderViewCount(int viewCount) {
            newPost.viewCount = viewCount;
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
    public String toString() {
        return "Post{"
                + "link='" + link
                + ", topicName='" + topicName
                + ", author='" + author
                + ", description='" + description
                + ", answers=" + answers
                + ", viewCount=" + viewCount
                + ", data=" + data
                + '}';
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
        return answers == post.answers
                && Objects.equals(link, post.link)
                && Objects.equals(topicName, post.topicName)
                && Objects.equals(author, post.author)
                && Objects.equals(description, post.description)
                && Objects.equals(data, post.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, topicName, author, description, answers, data);
    }
}
