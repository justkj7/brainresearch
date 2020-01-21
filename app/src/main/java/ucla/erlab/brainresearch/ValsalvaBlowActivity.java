package ucla.erlab.brainresearch;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

public class ValsalvaBlowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valsalva_blow);

        ProgressDialog pd = new ProgressDialog(this, R.style.NewDialog);
        pd.setMessage("Blow");
        pd.show();

        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                goToRest(null);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void goToRest(View view) {
        Intent intent = new Intent(ValsalvaBlowActivity.this, RestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(Config.PREV_ACTIVITY, Config.ActivityType.ValsalvaBlow);
        startActivity(intent);
    }
}
