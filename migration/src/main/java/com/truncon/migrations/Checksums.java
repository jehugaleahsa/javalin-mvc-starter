package com.truncon.migrations;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

public final class Checksums {
    private Checksums() {
    }

    public static long getChecksum(String content) {
        Checksum checksum = new CRC32C();
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        checksum.update(data, 0, data.length);
        return checksum.getValue();
    }
}
