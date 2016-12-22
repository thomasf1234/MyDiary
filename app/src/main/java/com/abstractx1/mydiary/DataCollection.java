package com.abstractx1.mydiary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;

/**
 * Created by tfisher on 15/11/2016.
 */
//singleton class
public class DataCollection {
    private int questionNumber;
    private String question;
    private File recording;
    private String answer;

    public DataCollection(int questionNumber, String question) {
        this.questionNumber = questionNumber;
        this.question = question;
        setAnswer("");
    }

    public int getQuestionNumber() { return questionNumber; }

    public String getQuestion() {
        return question;
    }

    public String getFullQuestion() {
        return getQuestionNumber() + ") " + getQuestion();
    }

    public String getShortQuestion() {
        return Utilities.getShortString(getFullQuestion(), 50);
    }

    public String getAnswer() {
        return answer;
    }

    public String getShortAnswer() {
        return Utilities.getShortString(getAnswer(), 50);
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setRecording(File recording) {
        this.recording = recording;
    }

    public File getRecording() {
        return recording;
    }

    public void clearRecording() {
        recording.delete();
        this.recording = null;
    }

    public boolean hasRecording() {
        return recording != null;
    }

    public boolean hasAnswer() {
        return !Utilities.isNullOrBlank(getAnswer());
    }
}