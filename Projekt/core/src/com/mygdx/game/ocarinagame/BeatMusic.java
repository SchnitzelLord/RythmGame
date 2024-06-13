package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;

public class BeatMusic implements Disposable {
    private final Music song;
    private float bpm;
    private final int totalBeatCount;
    private final float beatStart;
    private final float beatEnd;
    private final float songLength;

    // Constructor

    public BeatMusic(Music song, float bpm, int totalBeatCount, float beatStart, float beatEnd, float songLength) {
        this.song = song;
        this.bpm = bpm;
        this.totalBeatCount = totalBeatCount;
        this.beatStart = beatStart;
        this.beatEnd = beatEnd;
        this.songLength = songLength;
    }

    // Calculates totalBeatCount by dividing beat time with time per beat
    public BeatMusic(Music song, float bpm, float beatStart, float beatEnd, float songLength) {
        this(song, bpm, (int) ((beatEnd - beatStart) / (60 / bpm)), beatStart, beatEnd, songLength);
    }

    // Getter

    public Music getSong() {
        return song;
    }

    public float getBPM() {
        return bpm;
    }

    public int getTotalBeatCount() {
        return totalBeatCount;
    }

    public float getBeatStart() {
        return beatStart;
    }

    public float getBeatEnd() {
        return beatEnd;
    }

    public float getSongLength() {
        return songLength;
    }

    // Public function methods

    public void play() {
        song.play();
    }

    public void setVolume(float volume) {
        song.setVolume(volume);
    }

    public void pause() {
        song.pause();
    }

    public float getPosition() {
        return song.getPosition();
    }

    public boolean isPlaying() {
        return song.isPlaying();
    }

    public void playMusicAfterSec(float seconds) {
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                song.play();
            }
        }, seconds);
    }

    // Setter

    public void setBPM(float bpm) {
        this.bpm = bpm;
    }

    // Overrides

    @Override
    public void dispose() {
        song.dispose();
    }
}
