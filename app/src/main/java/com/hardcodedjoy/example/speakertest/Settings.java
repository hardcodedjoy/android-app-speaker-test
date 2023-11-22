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
import android.content.SharedPreferences;

import com.hardcodedjoy.util.ThemeUtil;

public class Settings {

    static private final String THEME_DEFAULT = ThemeUtil.SYSTEM;
    static private final long SAMPLE_RATE_DEFAULT = 48000;

    private long sampleRate;
    private String theme;

    private final SharedPreferences sp;

    public Settings(SharedPreferences sp) {
        this.sp = sp;
        setTheme(sp.getString(Keys.theme, THEME_DEFAULT));
        setSampleRate(sp.getLong(Keys.sampleRate, SAMPLE_RATE_DEFAULT));
    }

    public void setTheme(String theme) { this.theme = theme; }
    public String getTheme() { return theme; }

    public void setSampleRate(long sampleRate) { this.sampleRate = sampleRate; }
    public void setSampleRate(String sampleRate) {
        try {
            this.sampleRate = Long.parseLong(sampleRate);
        } catch (Exception e) {
            this.sampleRate = SAMPLE_RATE_DEFAULT;
        }
    }
    public long getSampleRate() { return sampleRate; }

    @SuppressLint("ApplySharedPref")
    void save() {
        sp.edit()
                .putString(Keys.theme, getTheme())
                .putLong(Keys.sampleRate, getSampleRate())
                .commit();
    }
}