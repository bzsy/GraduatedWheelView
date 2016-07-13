package com.github.bzsy.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.bzsy.graduatedwheelview.GraduatedWheelView;

public class MainActivity extends AppCompatActivity {
    private GraduatedWheelView graduatedWheelView = null;
    private TextView tvValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graduatedWheelView = (GraduatedWheelView) findViewById(R.id.ruler);
        tvValue = (TextView) findViewById(R.id.tv_value);

        float defaultValue = 0;
        tvValue.setText(String.format("%.1f", defaultValue));

        graduatedWheelView.setMinVal(0);
        graduatedWheelView.setMaxVal(100);
        graduatedWheelView.setCurValue(defaultValue);
        graduatedWheelView.setValueType(GraduatedWheelView.TYPE_DECIMAL);//or GraduatedWheelView.TYPE_INTEGER
//        graduatedWheelView.setDivLineColor();
//        graduatedWheelView.setStrokeColor();
//        graduatedWheelView.setCenterLineColor();
//        graduatedWheelView.setTextColor();
        graduatedWheelView.setOnValueChangedListener(new GraduatedWheelView.OnValueChangedListener() {
            @Override
            public void onChanged(float oldValue, float newValue) {
                tvValue.setText(String.format("%.1f", newValue));
            }
        });
    }
}
