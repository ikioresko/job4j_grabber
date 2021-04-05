package ru.job4j;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        int interval = getTime(new File("src/main/resources/rabbit.properties"));
        if (interval >= 1) {
            try {
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDetail job = newJob(Rabbit.class).build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(interval)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException se) {
                se.printStackTrace();
            }
        }
    }

    public static int getTime(File path) {
        int time = -1;
        try (BufferedReader reader = new BufferedReader(
                new FileReader(path))) {
            List<String> rsl = reader.lines().map(s -> s.split("="))
                    .filter(arr -> arr.length == 2)
                    .map(arr -> (arr[1]))
                    .collect(Collectors.toList());
            time = Integer.parseInt(rsl.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}
