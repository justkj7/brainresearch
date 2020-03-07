package ucla.erlab.brainresearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");


        populateFields();
    }

    private void populateFields() {
        Utils.SettingData data = Utils.getSettingData(this);

        EditText etSubjectId = (EditText) findViewById(R.id.setting_subject_id);
        etSubjectId.setText("" + data.subjectId);

        RadioGroup rgSex = (RadioGroup) findViewById(R.id.setting_sex);
        rgSex.check(data.sex == 0 ?
                    R.id.setting_sex_male : R.id.setting_sex_famale);

        RadioGroup rgMenstruating = (RadioGroup) findViewById(R.id.setting_menstruating);
        rgMenstruating.check(data.menstruating ?
                    R.id.setting_menstruating_yes : R.id.setting_menstruating_no);

        Spinner sProtocol = (Spinner) findViewById(R.id.setting_protocol);
        sProtocol.setSelection(data.protocol);

        EditText etGroup = (EditText) findViewById(R.id.setting_group);
        etGroup.setText(data.group);

        EditText etDayCount = (EditText) findViewById(R.id.setting_day_count);
        etDayCount.setText("" + data.daycount);

        RadioGroup rgTestingMode = (RadioGroup) findViewById(R.id.setting_testing_mode);
        rgTestingMode.check(data.testingmode ?
                R.id.setting_testing_mode_yes : R.id.setting_testing_mode_no);
    }

    private void saveFields() {
        Utils.SettingData data = new Utils.SettingData();
        EditText etSubjectId = (EditText) findViewById(R.id.setting_subject_id);
        data.subjectId = Integer.parseInt(etSubjectId.getText().toString());

        RadioGroup rgSex = (RadioGroup) findViewById(R.id.setting_sex);
        data.sex = rgSex.indexOfChild(rgSex.findViewById(rgSex.getCheckedRadioButtonId()));

        RadioGroup rgMenstruating = (RadioGroup) findViewById(R.id.setting_menstruating);
        data.menstruating = rgMenstruating.indexOfChild(
                rgMenstruating.findViewById(rgMenstruating.getCheckedRadioButtonId())) == 0; // 0 - yes

        Spinner sProtocol = (Spinner) findViewById(R.id.setting_protocol);
        data.protocol = sProtocol.getSelectedItemPosition();

        EditText etGroup = (EditText) findViewById(R.id.setting_group);
        data.group = etGroup.getText().toString();

        EditText etDayCount = (EditText) findViewById(R.id.setting_day_count);
        data.daycount = Integer.parseInt(etDayCount.getText().toString());

        RadioGroup rgTestingMode = (RadioGroup) findViewById(R.id.setting_testing_mode);
        data.testingmode = rgTestingMode.indexOfChild(
                rgTestingMode.findViewById(rgTestingMode.getCheckedRadioButtonId())) == 0; // 0 - yes

        Utils.setSettingData(this, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_set_default) {
            EditText etSubjectId = (EditText) findViewById(R.id.setting_subject_id);
            etSubjectId.setText("0");

            RadioGroup rgSex = (RadioGroup) findViewById(R.id.setting_sex);
            rgSex.check(R.id.setting_sex_male);

            RadioGroup rgMenstruating = (RadioGroup) findViewById(R.id.setting_menstruating);
            rgMenstruating.check(R.id.setting_menstruating_no);

            Spinner sProtocol = (Spinner) findViewById(R.id.setting_protocol);
            sProtocol.setSelection(0);

            EditText etGroup = (EditText) findViewById(R.id.setting_group);
            etGroup.setText("");

            EditText etDayCount = (EditText) findViewById(R.id.setting_day_count);
            etDayCount.setText("1");

            RadioGroup rgTestingMode = (RadioGroup) findViewById(R.id.setting_testing_mode);
            rgTestingMode.check(R.id.setting_testing_mode_no);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnCancel(View view) {
        finish();
    }

    public void onBtnSave(View view) {
        saveFields();
        finish();
    }
}
