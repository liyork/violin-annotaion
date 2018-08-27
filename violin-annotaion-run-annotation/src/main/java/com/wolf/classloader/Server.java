package com.wolf.classloader;

import com.wolf.annotation.Log;

/**
 */
public class Server implements Runnable {

    @Log
    @Override
    public void run() {
        System.out.println("server start...");
        System.out.println("current class loader: " + String.valueOf(this.getClass().getClassLoader().getClass().getName()));
    }
}
