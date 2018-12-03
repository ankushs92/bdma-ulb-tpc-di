package bdma.ulb.tpcdi.config

import groovy.util.logging.Slf4j
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer

import java.time.LocalDateTime
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
@Slf4j
class AsyncConfig implements AsyncConfigurer {

    private static final int cores = Runtime.runtime.availableProcessors()
    private static final ExecutorService executorService = Executors.newFixedThreadPool(cores * 3)

    @Override
    @Bean("parallelLoaderExecutor")
    Executor getAsyncExecutor() {
        executorService
    }

    @Override
    AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        { ex, method, params->
            log.info("Async Error occured at Date Time {} , {} ", LocalDateTime.now(), ex)
        } as AsyncUncaughtExceptionHandler
    }

}
