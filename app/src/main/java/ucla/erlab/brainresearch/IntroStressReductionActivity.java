package ucla.erlab.brainresearch;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

public class IntroStressReductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_stress_reduction);

        Utils.SettingData data = Utils.getSettingData(this);
        if (!"SR".equals(getResources().getStringArray(R.array.protocol_entries)[data.protocol])
                || data.daycount < 15) {
            Intent intent = new Intent(IntroStressReductionActivity.this, FinishActivity.class);
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
        // Cancel button event
        finish();
    }

    public void onBtnMusicGuide(View view) {
        Intent intent = new Intent(IntroStressReductionActivity.this, StressReductionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void onBtnMusicOnly(View view) {
        Intent intent = new Intent(IntroStressReductionActivity.this, StressReductionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
