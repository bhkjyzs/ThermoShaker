package com.example.thermoshaker.base;

import java.util.Locale;

public class MainType {




    /* 运行的三个槽 */
    public enum RunningEnum {
        RunningA("RunFile/RA.TD", "A", (byte) 0xda), RunningB("RunFile/RB.TD", "B", (byte) 0x02),
        RunningC("RunFile/RC.TD", "C", (byte) 0x03);

        private final String value;
        private final String name;
        private final byte address;

        RunningEnum(String value, String name, byte address) {
            this.value = value;
            this.name = name;
            this.address = address;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public byte getAddress() {
            return address;
        }
    }



    /* 串口来源 */
    public enum UartSource {
        uartUP("/dev/ttyS1"),
        //        uartUP("/dev/ttyS1"),
        uartDown("/dev/ttyS2");
        private final String value;

        UartSource(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
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
