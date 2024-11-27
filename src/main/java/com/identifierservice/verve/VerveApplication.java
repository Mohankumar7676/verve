package com.identifierservice.verve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class VerveApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerveApplication.class, args);
	}

	@Bean(name = "verve-async-thread")
	public TaskExecutor createTaskExecutor() {
		ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
		ex.setThreadNamePrefix("verve-async-thread-");
		ex.afterPropertiesSet();
		ex.setWaitForTasksToCompleteOnShutdown(true);
		ex.initialize();
		return ex;
	}

	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
