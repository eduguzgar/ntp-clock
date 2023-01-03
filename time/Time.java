package time;

public class Time {
    public static byte[] timeSecondsToHMS(long time_seconds)
    {
        byte[] time_hms = new byte[3];
        time_hms[0] = (byte)(((time_seconds % 86400) / 3600));
        time_hms[1] = (byte)((time_seconds % 3600) / 60);
        time_hms[2] = (byte)(time_seconds % 60);

        return time_hms;
    }
}
