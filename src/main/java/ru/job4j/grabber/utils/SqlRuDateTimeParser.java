package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class SqlRuDateTimeParser implements DateTimeParser {
    public final Map<String, Integer> map = new HashMap<>();

    public SqlRuDateTimeParser() {
        insert();
    }

    public void insert() {
        map.put("янв", 1);
        map.put("фев", 2);
        map.put("мар", 3);
        map.put("апр", 4);
        map.put("май", 5);
        map.put("июн", 6);
        map.put("июл", 7);
        map.put("авг", 8);
        map.put("сен", 9);
        map.put("окт", 10);
        map.put("ноя", 11);
        map.put("дек", 12);
    }

    /**
     * Метод принимает строку с датой и преобразует ее в формат LocalDateTime.
     *
     * @param date Строка с датой в виде "сегодня, 19:23" или "вчера, 9:15" или "2 янв 21, 7:30";
     * @return LocalDateTime в формате "2021-03-29T19:23".
     */
    @Override
    public LocalDateTime parseDateTime(String date) {
        LocalDateTime ldt;
        String[] time = date.replace(",", "").split(" ");
        int lengthDate = time.length;
        String[] hm = time[lengthDate - 1].split(":");
        int hours = Integer.parseInt(hm[0]);
        int minute = Integer.parseInt(hm[1]);
            if (lengthDate == 2) {
                ldt = LocalDateTime.of(LocalDate.now(), LocalTime.of(hours, minute));
                if (date.contains("вчера")) {
                    ldt = ldt.minusDays(1);
                }
            } else {
                int day = Integer.parseInt(time[0]);
                int month = map.get(time[1]);
                int year = Integer.parseInt("20" + time[2]);
                ldt = LocalDateTime.of(year, month, day, hours, minute);
            }
        return ldt;
    }
}
