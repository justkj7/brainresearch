package ucla.erlab.brainresearch;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class FinishActivity extends AppCompatActivity {
    Utils.SettingData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        data = Utils.getSettingData(this);
        TextView tv_finish_day = (TextView) findViewById(R.id.tv_finish_day);
        tv_finish_day.setText("" + data.daycount);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void onBtnFinish(View view) {
        data.daycount++;
        Utils.setSettingData(this, data);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Config.EXIT_KEY, true);
        startActivity(intent);
    }
}