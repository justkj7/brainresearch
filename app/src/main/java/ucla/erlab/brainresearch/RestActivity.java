package ucla.erlab.brainresearch;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

public class RestActivity extends AppCompatActivity {

    private Config.ActivityType mPrevActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        mPrevActivity = (Config.ActivityType) getIntent().getSerializableExtra(Config.PREV_ACTIVITY);

        ProgressDialog pd = new ProgressDialog(this, R.style.NewDialog);
        pd.setMessage("Rest");
        pd.show();

        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                goToBloodPressure(null);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void goToBloodPressure(View view) {
        switch (mPrevActivity) {
            case Question: {
                Intent intent = new Intent(RestActivity.this, BloodPressureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(Config.PREV_ACTIVITY, Config.ActivityType.Default_Rest);
                startActivity(intent);
            }
            break;
            default:
                break;
        }
    }
}
