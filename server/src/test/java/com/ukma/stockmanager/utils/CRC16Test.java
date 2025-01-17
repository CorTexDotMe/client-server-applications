package com.ukma.stockmanager.utils;

import com.ukma.stockmanager.core.utils.CRC16;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CRC16Test {

    @Test
    void getCRC16_1() {
        byte[] data = "123456789qwerty".getBytes();
        byte[] copyOfData = data.clone();
        assertEquals(CRC16.getCRC16(data), CRC16.getCRC16(copyOfData));
    }

    @Test
    void getCRC16_2() {
        byte[] data = "123456789qwerty".getBytes();
        byte[] copyOfData = data.clone();
        assertEquals(CRC16.getCRC16(data), CRC16.getCRC16(copyOfData));
    }

    @Test
    void getCRC16_3() {
        byte[] data = "123456789qwerty".getBytes();
        byte[] copyOfData = data.clone();
        assertEquals(CRC16.getCRC16(data), CRC16.getCRC16(copyOfData));
    }
}