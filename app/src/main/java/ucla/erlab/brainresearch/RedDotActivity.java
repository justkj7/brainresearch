package ucla.erlab.brainresearch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class RedDotActivity extends AppCompatActivity {
    AsyncTaskRunner async = null;
    int mLayoutWidth = 0;
    int mLayoutHeight = 0;

    private boolean mTaskInit = false;
    private int mTaskCount = 0;
    private Handler mHandler = new Handler();
    private Runnable myTask = new Runnable() {
        public void run() {
            TextView tv = (TextView) findViewById(R.id.tv_reddot);
            async = new AsyncTaskRunner(tv, mLayoutWidth, mLayoutHeight);
            async.execute();
            ++mTaskCount;

            if (mTaskCount == 1) {
                mHandler.removeCallbacks(myTask);
            }
            mHandler.postDelayed(this, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddot);

        final LinearLayout ll = (LinearLayout) findViewById(R.id.layout_reddot);
        ll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mTaskInit) {
                    mTaskInit = true;
                    mLayoutWidth = ll.getWidth();
                    mLayoutHeight = ll.getHeight();
                    mHandler.postDelayed(myTask, 1000);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);

    }

    public void onRedDot(View view) {
        Intent intent = new Intent(RedDotActivity.this, IntroStroopActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {
        TextView mCircle = null;
        int mViewHeight = 0;
        int mViewWidth = 0;

        AsyncTaskRunner(TextView tv, int width, int height) {
            mCircle = tv;
            mViewHeight = height;
            mViewWidth = width;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(Void result) {
            Random rand = new Random();
            int x = rand.nextInt(Integer.MAX_VALUE) % mViewWidth;
            int y = rand.nextInt(Integer.MAX_VALUE) % mViewHeight;
            int margin = 70;
            if (x < margin) x +=margin;
            else if (x > mViewWidth - margin) x -=margin;

            if (y < margin) y +=margin;
            else if (y > mViewHeight - margin) y -=margin;

            mCircle.setX(x);
            mCircle.setY(y);
            mCircle.setVisibility(View.VISIBLE);
        }
    }
}