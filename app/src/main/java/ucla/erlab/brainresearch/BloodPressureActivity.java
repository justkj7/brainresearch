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
        Intent intent = null;
        switch (mBPScreenType) {
            case Setup:
                intent = new Intent(BloodPressureActivity.this, QuestionActivity.class);
                break;
            case Rest:
                intent = new Intent(BloodPressureActivity.this, IntroValsalvaActivity.class);
                break;
            case Valsalva:
                intent = new Intent(BloodPressureActivity.this, IntroBreathHoldActivity.class);
                break;
            case PreStressReduction:
                intent = new Intent(BloodPressureActivity.this, StressReductionActivity.class);
                break;
            case PostStressReduction:
                intent = new Intent(BloodPressureActivity.this, FinishActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }
}