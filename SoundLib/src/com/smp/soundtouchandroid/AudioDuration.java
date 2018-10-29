package com.smp.soundtouchandroid;

/**
 * Created by dev on 5/7/17.
 */

public interface AudioDuration {
    void getTotalDuration(long totalDuration);
    void getCurrentDuration(long currentDuration);
    void getAudioEndStatus(boolean status);
}
