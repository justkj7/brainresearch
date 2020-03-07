package ucla.erlab.brainresearch;

public class Config {
    public static final String REST_TYPE = "rest_type";
    public static final String BP_TYPE = "bp_type";

    public static final String EXIT_KEY = "exit";

    public static final String PREF_SETTING = "pref_setting"; // SharedPreference for Setting data
    public static final String PREF_SETTING_SUBJECT_ID = "pref_setting_subject_id";
    public static final String PREF_SETTING_SEX = "pref_setting_sex";
    public static final String PREF_SETTING_MENSTRUATING = "pref_setting_menstruating";
    public static final String PREF_SETTING_PROTOCOL = "pref_setting_protocol";
    public static final String PREF_SETTING_GROUP = "pref_setting_group";
    public static final String PREF_SETTING_DAY_COUNT = "pref_setting_day_count";
    public static final String PREF_SETTING_TESTING_MODE = "pref_setting_testing_mode";

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