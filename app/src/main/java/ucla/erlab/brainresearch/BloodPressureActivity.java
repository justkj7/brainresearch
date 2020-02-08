package ucla.erlab.brainresearch;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

public class BloodPressureActivity extends AppCompatActivity {

    private Config.BPType mBPScreenType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        mBPScreenType = (Config.BPType) getIntent().getSerializableExtra(Config.BP_TYPE);
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
        switch (mBPScreenType) {
            case Setup: {
                Intent intent = new Intent(BloodPressureActivity.this, QuestionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
                break;
            case Rest: {
                Intent intent = new Intent(BloodPressureActivity.this, IntroValsalvaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
                break;
            case Valsalva: {
                Intent intent = new Intent(BloodPressureActivity.this, IntroBreathHoldActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
            default:
                break;
        }
    }
}