package ucla.erlab.brainresearch;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

public class BloodPressureActivity extends AppCompatActivity {

    private Config.ActivityType mPrevActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        mPrevActivity = (Config.ActivityType) getIntent().getSerializableExtra(Config.PREV_ACTIVITY);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void onBtnCancel(View view) {
        finish();
    }

    public void onBtnNext(View view) {
        switch (mPrevActivity) {
            case PulseOxConn: {
                Intent intent = new Intent(BloodPressureActivity.this, QuestionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
                break;
            default:
                break;
        }
    }
}