package com.itstep.fragmentandactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itstep.fragmentandactivity.core.GameCore;
import com.itstep.fragmentandactivity.core.QuizModel;
import com.itstep.fragmentandactivity.provider.GameDataProvider;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity  {
    private AdView mAdView;
    private RewardedAd rewardedAd;
    TextView tvScore;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;

    Button btnAnswer1, btnAnswer2, btnAnswer3;

    ImageView imgFlag, imgLife1, imgLife2, imgLife3;

    List<ImageView> lifeViews;

    GameCore gameCore = new GameCore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        tvScore = findViewById(R.id.tvScore);
        btnAnswer1 = findViewById(R.id.btnAnswer1);
        btnAnswer2 = findViewById(R.id.btnAnswer2);
        btnAnswer3 = findViewById(R.id.btnAnswer3);

        imgFlag = findViewById(R.id.imgFlag);
        imgLife1 = findViewById(R.id.imgLife1);
        imgLife2 = findViewById(R.id.imgLife2);
        imgLife3 = findViewById(R.id.imgLife3);

        lifeViews = new ArrayList<>();
        lifeViews.add(imgLife3);
        lifeViews.add(imgLife2);
        lifeViews.add(imgLife1);

        loadQuiz();

        initBanner();

        initVideoAd();
    }

    public void button_click(View view){
        Button button = (Button) view;
        String answer = button.getText().toString();
        Handler handler = new Handler();
        if (gameCore.isCorrect(answer)){
            //increase score
            button.setBackgroundColor(getColor(R.color.green));
            int score = Integer.parseInt(tvScore.getText().toString());
            score++;
            tvScore.setText(String.valueOf(score));

            if (score > GameDataProvider.getIns().getHighScore()){
                writeNewUser(
                        firebaseUser.getUid(),
                        firebaseUser.getEmail(),
                        score
                );
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadQuiz();
                }
            }, 1000);

        } else {
            //cut life
            button.setBackgroundColor(getColor(R.color.red));
            gameCore.cutLife();
            refreshLifeView();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (gameCore.outOfLife()){
                        //show game over
                        //Toast.makeText(this, "GameOver !", Toast.LENGTH_SHORT).show();
                        //showGame();
                        if (rewardedAd != null){
                            showConfirmVideo();
                        } else {
                            showGameOver();
                        }
                    } else {
                        loadQuiz();
                    }
                }
            }, 1000);
        }


    }

    private void resetButtonBackgroundColor(){
        btnAnswer1.setBackgroundColor(getColor(R.color.teal_200));
        btnAnswer2.setBackgroundColor(getColor(R.color.teal_200));
        btnAnswer3.setBackgroundColor(getColor(R.color.teal_200));
    }

    private void loadQuiz() {
        QuizModel quizModel = gameCore.generateRandomQuiz();
        imgFlag.setImageBitmap(quizModel.bitmap);
        btnAnswer1.setText(quizModel.answers.get(0));
        btnAnswer2.setText(quizModel.answers.get(1));
        btnAnswer3.setText(quizModel.answers.get(2));

        resetButtonBackgroundColor();
    }
    private void refreshLifeView() {
        for (int i = 0; i< lifeViews.size(); i++){
            ImageView lifeView = lifeViews.get(i);
            if (i < gameCore.getLife()){
                lifeView.setColorFilter(getColor(R.color.red));
            } else {
                lifeView.setColorFilter(getColor(R.color.black));
            }
        }
    }

    private void showGameOver(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Out of life");
        builder.setMessage("Your score : " + tvScore.getText().toString());
        builder.setPositiveButton("Play again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvScore.setText("0");
                gameCore.reset();
                refreshLifeView();
                loadQuiz();
            }
        });
        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.create().show();
    }
    private void showConfirmVideo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Out of life");
        builder.setMessage("Watch a short video to continue your score?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showVideo();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showGameOver();
            }
        });

        builder.create().show();
    }

    private void writeNewUser(String userId, String email, int score) {
        User user = new User(email, score);
        mDatabase.child("users").child(userId).setValue(user);
    }
    private void initBanner(){
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initVideoAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        initVideoListener();
                    }
                });
    }

    private void initVideoListener(){
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                rewardedAd = null;
                initVideoAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                rewardedAd = null;
                initVideoAd();
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
            }
        });
    }
    private void showVideo(){
        if (rewardedAd != null) {
            Activity activityContext = PlayActivity.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();

                    gameCore.reset();
                    refreshLifeView();
                    loadQuiz();
                }
            });
        }
    }
}