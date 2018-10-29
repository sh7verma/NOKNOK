package com.smp.soundtouchandroid;

import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public abstract class SoundStreamRunnable implements Runnable, AudioFormatChangedListener {
    protected static final int DEFAULT_BYTES_PER_SAMPLE = 2;
    private static final long NOT_SET = Long.MIN_VALUE;
    private static final long MAX_OUTPUT_BUFFER_SIZE = 1024;

    protected Object pauseLock;
    protected Object sinkLock;
    protected Object decodeLock;

    protected volatile long bytesWritten;

    protected volatile SoundTouch soundTouch;
    protected volatile AudioDecoder decoder;

    protected Handler handler;
    protected volatile boolean finished;
    protected volatile int channels;
    protected volatile int samplingRate;
    private String fileName;
    private boolean bypassSoundTouch;
    private volatile long loopStart = NOT_SET;
    private volatile long loopEnd = NOT_SET;
    private volatile AudioSink audioSink;
    private volatile OnProgressChangedListener progressListener;
    private volatile boolean paused;
    AudioDuration audioDuration;

    public SoundStreamRunnable(int id, String fileName, float tempo,
                               float pitchSemi, AudioDuration audioDuration) throws IOException {
        this.audioDuration = audioDuration;
        initDecoder(fileName);
        initSoundTouch(id, tempo, pitchSemi);
        audioSink = initAudioSink();
        this.fileName = fileName;

        handler = new Handler();

        pauseLock = new Object();
        sinkLock = new Object();
        decodeLock = new Object();

        paused = true;
        finished = false;
    }

//    public SoundStreamRunnable(int id, File file, float tempo,
//                               float pitchSemi, AudioDuration audioDuration) throws IOException {
//        this(id, file.getAbsolutePath(), tempo, pitchSemi, audioDuration);
//    }

    protected abstract AudioSink initAudioSink() throws IOException;

    @Override
    public void onAudioFormatChanged(int samplingRate, int channels) throws IOException {
        if (samplingRate != this.samplingRate || channels != this.channels) {
            this.channels = channels;
            this.samplingRate = samplingRate;
            soundTouch.clearBuffer();
            int id = soundTouch.getTrackId();
            initSoundTouch(id, soundTouch.getTempo(), soundTouch.getPitchSemi());
            audioSink.abort();
            audioSink = initAudioSink();
            start();
        }
    }

    private void initSoundTouch(int id, float tempo, float pitchSemi) {
        soundTouch = new SoundTouch(id, channels, samplingRate,
                DEFAULT_BYTES_PER_SAMPLE, tempo, pitchSemi);
    }

    public long getBytesWritten() {
        return bytesWritten;
    }

    public int getChannels() {
        return soundTouch.getChannels();
    }

    public long getDuration() {
        if (decoder != null)
            return decoder.getDuration();
        else
            return NOT_SET;
    }

    public String getFileName() {
        return fileName;
    }

    public long getPlayedDuration() {
        synchronized (decodeLock) {
            if (decoder != null)
                return decoder.getPlayedDuration();
            else
                return NOT_SET;
        }
    }

    public float getPitchSemi() {
        return soundTouch.getPitchSemi();
    }

    public void setPitchSemi(float pitchSemi) {
        soundTouch.setPitchSemi(pitchSemi);
    }

    public long getPlaybackLimit() {
        return loopEnd;
    }

    public int getSamplingRate() {
        return soundTouch.getSamplingRate();
    }

    public long getSoundTouchBufferSize() {
        return soundTouch.getOutputBufferSize();
    }

    protected int getSoundTouchTrackId() {
        return soundTouch.getTrackId();
    }

    public float getTempo() {
        return soundTouch.getTempo();
    }

    public void setTempo(float tempo) {
        soundTouch.setTempo(tempo);
    }

    public float getRate() {
        return soundTouch.getRate();
    }

    public void setRate(float rate) {
        soundTouch.setRate(rate);
    }

    public long getLoopEnd() {
        return loopEnd;
    }

    public void setLoopEnd(long loopEnd) {
        long pd = decoder.getPlayedDuration();
        if (loopStart != NOT_SET && pd <= loopStart)
            throw new SoundStreamRuntimeException(
                    "Invalid Loop Time, loop start must be < loop end");
        this.loopEnd = loopEnd;
    }

    public long getLoopStart() {
        return loopStart;
    }

    public void setLoopStart(long loopStart) {
        long pd = decoder.getPlayedDuration();
        if (loopEnd != NOT_SET && pd >= loopEnd)
            throw new SoundStreamRuntimeException(
                    "Invalid Loop Time, loop start must be < loop end");
        this.loopStart = loopStart;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isLooping() {
        return loopStart != NOT_SET && loopEnd != NOT_SET;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setBypassSoundTouch(boolean bypassSoundTouch) {
        this.bypassSoundTouch = bypassSoundTouch;
    }

    public void setOnProgressChangedListener(
            OnProgressChangedListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setTempoChange(float tempoChange) {
        soundTouch.setTempoChange(tempoChange);
    }

    public void setRateChange(float rate) {
        soundTouch.setRate(rate);
    }

    private void initDecoder(String fileName) throws IOException {
        if (Build.VERSION.SDK_INT >= 16) {
            decoder = new MediaCodecAudioDecoder(fileName, this, audioDuration);
        } else {
            throw new SoundStreamRuntimeException(
                    "Only API level >= 16 supported.");
        }

        channels = decoder.getChannels();
        samplingRate = decoder.getSamplingRate();
    }

    @Override
    public void run() {
        //does it do anything?
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        try {
            while (!finished) {
                processFile();
                paused = true;
                if (progressListener != null && !finished) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressListener.onTrackEnd(soundTouch.getTrackId());
                        }
                    });
                }
                synchronized (decodeLock) {
                    decoder.resetEOS();
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            if (progressListener != null)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.onExceptionThrown(Log.getStackTraceString(e));
                    }
                });
        } catch (final SoundStreamDecoderException e) {
            if (progressListener != null)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.onExceptionThrown(Log.getStackTraceString(e));
                    }
                });
        } finally {
            finished = true;
            soundTouch.clearBuffer();

            synchronized (sinkLock) {
                try {
                    audioSink.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                audioSink = null;
            }
            synchronized (decodeLock) {
                decoder.close();
                decoder = null;
            }
        }
    }

    public void clearLoopPoints() {
        loopStart = NOT_SET;
        loopEnd = NOT_SET;
    }

    protected abstract void onStart();

    protected abstract void onPause();

    protected abstract void onStop();

    protected void seekTo(long timeInUs) {
    }

    public void start() {
        synchronized (pauseLock) {
            onStart();
            paused = false;
            finished = false;
            pauseLock.notifyAll();
        }
    }

    public void pause() {
        synchronized (pauseLock) {
            onPause();
            paused = true;
        }
    }

    public void stop() {
        if (paused) {
            synchronized (pauseLock) {
                onStop();
                paused = false;
                pauseLock.notifyAll();
            }
        }
        finished = true;
    }

    private void pauseWait() {
        synchronized (pauseLock) {
            while (paused) {
                try {
                    pauseLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private void processFile() throws IOException, SoundStreamDecoderException {
        int bytesReceived = 0;
        do {
            pauseWait();
            if (finished)
                break;

            if (isLooping() && decoder.getPlayedDuration() >= loopEnd) {
            //    Log.d("DECODE", "TEST TIME: " + String.valueOf(decoder.getTestTime()));
                Log.d("DECODE", "PLAYED: " +  String.valueOf(decoder.getPlayedDuration()));
                seekTo(loopStart);
            }

            if (soundTouch.getOutputBufferSize() <= MAX_OUTPUT_BUFFER_SIZE) {
                boolean newBytes;
                synchronized (decodeLock) {
                    try {
                        newBytes = decoder.decodeChunk();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        throw new SoundStreamDecoderException(
                                "Buggy google decoder");
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        throw new SoundStreamDecoderException(
                                "Buggy google decoder");
                    }
                }
                if (newBytes) {
                    sendProgressUpdate();
                    processChunk(decoder.getLastChunk(), true);
                }
            } else {
                // avoiding an extra allocation.
                processChunk(decoder.getLastChunk(), false);
            }
        }
        while (!decoder.sawOutputEOS() && getPlayedDuration() < getDuration());

        soundTouch.finish();
        if (!bypassSoundTouch) {
            do {
                if (finished)
                    break;
                bytesReceived = processChunk(null, false);
            }
            while (bytesReceived > 0);
        }
    }

    private void sendProgressUpdate() {
        if (progressListener != null) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    if (!finished) {
                        long pd = decoder.getPlayedDuration();
                        long d = decoder.getDuration();
                        double cp = pd == 0 ? 0 : (double) pd / (double) d;
                        progressListener.onProgressChanged(
                                soundTouch.getTrackId(), cp, pd);
                    }
                }
            });
        }
    }

    private int processChunk(final byte[] input, boolean putBytes)
            throws IOException {
        int bytesReceived = 0;

        if (input != null) {
            if (bypassSoundTouch) {
                bytesReceived = input.length;
            } else {
                if (putBytes)
                    soundTouch.putBytes(input);
                // Log.d("bytes", String.valueOf(input.length));
                bytesReceived = soundTouch.getBytes(input);
            }
            if (bytesReceived > 0) {
                synchronized (sinkLock) {
                    bytesWritten += audioSink.write(input, 0, bytesReceived);
                }
            }
        }

        Log.d("MUSICPOS", "bytesreceibed: " + bytesReceived);
        return bytesReceived;
    }
}
