package ucla.erlab.brainresearch;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

public class RestActivity extends AppCompatActivity {

    private Config.RestType mRestScreenType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        mRestScreenType = (Config.RestType) getIntent().getSerializableExtra(Config.PREV_ACTIVITY);

        ProgressDialog pd = new ProgressDialog(this, R.style.NewDialog);
        TextView textView = (TextView)findViewById(R.id.rest_guide);
        switch (mRestScreenType) {
            case Setup: {
                textView.setText(R.string.rest_sit);
                pd.setMessage(getResources().getString(R.string.rest_rest));
            }
                break;
            case ValsalvaWait: {
                textView.setText(R.string.rest_valsalva_wait1);
                pd.setMessage(getResources().getString(R.string.rest_wait));
            }
                break;
            case ValsalvaBreath: {
                textView.setText(R.string.rest_valsalva_wait2);
                pd.setMessage(getResources().getString(R.string.rest_wait));
            }
                break;
            case BreathHoldWait: {
                textView.setText(R.string.rest_breath_hold_wait1);
                pd.setMessage(getResources().getString(R.string.rest_wait));
            }
                break;
            case BreathHoldBreath: {
                textView.setText(R.string.rest_breath_hold_wait2);
                pd.setMessage(getResources().getString(R.string.rest_wait));
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
        switch (mRestScreenType) {
            case Setup: {
                Intent intent = new Intent(RestActivity.this, BloodPressureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(Config.PREV_ACTIVITY, Config.BPType.Rest);
                startActivity(intent);
            }
                break;
            case ValsalvaWait: {
                Intent intent = new Intent(RestActivity.this, BlowValsalvaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
                break;
            case ValsalvaBreath: {
                Intent intent = new Intent(RestActivity.this, BloodPressureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(Config.PREV_ACTIVITY, Config.BPType.Valsalva);
                startActivity(intent);
            }
                break;
            case BreathHoldWait: {
                Intent intent = new Intent(RestActivity.this, BlowBreathHoldActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
                break;
            case BreathHoldBreath: {

            }
                break;
            default:
                break;
        }
    }
}
