package ru.job4j.quartz;

import org.slf4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.*;

import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(AlertRabbit.class.getName());
    private int interval;
    private Connection connection;

    public AlertRabbit() throws Exception {
        initConnect();
    }

    public int getInterval() {
        return interval;
    }

    private void initConnect() throws Exception {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream(
                "rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            interval = Integer.parseInt(config.getProperty("rabbit-interval"));
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password"));
        }
    }

    public static void main(String[] args) {
        try (AlertRabbit rabbit = new AlertRabbit()) {
            int interval = rabbit.getInterval();
            if (interval >= 1) {
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("connect", rabbit.connection);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(interval)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Connection connection = (Connection) context
                    .getJobDetail()
                    .getJobDataMap()
                    .get("connect");
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into rabbit(created_date) values(now())")) {
                statement.execute();
            } catch (Exception e) {
                LOG.error("Exception", e);
            }
        }
    }
}
