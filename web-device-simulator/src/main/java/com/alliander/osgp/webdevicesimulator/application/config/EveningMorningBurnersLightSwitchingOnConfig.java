package com.alliander.osgp.webdevicesimulator.application.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.alliander.osgp.webdevicesimulator.application.tasks.EveningMorningBurnersLightSwitchingOn;

@Configuration
@EnableScheduling
@PropertySource("file:${osp//webDeviceSimulator/config}")
public class EveningMorningBurnersLightSwitchingOnConfig {

    private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_EVENING_MORNING_BURNER_LIGHTSWITCHING_ON_CRON_EXPRESSION = "autonomous.tasks.evening.morning.burner.lightswitching.on.cron.expression";
    private static final String PROPERTY_NAME_AUTONOMOUS_EVENING_MORNING_BURNER_LIGHTSWITCHING_ON_POOL_SIZE = "autonomous.tasks.evening.morning.burner.lightswitching.on.pool.size";
    private static final String PROPERTY_NAME_AUTONOMOUS_EVENING_MORNING_BURNER_LIGHTSWITCHING_ON_THREAD_NAME_PREFIX = "autonomous.tasks.evening.morning.burner.lightswitching.on.thread.name.prefix";

    @Resource
    private Environment environment;

    @Autowired
    private EveningMorningBurnersLightSwitchingOn eveningMorningBurnersLightSwitchingOn;

    @Bean
    public CronTrigger eveningMorningBurnerslightSwitchingOnTrigger() {
        final String cron = this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TASKS_EVENING_MORNING_BURNER_LIGHTSWITCHING_ON_CRON_EXPRESSION);
        return new CronTrigger(cron);
    }

    @Bean(destroyMethod = "shutdown")
    public TaskScheduler eveningMorningBurnerslightSwitchingOnTaskScheduler() {
        final ThreadPoolTaskScheduler eveningMorningBurnerslightSwitchingOnTaskScheduler = new ThreadPoolTaskScheduler();
        eveningMorningBurnerslightSwitchingOnTaskScheduler.setPoolSize(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_EVENING_MORNING_BURNER_LIGHTSWITCHING_ON_POOL_SIZE)));
        eveningMorningBurnerslightSwitchingOnTaskScheduler
        .setThreadNamePrefix(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_EVENING_MORNING_BURNER_LIGHTSWITCHING_ON_THREAD_NAME_PREFIX));
        eveningMorningBurnerslightSwitchingOnTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        eveningMorningBurnerslightSwitchingOnTaskScheduler.setAwaitTerminationSeconds(10);
        eveningMorningBurnerslightSwitchingOnTaskScheduler.initialize();
        eveningMorningBurnerslightSwitchingOnTaskScheduler.schedule(this.eveningMorningBurnersLightSwitchingOn,
                this.eveningMorningBurnerslightSwitchingOnTrigger());
        return eveningMorningBurnerslightSwitchingOnTaskScheduler;
    }

}
