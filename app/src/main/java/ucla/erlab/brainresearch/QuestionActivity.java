package ucla.erlab.brainresearch;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

public class QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
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
        Intent intent = new Intent(QuestionActivity.this, RestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(Config.PREV_ACTIVITY, Config.RestType.Setup);
        startActivity(intent);
    }
}
