package com.zjtelcom.cpct.service.thread;

public class MyThread extends Thread {

    private int index;

    public MyThread(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }

    /*public void setIndex(int index){
        this.index = index;
    }*/
}
