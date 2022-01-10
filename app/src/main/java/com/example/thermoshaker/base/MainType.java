package com.example.thermoshaker.base;

import java.util.Locale;

public class MainType {



    /* 梯度方向 */
    public enum DirectionType {
        Forward, Reversal,ForwardAndReverse
    }

    /* 供管理员调用的命令 */
    public enum CMD {
        HideBar("service call activity 42 s16 com.android.systemui"),
        ShowBar("am startservice -n com.android.systemui/.SystemUIService"),
        StartApp("am start -n com.example.thermoshaker.MainActivity"), Package("com.example.thermoshaker"),
        Alarm("chmod 666 /dev/alarm"), StopApp("am force-stop com.example.thermoshaker"),
        ClearApp("pm clear com.example.thermoshaker");
        private final String value;

        CMD(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
