package com.itstep.fragmentandactivity.provider;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.itstep.fragmentandactivity.model.FlagInfoModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameDataProvider {
    private List<FlagInfoModel> flagInfoModelList;
    private static GameDataProvider ins;

    private int highScore;

    public static GameDataProvider getIns() {
        if (ins == null){
            ins = new GameDataProvider();
        }
        return ins;
    }
    private GameDataProvider(){}

    public void init(AssetManager assetManager) {
        flagInfoModelList = getList(assetManager);
    }
    private List<FlagInfoModel> getList(AssetManager assetManager){
        List<FlagInfoModel> flagInfoModelList = new ArrayList<>();
        String rootPath = "flags/";
        try {
            List<String> countries = loadCountryName(assetManager);
            String[] list = assetManager.list(rootPath);
            for (String f : list){
                String fullPath = rootPath + f;
                InputStream inputStream = assetManager.open(fullPath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                String found = null;
                for (String correctName : countries){
                    if (f.toLowerCase().contains(correctName.toLowerCase())){
                        found = correctName;
                        break;
                    }
                }

                if (found != null){
                    flagInfoModelList.add(new FlagInfoModel(found,fullPath, bitmap ));
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return flagInfoModelList;
    }

    private List<String> loadCountryName(AssetManager assetManager){
        List<String> names = new ArrayList<>();
        try {
            InputStream inputStream = assetManager.open("countries");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = bufferedReader.readLine()) != null){
                names.add(line);
               // Log.d("GameDAtaProvider", "loadCountryName: " + line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return names;
    }

    public List<FlagInfoModel> getFlagInfoModelList() {
        return flagInfoModelList;
    }
    public int getFlagsSize() {
        return flagInfoModelList.size();
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}
