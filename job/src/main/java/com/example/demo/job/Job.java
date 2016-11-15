package com.example.demo.job;

import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

/**
 * Created by tfisher on 26/10/2016.
 */

public abstract class Job {
    protected AppCompatActivity appCompatActivity;

    public Job(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public abstract void perform() throws Exception;
}
