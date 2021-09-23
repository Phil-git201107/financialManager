package com.chiczu.fmoney.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 執行緒池配置
 *
 */

@Configuration
@EnableAsync
public class ThreadPoolTaskConfig implements AsyncConfigurer {

    private static final int corePoolSize = 10;       		// 核心執行緒數（預設執行緒數）
    private static final int maxPoolSize = 50;			    // 最大執行緒數
    private static final int keepAliveTime = 30;			// 允許執行緒空閒時間（單位：預設為秒）
    private static final int queueCapacity = 100;			// 緩衝佇列數
    private static final String threadNamePrefix = "Async-Service-"; // 執行緒池名字首

    @Bean("taskExecutor") // bean的名稱，預設為首字母小寫的方法名
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setAwaitTerminationSeconds(1800);
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 執行緒池對拒絕任務的處理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }

//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return new CustomAsyncExceptionHandler();
//    }
}
