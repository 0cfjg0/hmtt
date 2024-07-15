package com.admin.utils;

public class ThreadLocalUtils {

    public static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<>();

    public static void setCurrentUser(Integer userId){
        THREAD_LOCAL.set(userId);
    }

    public static Integer getCurrentUser(){
        return THREAD_LOCAL.get();
    }

    public static void removceCurrentUser(){
        THREAD_LOCAL.remove();
    }
}
