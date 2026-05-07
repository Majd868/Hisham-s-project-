package com.majd.pomodoro;

import org.junit.Assert;
import org.junit.Test;

public class PomodoroEngineTest {
    @Test
    public void sequence_advances_to_completed() {
        PomodoroEngine engine = new PomodoroEngine(25, 5, 2);

        Assert.assertEquals(PomodoroEngine.Phase.FOCUS, engine.getPhase());
        engine.advance();
        Assert.assertEquals(PomodoroEngine.Phase.BREAK, engine.getPhase());
        engine.advance();
        Assert.assertEquals(PomodoroEngine.Phase.FOCUS, engine.getPhase());
        engine.advance();
        Assert.assertEquals(PomodoroEngine.Phase.BREAK, engine.getPhase());
        engine.advance();
        Assert.assertEquals(PomodoroEngine.Phase.COMPLETED, engine.getPhase());
    }
}
