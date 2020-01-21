package ucla.erlab.brainresearch;

public class Config {
    public static final String PREV_ACTIVITY = "prev_activity";

    public enum ActivityType {
        PulseOxConn,
        Question,
        IntroValsalva,
        IntroBreathHold,
        BlowValsalva,
        BlowBreathHold,
        Default_Rest,
        Valsalva_Rest
    }
}