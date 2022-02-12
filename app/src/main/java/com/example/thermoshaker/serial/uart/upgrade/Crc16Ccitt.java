package com.example.thermoshaker.serial.uart.upgrade;

public class Crc16Ccitt {

    int poly = 0x1021;
    int[] table = new int[256];
    int initialValue = 0;

    public enum InitialCrcValue {
        Zeros(0), NonZero1(0xffff), NonZero2(0x1D0F);

        private final int value;

        InitialCrcValue(int value) {
            this.value = value;

        }

        public int getValue() {
            return value;
        }
    }

    public byte[] ComputeChecksumBytes(byte[] pcrc) {
        short crc16 = (short) this.initialValue;
        short item = 0;
        short crc_h = 0;

        for (int i = 0; i < pcrc.length; i++) {

            crc_h = (short) (crc16 >> 8);
            item = (short) (crc_h ^ (0xff & pcrc[i]));

            if (item < 0)
                item += 256;
            crc16 = (short) (crc16 << 8 ^ table[item]);

        }

        return new byte[] { (byte) (crc16 >> 8), (byte) crc16 };
    }

    // 初始化
    public Crc16Ccitt(InitialCrcValue initialValue) {
        this.initialValue = initialValue.getValue();

        short temp, a;
        for (int i = 0; i < table.length; i++) {
            temp = 0;
            a = (short) (i << 8);
            for (int j = 0; j < 8; j++) {
                if (((temp ^ a) & 0x8000) != 0) {
                    temp = (short) ((temp << 1) ^ poly);
                } else {
                    temp <<= 1;
                }
                a <<= 1;
            }
            table[i] = temp & 0xffff;
        }
    }
}

