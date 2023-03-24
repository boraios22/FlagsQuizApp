package com.itstep.fragmentandactivity.core;

import android.graphics.Bitmap;

import java.util.List;

public class QuizModel {
    public Bitmap bitmap;
    public List<String> answers;

    public QuizModel(Bitmap bitmap, List<String> answers) {
        this.bitmap = bitmap;
        this.answers = answers;
    }
}
