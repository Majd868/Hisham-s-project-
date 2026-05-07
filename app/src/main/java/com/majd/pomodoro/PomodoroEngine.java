package com.majd.pomodoro;

public class PomodoroEngine {
    public enum Phase {
        FOCUS,
        BREAK,
        COMPLETED
    }

    private final int focusMin;
    private final int breakMin;
    private final int totalBlocks;

    private int currentBlock = 1;
    private Phase phase = Phase.FOCUS;

    public PomodoroEngine(int focusMin, int breakMin, int totalBlocks) {
        this.focusMin = Math.max(1, focusMin);
        this.breakMin = Math.max(1, breakMin);
        this.totalBlocks = Math.max(1, totalBlocks);
    }

    public int getCurrentDurationMillis() {
        return (phase == Phase.FOCUS ? focusMin : breakMin) * 60 * 1000;
    }

    public int getCurrentBlock() {
        return currentBlock;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public int getFocusMin() {
        return focusMin;
    }

    public Phase getPhase() {
        return phase;
    }

    public Phase advance() {
        if (phase == Phase.COMPLETED) {
            return phase;
        }

        if (phase == Phase.FOCUS) {
            phase = Phase.BREAK;
        } else {
            if (currentBlock >= totalBlocks) {
                phase = Phase.COMPLETED;
            } else {
                currentBlock++;
                phase = Phase.FOCUS;
            }
        }
        return phase;
    }
}
