package com.slava.localization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Position {
    float x;
    float y;
    HashMap<String, Float> sig_str;
    public Position(float x, float y, HashMap<String, Float>sig_str) {
        this.x = x;
        this.y = y;
        this.sig_str = sig_str;
    }

    public List<Float> get() {
        return Arrays.asList(x, y);
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void putX(float x) {
        this.x = x;
    }

    public void putY(float y) {
        this.y = y;
    }

    public float getSS(String ss) {
        return sig_str.get(ss);
    }
}
