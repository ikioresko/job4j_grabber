package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.Post;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class SqlRuParse {
    private final Set<Post> postFromSql;

    public SqlRuParse() {
        this.postFromSql = new HashSet<>();
    }

    public HashSet<Post> getSet() {
        return new HashSet<>(this.postFromSql);
    }

    public Elements getElements(String url, String cssQuery) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select(cssQuery);
    }

    public LocalDateTime parseLocalDateTime(String dataText) {
        return new SqlRuDateTimeParser().parseDateTime(dataText);
    }

    /**
     * Метод принимает значение количества страниц для парсинга. Вызывает метод getElements(),
     * который извлекает элементы аттрибута по указанной ссылке. Строка дата преобразуется
     * в формат LocalDateTime в классе {@link SqlRuDateTimeParser} методом parseDateTime(),
     * остальные строки конвертируются в текстовые и цифрые значения. Затем создается объект
     * класса {@link Post} в который при помощи Билдера вставляются его данные, затем
     * объект заносится в HashSet.
     *
     * @param quantityPage количество страниц для парсинга.
     * @throws IOException getDesc(), getElements()
     */
    public void run(int quantityPage) throws IOException {
        for (int i = 1; i < quantityPage + 1; i++) {
            Elements row = getElements(
                    "https://www.sql.ru/forum/job-offers/" + i, ".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                Element data = td.parent();
                LocalDateTime ldt = parseLocalDateTime(data.child(5).text());
                String link = href.attr("href");
                Post post = new Post.Builder()
                        .builderLink(link)
                        .builderTopicName(data.child(1).child(0).text())
                        .builderAuthor(data.child(2).text())
                        .builderDescription(getDesc(link))
                        .builderAnswers(Integer.parseInt(data.child(3).text()))
                        .builderViewCount(Integer.parseInt(data.child(4).text()))
                        .builderData(ldt).build();
                postFromSql.add(post);
            }
        }
    }

    public String getDesc(String link) throws IOException {
        Elements row = getElements(link, ".msgBody");
        return row.get(1).text();
    }

    public void print() {
        for (Post post : getSet()) {
            System.out.println(post.toString());
        }
    }

    public static void main(String[] args) throws IOException {
        SqlRuParse sql = new SqlRuParse();
        sql.run(1);
    }
}