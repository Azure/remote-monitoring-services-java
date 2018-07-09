package com.microsoft.azure.iotsolutions.devicetelemetry.RecurringTasksAgent;

import com.google.inject.Singleton;
import play.Logger;

import java.util.concurrent.CompletableFuture;

@Singleton
public class RecurringTasks implements IRecurringTasks {
    private static final Logger.ALogger log = Logger.of(RecurringTasks.class);

    public RecurringTasks(){
        CompletableFuture.runAsync(() -> this.run());
    }

    @Override
    public void run() {
        tryMethod();
    }

    private void tryMethod(){
        while(true){
            log.info("Testing...");
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
