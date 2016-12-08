package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.CarersScheduler;
import gov.dwp.carers.cs.monitoring.DBHealthCheck;
import gov.dwp.carers.cs.monitoring.DfConnectionCheck;
import gov.dwp.carers.cs.monitoring.DrConnectionCheck;
import gov.dwp.carers.cs.service.database.DatabasePurgeServiceImpl;
import gov.dwp.carers.monitor.MonitorRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(exclude = JmsAutoConfiguration.class)
@ComponentScan(basePackages = {"gov.dwp.carers"})
@PropertySource("classpath:/config/application-info.properties")
public class CsApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(CsApplication.class);

    @Value("${server.port}")
    private String serverPort;

    @Value("${env.name}")
    private String envName;

    @Value("${application.name}")
    private String appName;

    @Inject
    private MonitorRegistration monitorRegistration;

    private CarersScheduler carersScheduler;

    @PostConstruct
    public void onStart() throws Exception {
        LOGGER.info("STARTING application with - serverPort:{} envName:{} appName:{}", serverPort, envName, appName);
        LOGGER.info("{} is now STARTING.", appName);
        monitorRegistration.registerReporters();
        LOGGER.info("{} STARTED.", appName);
    }

    @PreDestroy
    public void onStop() {
        LOGGER.info("STOPPING application with - serverPort:{} envName:{} appName:{}", serverPort, envName, appName);
        monitorRegistration.unRegisterReporters();
        monitorRegistration.unRegisterHealthChecks();
        carersScheduler.stop();
        LOGGER.info("{} STOPPED.", appName);
    }

    @Inject
    private void registerHealthChecks(final DrConnectionCheck drConnectionCheck,
                                      final DfConnectionCheck dfConnectionCheck,
                                      final DBHealthCheck dbHealthCheck) {
        LOGGER.info("{} - registering health checks.", appName);
        monitorRegistration.registerHealthChecks(Arrays.asList(drConnectionCheck, dfConnectionCheck, dbHealthCheck));
    }

    @Inject
    private void startPurgeDatabaseSchedule(final DatabasePurgeServiceImpl databasePurgeServiceImpl,
                                            @Value("${database.purge.scheduler.hours}") final Long databasePurgeSchedulerHours) {
        LOGGER.info("{} - starting purge database scheduler to run every {} hours.", appName, databasePurgeSchedulerHours);
        this.carersScheduler = new CarersScheduler(appName, "purge-database", databasePurgeServiceImpl);
        this.carersScheduler.start(databasePurgeSchedulerHours, TimeUnit.HOURS);
    }

    public static void main(final String... args) throws Exception {
        final SpringApplication springApplication = new SpringApplication(CsApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        springApplication.run(args);
    }
}
