package ru.job4j.grabber.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.job4j.html.SqlRuParse;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertThat;

public class SqlRuDateTimeParserTest {
    @Test
    public void parseDateAndTime() {
        SqlRuParse parse = new SqlRuParse();
        List<String> list = List.of("сегодня, 19:23", "вчера, 9:15", "2 янв 21, 7:30");
        LocalDateTime rsl1 = parse.parseLocalDateTime(list.get(0));
        LocalDateTime rsl2 = parse.parseLocalDateTime(list.get(1));
        LocalDateTime rsl3 = parse.parseLocalDateTime(list.get(2));
        LocalDateTime exp1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(19, 23));
        LocalDateTime exp2 = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(9, 15));
        LocalDateTime exp3 = LocalDateTime.of(
                LocalDate.of(2021, 1, 2), LocalTime.of(7, 30));
        Assert.assertEquals(rsl1, exp1);
        Assert.assertEquals(rsl2, exp2);
        Assert.assertEquals(rsl3, exp3);
    }

    @Test
    public void parseSomePage() throws IOException {
        SqlRuParse parse1Page = new SqlRuParse();
        List<Post> list = parse1Page.list("https://www.sql.ru/forum/job-offers/");
        assertThat(list.size(), is(53));
    }
}