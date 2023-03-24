package com.itstep.fragmentandactivity.provider;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.itstep.fragmentandactivity.model.FlagInfoModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GameDataProvider {
    private List<FlagInfoModel> flagInfoModelList;
    private static GameDataProvider ins;
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
            String[] list = assetManager.list(rootPath);
            for (String f : list){
                String fullPath = rootPath + f;
                InputStream inputStream = assetManager.open(fullPath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                flagInfoModelList.add(new FlagInfoModel(f,fullPath, bitmap ));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return flagInfoModelList;
    }

    public List<FlagInfoModel> getFlagInfoModelList() {
        return flagInfoModelList;
    }
    public int getFlagsSize() {
        return flagInfoModelList.size();
    }
}
