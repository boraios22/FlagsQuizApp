package com.itstep.fragmentandactivity.core;

import android.graphics.Bitmap;

import com.itstep.fragmentandactivity.model.FlagInfoModel;
import com.itstep.fragmentandactivity.provider.GameDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameCore {
    int life = 3;
    FlagInfoModel selected;
    Random randomizer = new Random();

    List<String> answers = new ArrayList<>();

    public QuizModel generateRandomQuiz(){
        answers.clear();
        int index = randomizer.nextInt(GameDataProvider.getIns().getFlagsSize());
        FlagInfoModel model = GameDataProvider.getIns().getFlagInfoModelList().get(index);
        selected = model;
        answers.add(selected.getCountryName());
        generateFake();
        generateFake();

        shuffleArray(answers);

     return new QuizModel(selected.getBitmap(), answers);
    }

    static void shuffleArray(List<String> ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.size() - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar.get(index);
            //ar[index] = ar[i];
            ar.set(index, ar.get(i));
            ar.set(i, a);
        }
    }

    public boolean isCorrect(String answered){
        return answered.equals(selected.getCountryName());
    }

    public void  cutLife(){
        life--;
    }

    public boolean outOfLife(){
        return life <= 0;
    }

    public int getLife() {
        return life;
    }

    public void reset(){
        life = 3;
    }

    private void generateFake(){
        int index = randomizer.nextInt(GameDataProvider.getIns().getFlagsSize());
        FlagInfoModel model = GameDataProvider.getIns().getFlagInfoModelList().get(index);
        while (answers.contains(model.getCountryName())){
            index = randomizer.nextInt(GameDataProvider.getIns().getFlagsSize());
            model = GameDataProvider.getIns().getFlagInfoModelList().get(index);
        }

        answers.add(model.getCountryName());
    }
}
