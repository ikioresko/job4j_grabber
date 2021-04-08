package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SqlRuParse {
    private final Map<String, String> map;

    public SqlRuParse() {
        this.map = new HashMap<>();
    }

    public Map<String, String> getMap() {
        return new HashMap<>(this.map);
    }

    public static Elements getElements(String url, String cssQuery) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select(cssQuery);
    }

    public static LocalDateTime parseLocalDateTime(String dataText) {
        return new SqlRuDateTimeParser().parseDateTime(dataText);
    }

    public void run(int quantityPage) throws IOException {
        for (int i = 1; i < quantityPage + 1; i++) {
            Elements row = getElements(
                    "https://www.sql.ru/forum/job-offers/" + i, ".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                Element data = td.parent();
                String link = href.attr("href");
                String topicName = href.text() + " Дата: " + data.child(5).text();
                map.put(topicName, link);
            }
        }
    }

    public void print() {
        for (Map.Entry<String, String> s : map.entrySet()) {
            System.out.println(s.getKey());
            System.out.println(s.getValue());
        }
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse parse = new SqlRuParse();
        parse.run(1);
        parse.print();
    }
}