package br.com.ilegra.challenge;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class ChallengeApplication {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public static void main(final String[] args) {
        SpringApplication.run(ChallengeApplication.class, args);
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void perform() throws Exception {
        final JobParameters params = new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters();
        jobLauncher.run(job, params);
    }
}
