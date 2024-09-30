package com.batch.config;

import com.batch.entities.Person;
import com.batch.step.PersonItemProcessor;
import com.batch.step.PersonItemReader;
import com.batch.step.PersonItemWriterStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfiguration {

    @Bean
    public PersonItemWriterStep personItemWriter(){
        return new PersonItemWriterStep();
    }

    @Bean
    public PersonItemReader personItemReader() {
        return new PersonItemReader();
    }

    @Bean
    public PersonItemProcessor personItemProcessor(){
        return new PersonItemProcessor();
    }


    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setQueueCapacity(5);
        return taskExecutor;
    }

    @Bean
    public Step readFile(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("readFileStep",jobRepository)
                .<Person, Person>chunk(10, transactionManager)
                .reader(personItemReader())
                .processor(personItemProcessor())
                .writer(personItemWriter())
                .taskExecutor(taskExecutor())
                .build();

    }

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("readFileWithChunk", jobRepository)
                .start(readFile(jobRepository,transactionManager))
                .build();
    }
}
