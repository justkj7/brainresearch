package ucla.erlab.brainresearch;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

public class IntroBreathHoldActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_breath_hold);

        Utils.SettingData data = Utils.getSettingData(this);
        if (data.daycount % 6 != 1) {
            // every 6 days with offset 0 (day 1, 7, 13...)
            Intent intent = new Intent(IntroBreathHoldActivity.this, IntroPVTActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void onBtnCancel(View view) {
        finish();
    }

    public void onBtnStart(View view) {
        Intent intent = new Intent(IntroBreathHoldActivity.this, RestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(Config.REST_TYPE, Config.RestType.BreathHoldWait);
        startActivity(intent);
    }
}
