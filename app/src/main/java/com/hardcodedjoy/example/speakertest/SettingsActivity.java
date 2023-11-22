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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hardcodedjoy.util.GuiUtil;
import com.hardcodedjoy.util.SetGetter;
import com.hardcodedjoy.util.ThemeUtil;

public class SettingsActivity extends Activity {

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new Settings(getSharedPreferences(getPackageName(), Context.MODE_PRIVATE));
        initGUI();
    }

    private void initGUI() {
        // we use our own title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ThemeUtil.set(this, settings.getTheme());
        setContentView(R.layout.activity_settings);

        EditText etSampleRate = findViewById(R.id.et_sample_rate);
        GuiUtil.link(etSampleRate, new SetGetter() {
            @Override
            public void set(String value) { settings.setSampleRate(value); }
            @Override
            public String get() { return "" + settings.getSampleRate(); }
        });

        RadioGroup rgTheme = findViewById(R.id.rg_theme);

        RadioButton rb;
        rb = findViewById(R.id.rb_theme_light);
        rb.setText(ThemeUtil.LIGHT);
        rb = findViewById(R.id.rb_theme_dark);
        rb.setText(ThemeUtil.DARK);
        rb = findViewById(R.id.rb_theme_system);
        rb.setText(ThemeUtil.SYSTEM);

        GuiUtil.link(rgTheme, new SetGetter() {
            @Override
            public void set(String value) {
                boolean mustRecreate = !settings.getTheme().equals(value);
                settings.setTheme(value);
                if(mustRecreate) {
                    settings.save();
                    recreate();
                }
            }
            @Override
            public String get() { return settings.getTheme(); }
        });
    }

    @Override
    public void onBackPressed() {
        settings.save();

        setResult(RESULT_OK);
        super.onBackPressed();
        super.finish();
    }
}