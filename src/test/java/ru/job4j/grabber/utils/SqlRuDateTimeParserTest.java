package ru.job4j.grabber.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.job4j.html.SqlRuParse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class SqlRuDateTimeParserTest {

    @Test
    public void parseDateAndTime() {
        List<String> list = List.of("сегодня, 19:23", "вчера, 9:15", "2 янв 21, 7:30");
        LocalDateTime rsl1 = SqlRuParse.parseLocalDateTime(list.get(0));
        LocalDateTime rsl2 = SqlRuParse.parseLocalDateTime(list.get(1));
        LocalDateTime rsl3 = SqlRuParse.parseLocalDateTime(list.get(2));
        LocalDateTime exp1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(19, 23));
        LocalDateTime exp2 = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(9, 15));
        LocalDateTime exp3 = LocalDateTime.of(
                LocalDate.of(2021, 1, 2), LocalTime.of(7, 30));
        Assert.assertEquals(rsl1, exp1);
        Assert.assertEquals(rsl2, exp2);
        Assert.assertEquals(rsl3, exp3);
    }
}