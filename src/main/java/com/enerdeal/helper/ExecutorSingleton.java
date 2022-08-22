package com.enerdeal.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Chigozirim on 29/08/2019.
 */
public class ExecutorSingleton {
    private final ExecutorService emailExecutor = Executors.newCachedThreadPool();
    private static ExecutorSingleton ourInstance = new ExecutorSingleton();

    public static ExecutorSingleton getInstance() {
        return ourInstance;
    }

    private ExecutorSingleton() {
    }

    public void execute(Runnable runnable){
        this.emailExecutor.execute(runnable);
    }
}
