/*

MIT License

Copyright © 2023 HARDCODED JOY S.R.L. (https://hardcodedjoy.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package com.hardcodedjoy.example.speakertest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hardcodedjoy.noisoid.Noisoid;
import com.hardcodedjoy.util.GuiUtil;
import com.hardcodedjoy.noisoid.SineGenerator;
import com.hardcodedjoy.util.ThemeUtil;

public class MainActivity extends Activity {

    static private final int[] frequencies = { 20, 200, 1000, 2000, 10000, 20000 };

    static private int[][] sourceIds;

    static private final int RQ_CODE_SETTINGS = 1;

    private LinearLayout llMenuOptions;

    static private Noisoid noisoid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Settings settings = new Settings(getSharedPreferences(
                getPackageName(), Context.MODE_PRIVATE));

        ThemeUtil.setResIdThemeLight(R.style.AppThemeLight);
        ThemeUtil.setResIdThemeDark(R.style.AppThemeDark);
        ThemeUtil.set(this, settings.getTheme());

        initGUI();

        int sampleRate = (int) settings.getSampleRate();

        if(noisoid == null) {
            noisoid = new Noisoid(sampleRate, 10);
            noisoid.start();
            sourceIds = new int[3][frequencies.length];
            for(int i=0; i<sourceIds.length; i++) {
                for(int j=0; j<sourceIds[0].length; j++) {
                    sourceIds[i][j] = -1;
                }
            }
        }
    }


    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    private void initGUI() {

        // we use our own title bar in "layout_main"
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        FrameLayout flMenuOptions = findViewById(R.id.fl_menu_options);
        llMenuOptions = (LinearLayout) flMenuOptions.getChildAt(0);

        findViewById(R.id.iv_menu).setOnClickListener(view -> {
            if(llMenuOptions.getVisibility() == View.VISIBLE) {
                llMenuOptions.setVisibility(View.GONE);
            } else if(llMenuOptions.getVisibility() == View.GONE) {
                llMenuOptions.setVisibility(View.VISIBLE);
            }
        });
        llMenuOptions.setVisibility(View.GONE);
        flMenuOptions.setOnTouchListener((v, event) -> {
            if(llMenuOptions.getVisibility() == View.VISIBLE) {
                llMenuOptions.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        GuiUtil.setOnClickListenerToAllButtons(findViewById(R.id.ll_main), view -> {
            llMenuOptions.setVisibility(View.GONE);
            int id = view.getId();

            if(id == R.id.btn_about) {
                startActivity(new Intent(this, AboutActivity.class));
            }
            if(id == R.id.btn_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, RQ_CODE_SETTINGS);
            }
        });

        View view;
        LinearLayout llContent = findViewById(R.id.ll_content);
        view = llContent.getChildAt(0);
        llContent.removeAllViews();
        llContent.addView(view);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout llRow;

        LinearLayout.LayoutParams params;
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1.0f;

        String freqString;

        View.OnTouchListener otl = (v, event) -> {
            int action = event.getAction();
            if(action == MotionEvent.ACTION_DOWN) { onPressed(v); }
            else if(action == MotionEvent.ACTION_UP) { onReleased(v); }
            return false;
        };

        for(int i=0; i<frequencies.length; i++) {
            int freq = frequencies[i];
            llRow = (LinearLayout) inflater.inflate(R.layout.layout_freq_row, null);
            llRow.setLayoutParams(params);

            freqString = freq + " Hz";

            view = llRow.findViewById(R.id.btn_left);
            ((Button) view).setText(freqString);
            view.setTag(i);
            view.setOnTouchListener(otl);

            view = llRow.findViewById(R.id.btn_right);
            ((Button) view).setText(freqString);
            view.setTag(i);
            view.setOnTouchListener(otl);

            view = llRow.findViewById(R.id.btn_both);
            ((Button) view).setText(freqString);
            view.setTag(i);
            view.setOnTouchListener(otl);

            llContent.addView(llRow);
        }

    }

    private void onPressed(View view) {

        int sampleRate = noisoid.getSampleRate();
        int freqId = (int)view.getTag();
        int id = view.getId();
        SineGenerator source = new SineGenerator(sampleRate, frequencies[freqId]);

        if(id == R.id.btn_left) {
            source.setVolume(0.8f, 0.0f);
            sourceIds[0][freqId] = source.getId();
        }
        if(id == R.id.btn_right) {
            source.setVolume(0.0f, 0.8f);
            sourceIds[1][freqId] = source.getId();
        }
        if(id == R.id.btn_both) {
            source.setVolume(0.8f, 0.8f);
            sourceIds[2][freqId]  = source.getId();
        }

        noisoid.addSource(source);
    }

    private void onReleased(View view) {

        int freqId = (int)view.getTag();
        int id = view.getId();

        int sourceId = -1;

        if(id == R.id.btn_left)  { sourceId = sourceIds[0][freqId];  }
        if(id == R.id.btn_right) { sourceId = sourceIds[1][freqId]; }
        if(id == R.id.btn_both)  { sourceId = sourceIds[2][freqId];  }

        noisoid.removeSource(sourceId);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) { return; }
        if(requestCode == RQ_CODE_SETTINGS) { recreate(); }
    }

    @Override
    public void onBackPressed() {
        if(llMenuOptions.getVisibility() == View.VISIBLE) {
            llMenuOptions.setVisibility(View.GONE);
            return;
        }

        if(noisoid != null) {
            noisoid.stop();
            noisoid = null;
        }

        super.onBackPressed();
        super.finish();
    }
}