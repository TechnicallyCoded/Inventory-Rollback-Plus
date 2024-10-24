package com.nuclyon.technicallycoded.inventoryrollback.nms;

import com.tcoded.lightlibs.bukkitversion.BukkitVersion;
import com.tcoded.lightlibs.bukkitversion.MCVersion;

public enum EnumNmsVersion {
    v1_8_R3(BukkitVersion.v1_8_R3),
    v1_9_R1(BukkitVersion.v1_9_R1),
    v1_9_R2(BukkitVersion.v1_9_R2),
    v1_10_R1(BukkitVersion.v1_10_R1),
    v1_11_R1(BukkitVersion.v1_11_R1),
    v1_12_R1(BukkitVersion.v1_12_R1),
    v1_13_R1(BukkitVersion.v1_13_R1),
    v1_13_R2(BukkitVersion.v1_13_R2),
    v1_14_R1(BukkitVersion.v1_14_R1),
    v1_15_R1(BukkitVersion.v1_15_R1),
    v1_16_R1(BukkitVersion.v1_16_R1),
    v1_16_R2(BukkitVersion.v1_16_R2),
    v1_16_R3(BukkitVersion.v1_16_R3),
    v1_17_R1(BukkitVersion.v1_17_R1),
    v1_18_R1(BukkitVersion.v1_18_R1),
    v1_18_R2(BukkitVersion.v1_18_R2),
    v1_19_R1(BukkitVersion.v1_19_R1),
    v1_19_R2(BukkitVersion.v1_19_R2),
    v1_19_R3(BukkitVersion.v1_19_R3),
    v1_20_R1(BukkitVersion.v1_20_R1),
    v1_20_R2(BukkitVersion.v1_20_R2),
    v1_20_R3(BukkitVersion.v1_20_R3),
    v1_20_R4(BukkitVersion.v1_20_R4),
    v1_21_R1(BukkitVersion.v1_21_R1),
    v1_21_R2(BukkitVersion.v1_21_R2),
    ;

    public static EnumNmsVersion fromMcVersion(String mcVersionStr) {
        MCVersion mcVersion = MCVersion.fromMcVersion(mcVersionStr);
        BukkitVersion bukkitVersion = mcVersion.toBukkitVersion();

        for (EnumNmsVersion nmsVersion : values()) {
            if (nmsVersion.bukkitVersion == bukkitVersion) {
                return nmsVersion;
            }
        }

        return null;
    }

    private final BukkitVersion bukkitVersion;

    EnumNmsVersion(BukkitVersion bukkitVersion) {
        this.bukkitVersion = bukkitVersion;
    }

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
