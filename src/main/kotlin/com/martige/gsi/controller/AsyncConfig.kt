package com.martige.gsi.controller

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig {
    @Bean(name = ["asyncExecutor"])
    fun asyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = Runtime.getRuntime().availableProcessors()
        executor.maxPoolSize = Runtime.getRuntime().availableProcessors()
        executor.setQueueCapacity(100)
        executor.setThreadNamePrefix("AsynchThread-")
        executor.initialize()
        return executor
    }
}
