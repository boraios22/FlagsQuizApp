package com.itstep.fragmentandactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlayActivity extends AppCompatActivity {

    TextView tvScore;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        tvScore = findViewById(R.id.tvScore);
        Button btnIncrease = findViewById(R.id.btnIncrease);
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int score = Integer.parseInt(tvScore.getText().toString());
                score++;
                tvScore.setText(String.valueOf(score));
                writeNewUser(firebaseUser.getUid(), firebaseUser.getEmail(), score);
            }
        });
    }

    private void writeNewUser(String userId, String email, int score) {
        User user = new User(email, score);
        mDatabase.child("users").child(userId).setValue(user);
    }

}