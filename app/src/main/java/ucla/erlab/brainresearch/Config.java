package ucla.erlab.brainresearch;

public class Config {
    public static final String REST_TYPE = "rest_type";
    public static final String BP_TYPE = "bp_type";

    public static final String EXIT_KEY = "exit";

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
        Valsalva,
        PreStressReduction,
        PostStressReduction
    }
}