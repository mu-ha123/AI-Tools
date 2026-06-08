package com.ai.toolbox.common.util;

import java.time.Duration;

public final class TimeFormatUtils {

    private TimeFormatUtils() {
    }

    public static String formatMinutes(long minutes) {
        long hours = minutes / 60;
        long remainMinutes = minutes % 60;
        if (hours == 0) {
            return remainMinutes + "分钟";
        }
        if (remainMinutes == 0) {
            return hours + "小时";
        }
        return hours + "小时" + remainMinutes + "分钟";
    }

    public static long toMinutes(Duration duration) {
        return duration.toMinutes();
    }
}
