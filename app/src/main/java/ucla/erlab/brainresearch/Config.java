package ucla.erlab.brainresearch;

public class Config {
    public static final String PREV_ACTIVITY = "prev_activity";

    public enum RestType {
        Setup,
        ValsalvaWait,
        ValsalvaBreath,
        BreathHoldWait,
        BreathHoldBreath
    }

    public enum BPType {
        Setup,
        Rest,
        Valsalva
    }
}