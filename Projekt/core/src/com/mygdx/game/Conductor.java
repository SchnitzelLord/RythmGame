package com.mygdx.game;

import com.badlogic.gdx.audio.Music;

public class Conductor {

    int crotchetsPerBar;
    public float bpm;
    public float crochet;
    public float songPosition;
    public float deltaSongPosition;
    public float nextBeatTime;
    public float nextBarTime;
    public float lastBeat;
    public float offset;
    public float addOffset;
    public boolean IsOffsetAdjusted = false;
    public int beatNumber = 0;
    public int barNumber = 0;

    public Conductor(float bpm, float offset) {
        this.bpm = bpm;
        this.offset = offset;
    }

    public void start() {
        lastBeat = 0;
        crochet = 60 / bpm;
    }





}
