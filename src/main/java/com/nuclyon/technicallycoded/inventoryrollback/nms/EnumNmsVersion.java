package com.nuclyon.technicallycoded.inventoryrollback.nms;

public enum EnumNmsVersion {
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1;

    public boolean isAtLeast(EnumNmsVersion version) {
        return this.ordinal() >= version.ordinal();
    }

    public boolean isNoHigherThan(EnumNmsVersion version) {
        return this.ordinal() <= version.ordinal();
    }

    public boolean isWithin(EnumNmsVersion versionLow, EnumNmsVersion versionHigh) {
        return this.isAtLeast(versionLow) && this.isNoHigherThan(versionHigh);
    }
}
