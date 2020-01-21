package ucla.erlab.brainresearch;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

public class RestActivity extends AppCompatActivity {

    private Config.ActivityType mPrevActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        mPrevActivity = (Config.ActivityType) getIntent().getSerializableExtra(Config.PREV_ACTIVITY);

        ProgressDialog pd = new ProgressDialog(this, R.style.NewDialog);
        TextView textView = (TextView)findViewById(R.id.rest_guide);
        switch (mPrevActivity) {
            case Question: {
                textView.setText(R.string.rest_sit);
                pd.setMessage("Rest");
            }
                break;
            case IntroValsalva: {
                textView.setText(R.string.rest_valsalva_wait1);
                pd.setMessage("Wait....");
            }
                break;
            case ValsalvaBlow: {
                textView.setText(R.string.rest_valsalva_wait2);
                pd.setMessage("Wait....");
            }
                break;
            default:
                break;
        }

        pd.show();
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                goToNextActivity(null);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void goToNextActivity(View view) {
        switch (mPrevActivity) {
            case Question: {
                Intent intent = new Intent(RestActivity.this, BloodPressureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(Config.PREV_ACTIVITY, Config.ActivityType.Default_Rest);
                startActivity(intent);
            }
                break;
            case IntroValsalva: {
                Intent intent = new Intent(RestActivity.this, ValsalvaBlowActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
                break;
            case ValsalvaBlow: {
                Intent intent = new Intent(RestActivity.this, BloodPressureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(Config.PREV_ACTIVITY, Config.ActivityType.Valsalva_Rest);
                startActivity(intent);
            }
                break;
            default:
                break;
        }
    }
}
