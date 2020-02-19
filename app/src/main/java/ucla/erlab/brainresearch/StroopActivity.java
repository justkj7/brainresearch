package ucla.erlab.brainresearch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class StroopActivity extends AppCompatActivity {

    private final String[] mColorStrs = { "Green", "Orange", "Blue", "Red", "Yellow", "Purple"};
    private final String[] mColorRGBs = { "#008000", "#FFA500", "#0000FF", "#FF0000", "#FFFF00", "#800080"};

    private String mColor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stroop);
    }

    @Override
    public void onResume() {
        super.onResume();

        Random rand = new Random();
        int choice = rand.nextInt(Integer.MAX_VALUE) % mColorStrs.length;
        mColor = mColorStrs[choice];

        TextView tv_question = (TextView) findViewById(R.id.tv_stroop_question);
        tv_question.setText(mColorStrs[choice]);
        tv_question.setTextColor(Color.parseColor(mColorRGBs[choice]));

        TextView tv_result = (TextView) findViewById(R.id.tv_stroop_result);
        tv_result.setText("");

        enableButtons(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void processAnswer(String answer) {
        TextView tv_result = (TextView) findViewById(R.id.tv_stroop_result);
        if (mColor.equals(answer)) {
            tv_result.setText("O");
            tv_result.setTextColor(Color.parseColor("#008000"));
        } else {
            tv_result.setText("X");
            tv_result.setTextColor(Color.parseColor("#FF0000"));
        }

        enableButtons(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                goNext();
            }
        }, 1500);
    }

    public void enableButtons(boolean is_enable) {
        Button bt_green = (Button) findViewById(R.id.bt_stroop_green);
        Button bt_orange = (Button) findViewById(R.id.bt_stroop_orange);
        Button bt_blue = (Button) findViewById(R.id.bt_stroop_blue);
        Button bt_red = (Button) findViewById(R.id.bt_stroop_red);
        Button bt_yellow = (Button) findViewById(R.id.bt_stroop_yellow);
        Button bt_purple = (Button) findViewById(R.id.bt_stroop_purple);
        bt_green.setEnabled(is_enable);
        bt_orange.setEnabled(is_enable);
        bt_blue.setEnabled(is_enable);
        bt_red.setEnabled(is_enable);
        bt_yellow.setEnabled(is_enable);
        bt_purple.setEnabled(is_enable);
    }

    public void goNext() {
        Intent intent = new Intent(StroopActivity.this, BloodPressureActivity.class);
        intent.putExtra(Config.BP_TYPE, Config.BPType.PreStressReduction);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void onBtnGreen(View view) {
        processAnswer("Green");
    }

    public void onBtnOrange(View view) {
        processAnswer("Orange");
    }

    public void onBtnBlue(View view) {
        processAnswer("Blue");
    }

    public void onBtnRed(View view) {
        processAnswer("Red");
    }

    public void onBtnYellow(View view) {
        processAnswer("Yellow");
    }

    public void onBtnPurple(View view) {
        processAnswer("Purple");
    }
}
