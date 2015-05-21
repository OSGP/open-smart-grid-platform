package com.alliander.osgp.webdevicesimulator.application.config;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.alliander.osgp.webdevicesimulator.application.tasks.AutonomousDeviceRegister;

@Configuration
@EnableScheduling
@PropertySource("file:${osp//webDeviceSimulator/config}")
public class AutonomousDeviceRegisterConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutonomousDeviceRegisterConfig.class);

    private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_CRON_EXPRESSION = "autonomous.tasks.device.registration.cron.expression";
    private static final String PROPERTY_NAME_AUTONOMOUS_POOL_SIZE = "autonomous.task.device.registration.pool.size";
    private static final String PROPERTY_NAME_AUTONOMOUS_THREAD_NAME_PREFIX = "autonomous.task.device.registration.thread.name.prefix";

    @Resource
    private Environment environment;

    @Autowired
    private AutonomousDeviceRegister autonomousDeviceRegister;

    @Bean
    public CronTrigger autonomousDeviceRegisterTrigger() {
        final String cron = this.environment.getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TASKS_CRON_EXPRESSION);
        return new CronTrigger(cron);
    }

    @Bean(destroyMethod = "shutdown")
    public TaskScheduler deviceRegistrationTaskScheduler() {
        final ThreadPoolTaskScheduler deviceRegistrationTaskScheduler = new ThreadPoolTaskScheduler();
        deviceRegistrationTaskScheduler.setPoolSize(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_POOL_SIZE)));
        deviceRegistrationTaskScheduler.setThreadNamePrefix(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_THREAD_NAME_PREFIX));
        deviceRegistrationTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        deviceRegistrationTaskScheduler.setAwaitTerminationSeconds(10);
        deviceRegistrationTaskScheduler.initialize();
        deviceRegistrationTaskScheduler.schedule(this.autonomousDeviceRegister, this.autonomousDeviceRegisterTrigger());
        return deviceRegistrationTaskScheduler;
    }

}
