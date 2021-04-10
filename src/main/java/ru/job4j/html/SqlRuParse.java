package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.Post;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    public Elements getElements(String url, String cssQuery) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select(cssQuery);
    }

    public LocalDateTime parseLocalDateTime(String dataText) {
        return new SqlRuDateTimeParser().parseDateTime(dataText);
    }

    /**
     * Метод принимает ссылку на страницу с объявлениями вакансий,
     * парсит ссылку на каждое объявление и передает ее в метод detail(),
     * который возвращает объект Post содержащий детали одного поста.
     * Затем объект добавляется в лист.
     *
     * @param link ссылка на страницу с объявлениями.
     * @return Список всех объявлений, с их содержимым.
     * @throws IOException detail(), getElements()
     */
    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> list = new ArrayList<>();
        Elements row = getElements(link, ".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            String linkOfPost = href.attr("href");
            list.add(detail(linkOfPost));
        }
        return list;
    }

    /**
     * Метод принимает ссылку на конкретный пост, извлекает из него имя вакансии,
     * описание вакансии, дату его размещения, и размещает полученные данные в
     * объекте класса {@link Post}
     *
     * @param link Ссылка на конкретное объявление.
     * @return объект Post содержащий детали одного объявления.
     * @throws IOException getElements()
     */
    @Override
    public Post detail(String link) throws IOException {
        Post post;
        Elements row = getElements(link, ".msgTable");
        Element el = row.get(0);
        String data = el.child(0).child(2).text();
        int index = data.indexOf("[");
        data = data.substring(0, index - 1);
        LocalDateTime ldt = parseLocalDateTime(data);
        post = new Post.Builder()
                .builderName(el.child(0).child(0).text())
                .builderText(el.child(0).child(1).child(1).text())
                .builderLink(link)
                .builderData(ldt)
                .build();
        return post;
    }

    public static void main(String[] args) throws IOException {
        SqlRuParse sql = new SqlRuParse();
        List<Post> list = sql.list("https://www.sql.ru/forum/job-offers");
        for (Post p : list) {
            System.out.println(p.toString());
        }
    }
}