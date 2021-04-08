package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;

public class SqlRuParse {

    public static Elements getElements(String url, String cssQuery) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select(cssQuery);
    }

    public static LocalDateTime parseLocalDateTime(String dataText) {
        return new SqlRuDateTimeParser().parseDateTime(dataText);
    }

    public static void main(String[] args) throws Exception {
        Elements row = getElements(
                "https://www.sql.ru/forum/job-offers", ".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            Element data = td.parent();
            LocalDateTime ld =  parseLocalDateTime(data.child(5).text());
            System.out.println(ld);
            System.out.println(href.attr("href"));
            System.out.println(data.child(5).text());
            System.out.println(href.text() + " Дата: " + data.child(5).text());
        }
    }
}