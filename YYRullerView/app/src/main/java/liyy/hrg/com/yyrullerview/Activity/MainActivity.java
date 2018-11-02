package liyy.hrg.com.yyrullerview.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import liyy.hrg.com.yyrullerview.R;
import liyy.hrg.com.yyrullerview.RullerView.HorizontalScaleScrollView;
import liyy.hrg.com.yyrullerview.RullerView.TimeRange;
//import liyy.hrg.com.yyrullerview.RullerView.VerticalScaleScrollView;

public class MainActivity extends AppCompatActivity {
    EditText mTvHorizontalScale;
//    TextView mTvVerticalScale;
    HorizontalScaleScrollView scaleScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvHorizontalScale = (EditText) findViewById(R.id.horizontalScaleValue);

        scaleScrollView = (HorizontalScaleScrollView) findViewById(R.id.horizontalScale);
        scaleScrollView.setOnScrollListener(new HorizontalScaleScrollView.OnScrollListener() {
            @Override
            public void onScaleScroll(double scale) {
                mTvHorizontalScale.setText("" + scale);
            }

            @Override
            public void touchBegin() {

            }

            @Override
            public void touchEnd() {

            }
        });

        List<TimeRange> ranges = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            TimeRange r = new TimeRange();
            r.start = (i * 10) * 60;
            r.duration = 5 * 60;

            ranges.add(r);
        }

        scaleScrollView.setRanges(ranges);

        findViewById(R.id.horizontalScaleValueBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valStr = mTvHorizontalScale.getText().toString().trim();
                scaleScrollView.setCurScale(Integer.parseInt(valStr));
            }
        });

//        mTvVerticalScale = (TextView) findViewById(R.id.verticalScaleValue);
//
//        VerticalScaleScrollView vScaleScrollView = (VerticalScaleScrollView) findViewById(R.id.verticalScale);
//        vScaleScrollView.setOnScrollListener(new HorizontalScaleScrollView.OnScrollListener() {
//            @Override
//            public void onScaleScroll(int scale) {
//                mTvVerticalScale.setText("" + scale);
//            }
//        });
    }
}
