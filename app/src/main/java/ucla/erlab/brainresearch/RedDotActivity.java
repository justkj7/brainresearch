package ucla.erlab.brainresearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import java.util.Random;

public class RedDotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PVCView pvcview = new PVCView(this);
        setContentView(pvcview);
    }

    public class PVCView extends View {
        Paint paint = null;
        public PVCView(Context context) {
            super(context);
            paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Random rand = new Random();

            int x = rand.nextInt(Integer.MAX_VALUE) % getWidth();
            int y = rand.nextInt(Integer.MAX_VALUE) % getHeight();

            int radius = 30;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);

            canvas.drawPaint(paint);
            paint.setColor(Color.RED);

            canvas.drawCircle(x, y, radius, paint);
        }
    }
}
