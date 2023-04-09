package com.nuclyon.technicallycoded.inventoryrollback.util;

import com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

public class TimeZoneUtil {

    private ImmutableMap<String, String> shortCodeUTCOffsets;
    private ImmutableMap<String, String> shortCodesNames;

    public TimeZoneUtil() {
        this.loadDefaultData();
    }

    /**
     * Get adjusted date based on the time zone provided
     * @param timeZone Time zone ID
     * @return Date instant with time offset
     */
    public Date getTimeAtTimeZone(String timeZone) throws IllegalArgumentException, NullPointerException {
        return this.getDateFromMillisOffset(this.getMillisOffsetAtTimeZone(timeZone));
    }

    /**
     * Get adjusted date based on the milliseconds offset provided
     * @param offset Milliseconds offset
     * @return Date instant with time offset
     */
    public Date getDateFromMillisOffset(long offset) throws IllegalArgumentException, NullPointerException {
        return Date.from(Instant.now().plusMillis(offset));
    }

    /**
     * Get milliseconds offset from the time zone provided
     * @param timeZone Time zone ID
     * @return Date instant with time offset
     */
    public long getMillisOffsetAtTimeZone(String timeZone) throws IllegalArgumentException, NullPointerException {
        if (timeZone.equals("UTC") || timeZone.equals("GMT")) {
            return 0L;
        } else {
            char signChar = timeZone.length() > 3 ? timeZone.charAt(3) : ' ';

            // Allow UTC offsets
            if (timeZone.length() > 3 && timeZone.startsWith("UTC")) {
                timeZone = "GMT" + timeZone.substring(3);
            }

            String utcOffsetFormat;
            if (timeZone.length() > 4 &&
                    timeZone.startsWith("GMT") &&
                    (signChar == '+' || signChar == '-')) {
                // Is of UTC offset format, parse millis offset directly
                utcOffsetFormat = timeZone;
            } else {
                // Is other type, attempt to retrieve from loaded data
                utcOffsetFormat = this.getUTCOffsetAtTimeZone(timeZone);
            }

            return this.getMillisOffsetFromUTCFormat(utcOffsetFormat);
        }
    }

    /**
     * Get the milliseconds off of UTC time using the UTC offset format
     * @param utcFormat Time zone in UTC format: UTC+00:00
     * @return Time offset from UTC in milliseconds
     * @throws IllegalArgumentException, NullPointerException
     */
    public long getMillisOffsetFromUTCFormat(String utcFormat) throws IllegalArgumentException, NullPointerException {
        if (utcFormat == null) {
            throw new NullPointerException("UTC format provided is null!");
        }

        String[] mainParts;
        if (utcFormat.contains("-")) mainParts = utcFormat.split("-");
        else mainParts = utcFormat.split("\\+");

        if (!mainParts[0].equals("GMT")) {
            throw new IllegalArgumentException("UTC format provided (" + utcFormat + ") does not follow the GMT+00:00 format");
        }

        boolean negative = utcFormat.charAt(mainParts[0].length()) == '-';

        String[] timeParts = mainParts[1].split(":");
        String hourOffsetStr = timeParts[0];
        String minuteOffsetStr = timeParts.length > 1 ? timeParts[1] : "00";

        int hourOffset;
        int minuteOffset;

        hourOffset = Integer.parseInt(hourOffsetStr);
        minuteOffset = Integer.parseInt(minuteOffsetStr);

        long millisOffset = 0L;
        millisOffset += (long) hourOffset * 60L * 60L * 1000L;
        millisOffset += (long) minuteOffset * 60L * 1000L;

        if (negative) millisOffset *= -1L;

        return millisOffset;
    }

    /**
     * Gets the UTC+00:00 format from any shortcode
     * @param shortCode The 3-4 letter short code for the timezone
     * @return String using the UTC+00:00 format
     */
    public String getUTCOffsetAtTimeZone(String shortCode) {
        return this.shortCodeUTCOffsets.getOrDefault(shortCode, null);
    }

    /**
     * Gets the UTC+00:00 format from any shortcode
     * @param shortCode The 3-4 letter short code for the timezone
     * @return String using the UTC+00:00 format
     */
    public String getTimeZoneFullName(String shortCode) {
        return this.shortCodesNames.getOrDefault(shortCode, null);
    }

    // INIT DATA

    public void loadDefaultData() {
        HashMap<String, String> offsetsMapBuilder = new HashMap<>();
        HashMap<String, String> shortCodeNamesBuilder = new HashMap<>();

        offsetsMapBuilder.put("ACDT", "GMT+10:30");
        offsetsMapBuilder.put("ACST", "GMT+09:30");
        offsetsMapBuilder.put("ACT", "GMT+10");
        offsetsMapBuilder.put("ACWST", "GMT+08:45");
        offsetsMapBuilder.put("ADT", "GMT-03");
        offsetsMapBuilder.put("AEDT", "GMT+11");
        offsetsMapBuilder.put("AEST", "GMT+10");
        offsetsMapBuilder.put("AET", "GMT+10");
        offsetsMapBuilder.put("AFT", "GMT+04:30");
        offsetsMapBuilder.put("AKDT", "GMT-08");
        offsetsMapBuilder.put("AKST", "GMT-09");
        offsetsMapBuilder.put("ALMT", "GMT+06");
        offsetsMapBuilder.put("AMST", "GMT-03");
        offsetsMapBuilder.put("AMT", "GMT+04");
        offsetsMapBuilder.put("ANAT", "GMT+12");
        offsetsMapBuilder.put("AQTT", "GMT+05");
        offsetsMapBuilder.put("ART", "GMT-03");
        offsetsMapBuilder.put("AST", "GMT-04");
        offsetsMapBuilder.put("AWST", "GMT+08");
        offsetsMapBuilder.put("AZOST", "GMT+00");
        offsetsMapBuilder.put("AZOT", "GMT-01");
        offsetsMapBuilder.put("AZT", "GMT+04");
        offsetsMapBuilder.put("BNT", "GMT+08");
        offsetsMapBuilder.put("BIOT", "GMT+06");
        offsetsMapBuilder.put("BIT", "GMT-12");
        offsetsMapBuilder.put("BOT", "GMT-04");
        offsetsMapBuilder.put("BRST", "GMT-02");
        offsetsMapBuilder.put("BRT", "GMT-03");
        offsetsMapBuilder.put("BST", "GMT+01");
        offsetsMapBuilder.put("BTT", "GMT+06");
        offsetsMapBuilder.put("CAT", "GMT+02");
        offsetsMapBuilder.put("CCT", "GMT+06:30");
        offsetsMapBuilder.put("CDT", "GMT-05");
        offsetsMapBuilder.put("CEST", "GMT+02");
        offsetsMapBuilder.put("CET", "GMT+01");
        offsetsMapBuilder.put("CHADT", "GMT+13:45");
        offsetsMapBuilder.put("CHAST", "GMT+12:45");
        offsetsMapBuilder.put("CHOT", "GMT+08");
        offsetsMapBuilder.put("CHOST", "GMT+09");
        offsetsMapBuilder.put("CHST", "GMT+10");
        offsetsMapBuilder.put("CHUT", "GMT+10");
        offsetsMapBuilder.put("CIST", "GMT-08");
        offsetsMapBuilder.put("CKT", "GMT-10");
        offsetsMapBuilder.put("CLST", "GMT-03");
        offsetsMapBuilder.put("CLT", "GMT-04");
        offsetsMapBuilder.put("COST", "GMT-04");
        offsetsMapBuilder.put("COT", "GMT-05");
        offsetsMapBuilder.put("CST", "GMT-06");
        offsetsMapBuilder.put("CT", "GMT-06");
        offsetsMapBuilder.put("CVT", "GMT-01");
        offsetsMapBuilder.put("CWST", "GMT+08:45");
        offsetsMapBuilder.put("CXT", "GMT+07");
        offsetsMapBuilder.put("DAVT", "GMT+07");
        offsetsMapBuilder.put("DDUT", "GMT+10");
        offsetsMapBuilder.put("DFT", "GMT+01");
        offsetsMapBuilder.put("EASST", "GMT-05");
        offsetsMapBuilder.put("EAST", "GMT-06");
        offsetsMapBuilder.put("EAT", "GMT+03");
        offsetsMapBuilder.put("ECT", "GMT-05");
        offsetsMapBuilder.put("EDT", "GMT-04");
        offsetsMapBuilder.put("EEST", "GMT+03");
        offsetsMapBuilder.put("EET", "GMT+02");
        offsetsMapBuilder.put("EGST", "GMT+00");
        offsetsMapBuilder.put("EGT", "GMT-01");
        offsetsMapBuilder.put("EST", "GMT-05");
        offsetsMapBuilder.put("ET", "GMT-05");
        offsetsMapBuilder.put("FET", "GMT+03");
        offsetsMapBuilder.put("FJT", "GMT+12");
        offsetsMapBuilder.put("FKST", "GMT-03");
        offsetsMapBuilder.put("FKT", "GMT-04");
        offsetsMapBuilder.put("FNT", "GMT-02");
        offsetsMapBuilder.put("GALT", "GMT-06");
        offsetsMapBuilder.put("GAMT", "GMT-09");
        offsetsMapBuilder.put("GET", "GMT+04");
        offsetsMapBuilder.put("GFT", "GMT-03");
        offsetsMapBuilder.put("GILT", "GMT+12");
        offsetsMapBuilder.put("GIT", "GMT-09");
        offsetsMapBuilder.put("GMT", "GMT+00");
        offsetsMapBuilder.put("GST", "GMT+04");
        offsetsMapBuilder.put("GYT", "GMT-04");
        offsetsMapBuilder.put("HDT", "GMT-09");
        offsetsMapBuilder.put("HAEC", "GMT+02");
        offsetsMapBuilder.put("HST", "GMT-10");
        offsetsMapBuilder.put("HKT", "GMT+08");
        offsetsMapBuilder.put("HMT", "GMT+05");
        offsetsMapBuilder.put("HOVST", "GMT+08");
        offsetsMapBuilder.put("HOVT", "GMT+07");
        offsetsMapBuilder.put("ICT", "GMT+07");
        offsetsMapBuilder.put("IDLW", "GMT-12");
        offsetsMapBuilder.put("IDT", "GMT+03");
        offsetsMapBuilder.put("IOT", "GMT+03");
        offsetsMapBuilder.put("IRDT", "GMT+04:30");
        offsetsMapBuilder.put("IRKT", "GMT+08");
        offsetsMapBuilder.put("IRST", "GMT+03:30");
        offsetsMapBuilder.put("IST", "GMT+05:30");
        offsetsMapBuilder.put("JST", "GMT+09");
        offsetsMapBuilder.put("KALT", "GMT+02");
        offsetsMapBuilder.put("KGT", "GMT+06");
        offsetsMapBuilder.put("KOST", "GMT+11");
        offsetsMapBuilder.put("KRAT", "GMT+07");
        offsetsMapBuilder.put("KST", "GMT+09");
        offsetsMapBuilder.put("LHST", "GMT+10:30");
        offsetsMapBuilder.put("LINT", "GMT+14");
        offsetsMapBuilder.put("MAGT", "GMT+12");
        offsetsMapBuilder.put("MART", "GMT-09:30");
        offsetsMapBuilder.put("MAWT", "GMT+05");
        offsetsMapBuilder.put("MDT", "GMT-06");
        offsetsMapBuilder.put("MET", "GMT+01");
        offsetsMapBuilder.put("MEST", "GMT+02");
        offsetsMapBuilder.put("MHT", "GMT+12");
        offsetsMapBuilder.put("MIST", "GMT+11");
        offsetsMapBuilder.put("MIT", "GMT-09:30");
        offsetsMapBuilder.put("MMT", "GMT+06:30");
        offsetsMapBuilder.put("MSK", "GMT+03");
        offsetsMapBuilder.put("MST", "GMT-07");
        offsetsMapBuilder.put("MUT", "GMT+04");
        offsetsMapBuilder.put("MVT", "GMT+05");
        offsetsMapBuilder.put("MYT", "GMT+08");
        offsetsMapBuilder.put("NCT", "GMT+11");
        offsetsMapBuilder.put("NDT", "GMT-02:30");
        offsetsMapBuilder.put("NFT", "GMT+11");
        offsetsMapBuilder.put("NOVT", "GMT+07");
        offsetsMapBuilder.put("NPT", "GMT+05:45");
        offsetsMapBuilder.put("NST", "GMT-03:30");
        offsetsMapBuilder.put("NT", "GMT-03:30");
        offsetsMapBuilder.put("NUT", "GMT-11");
        offsetsMapBuilder.put("NZDT", "GMT+13");
        offsetsMapBuilder.put("NZST", "GMT+12");
        offsetsMapBuilder.put("OMST", "GMT+06");
        offsetsMapBuilder.put("ORAT", "GMT+05");
        offsetsMapBuilder.put("PDT", "GMT-07");
        offsetsMapBuilder.put("PET", "GMT-05");
        offsetsMapBuilder.put("PETT", "GMT+12");
        offsetsMapBuilder.put("PGT", "GMT+10");
        offsetsMapBuilder.put("PHOT", "GMT+13");
        offsetsMapBuilder.put("PHT", "GMT+08");
        offsetsMapBuilder.put("PHST", "GMT+08");
        offsetsMapBuilder.put("PKT", "GMT+05");
        offsetsMapBuilder.put("PMDT", "GMT-02");
        offsetsMapBuilder.put("PMST", "GMT-03");
        offsetsMapBuilder.put("PONT", "GMT+11");
        offsetsMapBuilder.put("PST", "GMT-08");
        offsetsMapBuilder.put("PWT", "GMT+09");
        offsetsMapBuilder.put("PYST", "GMT-03");
        offsetsMapBuilder.put("PYT", "GMT-04");
        offsetsMapBuilder.put("RET", "GMT+04");
        offsetsMapBuilder.put("ROTT", "GMT-03");
        offsetsMapBuilder.put("SAKT", "GMT+11");
        offsetsMapBuilder.put("SAMT", "GMT+04");
        offsetsMapBuilder.put("SAST", "GMT+02");
        offsetsMapBuilder.put("SBT", "GMT+11");
        offsetsMapBuilder.put("SCT", "GMT+04");
        offsetsMapBuilder.put("SDT", "GMT-10");
        offsetsMapBuilder.put("SGT", "GMT+08");
        offsetsMapBuilder.put("SLST", "GMT+05:30");
        offsetsMapBuilder.put("SRET", "GMT+11");
        offsetsMapBuilder.put("SRT", "GMT-03");
        offsetsMapBuilder.put("SST", "GMT-11");
        offsetsMapBuilder.put("SYOT", "GMT+03");
        offsetsMapBuilder.put("TAHT", "GMT-10");
        offsetsMapBuilder.put("THA", "GMT+07");
        offsetsMapBuilder.put("TFT", "GMT+05");
        offsetsMapBuilder.put("TJT", "GMT+05");
        offsetsMapBuilder.put("TKT", "GMT+13");
        offsetsMapBuilder.put("TLT", "GMT+09");
        offsetsMapBuilder.put("TMT", "GMT+05");
        offsetsMapBuilder.put("TRT", "GMT+03");
        offsetsMapBuilder.put("TOT", "GMT+13");
        offsetsMapBuilder.put("TVT", "GMT+12");
        offsetsMapBuilder.put("ULAST", "GMT+09");
        offsetsMapBuilder.put("ULAT", "GMT+08");
        offsetsMapBuilder.put("UTC", "GMT+00");
        offsetsMapBuilder.put("UYST", "GMT-02");
        offsetsMapBuilder.put("UYT", "GMT-03");
        offsetsMapBuilder.put("UZT", "GMT+05");
        offsetsMapBuilder.put("VET", "GMT-04");
        offsetsMapBuilder.put("VLAT", "GMT+10");
        offsetsMapBuilder.put("VOLT", "GMT+04");
        offsetsMapBuilder.put("VOST", "GMT+06");
        offsetsMapBuilder.put("VUT", "GMT+11");
        offsetsMapBuilder.put("WAKT", "GMT+12");
        offsetsMapBuilder.put("WAST", "GMT+02");
        offsetsMapBuilder.put("WAT", "GMT+01");
        offsetsMapBuilder.put("WEST", "GMT+01");
        offsetsMapBuilder.put("WET", "GMT+00");
        offsetsMapBuilder.put("WIB", "GMT+07");
        offsetsMapBuilder.put("WIT", "GMT+09");
        offsetsMapBuilder.put("WITA", "GMT+08");
        offsetsMapBuilder.put("WGST", "GMT-02");
        offsetsMapBuilder.put("WGT", "GMT-03");
        offsetsMapBuilder.put("WST", "GMT+08");
        offsetsMapBuilder.put("YAKT", "GMT+09");
        offsetsMapBuilder.put("YEKT", "GMT+05");

        shortCodeNamesBuilder.put("ACDT", "Australian Central Daylight Saving Time");
        shortCodeNamesBuilder.put("ACST", "Australian Central Standard Time");
        shortCodeNamesBuilder.put("ACT", "Australian Central Time");
        shortCodeNamesBuilder.put("ACWST", "Australian Central Western Standard Time (unofficial)");
        shortCodeNamesBuilder.put("ADT", "Atlantic Daylight Time");
        shortCodeNamesBuilder.put("AEDT", "Australian Eastern Daylight Saving Time");
        shortCodeNamesBuilder.put("AEST", "Australian Eastern Standard Time");
        shortCodeNamesBuilder.put("AET", "Australian Eastern Time");
        shortCodeNamesBuilder.put("AFT", "Afghanistan Time");
        shortCodeNamesBuilder.put("AKDT", "Alaska Daylight Time");
        shortCodeNamesBuilder.put("AKST", "Alaska Standard Time");
        shortCodeNamesBuilder.put("ALMT", "Alma-Ata Time[1]");
        shortCodeNamesBuilder.put("AMST", "Amazon Summer Time (Brazil)");
        shortCodeNamesBuilder.put("AMT", "Armenia Time");
        shortCodeNamesBuilder.put("ANAT", "Anadyr Time[4]");
        shortCodeNamesBuilder.put("AQTT", "Aqtobe Time[5]");
        shortCodeNamesBuilder.put("ART", "Argentina Time");
        shortCodeNamesBuilder.put("AST", "Atlantic Standard Time");
        shortCodeNamesBuilder.put("AWST", "Australian Western Standard Time");
        shortCodeNamesBuilder.put("AZOST", "Azores Summer Time");
        shortCodeNamesBuilder.put("AZOT", "Azores Standard Time");
        shortCodeNamesBuilder.put("AZT", "Azerbaijan Time");
        shortCodeNamesBuilder.put("BNT", "Brunei Time");
        shortCodeNamesBuilder.put("BIOT", "British Indian Ocean Time");
        shortCodeNamesBuilder.put("BIT", "Baker Island Time");
        shortCodeNamesBuilder.put("BOT", "Bolivia Time");
        shortCodeNamesBuilder.put("BRST", "Brasília Summer Time");
        shortCodeNamesBuilder.put("BRT", "Brasília Time");
        shortCodeNamesBuilder.put("BST", "British Summer Time");
        shortCodeNamesBuilder.put("BTT", "Bhutan Time");
        shortCodeNamesBuilder.put("CAT", "Central Africa Time");
        shortCodeNamesBuilder.put("CCT", "Cocos Islands Time");
        shortCodeNamesBuilder.put("CDT", "Central Daylight Time");
        shortCodeNamesBuilder.put("CEST", "Central European Summer Time");
        shortCodeNamesBuilder.put("CET", "Central European Time");
        shortCodeNamesBuilder.put("CHADT", "Chatham Daylight Time");
        shortCodeNamesBuilder.put("CHAST", "Chatham Standard Time");
        shortCodeNamesBuilder.put("CHOT", "Choibalsan Standard Time");
        shortCodeNamesBuilder.put("CHOST", "Choibalsan Summer Time");
        shortCodeNamesBuilder.put("CHST", "Chamorro Standard Time");
        shortCodeNamesBuilder.put("CHUT", "Chuuk Time");
        shortCodeNamesBuilder.put("CIST", "Clipperton Island Standard Time");
        shortCodeNamesBuilder.put("CKT", "Cook Island Time");
        shortCodeNamesBuilder.put("CLST", "Chile Summer Time");
        shortCodeNamesBuilder.put("CLT", "Chile Standard Time");
        shortCodeNamesBuilder.put("COST", "Colombia Summer Time");
        shortCodeNamesBuilder.put("COT", "Colombia Time");
        shortCodeNamesBuilder.put("CST", "Central Standard Time (North America)");
        shortCodeNamesBuilder.put("CT", "Central Time");
        shortCodeNamesBuilder.put("CVT", "Cape Verde Time");
        shortCodeNamesBuilder.put("CWST", "Central Western Standard Time (Australia) unofficial");
        shortCodeNamesBuilder.put("CXT", "Christmas Island Time");
        shortCodeNamesBuilder.put("DAVT", "Davis Time");
        shortCodeNamesBuilder.put("DDUT", "Dumont d'Urville Time");
        shortCodeNamesBuilder.put("DFT", "AIX-specific equivalent of Central European Time[NB 1]");
        shortCodeNamesBuilder.put("EASST", "Easter Island Summer Time");
        shortCodeNamesBuilder.put("EAST", "Easter Island Standard Time");
        shortCodeNamesBuilder.put("EAT", "East Africa Time");
        shortCodeNamesBuilder.put("ECT", "Ecuador Time");
        shortCodeNamesBuilder.put("EDT", "Eastern Daylight Time (North America)");
        shortCodeNamesBuilder.put("EEST", "Eastern European Summer Time");
        shortCodeNamesBuilder.put("EET", "Eastern European Time");
        shortCodeNamesBuilder.put("EGST", "Eastern Greenland Summer Time");
        shortCodeNamesBuilder.put("EGT", "Eastern Greenland Time");
        shortCodeNamesBuilder.put("EST", "Eastern Standard Time (North America)");
        shortCodeNamesBuilder.put("ET", "Eastern Time (North America)");
        shortCodeNamesBuilder.put("FET", "Further-eastern European Time");
        shortCodeNamesBuilder.put("FJT", "Fiji Time");
        shortCodeNamesBuilder.put("FKST", "Falkland Islands Summer Time");
        shortCodeNamesBuilder.put("FKT", "Falkland Islands Time");
        shortCodeNamesBuilder.put("FNT", "Fernando de Noronha Time");
        shortCodeNamesBuilder.put("GALT", "Galápagos Time");
        shortCodeNamesBuilder.put("GAMT", "Gambier Islands Time");
        shortCodeNamesBuilder.put("GET", "Georgia Standard Time");
        shortCodeNamesBuilder.put("GFT", "French Guiana Time");
        shortCodeNamesBuilder.put("GILT", "Gilbert Island Time");
        shortCodeNamesBuilder.put("GIT", "Gambier Island Time");
        shortCodeNamesBuilder.put("GMT", "Greenwich Mean Time");
        shortCodeNamesBuilder.put("GST", "Gulf Standard Time");
        shortCodeNamesBuilder.put("GYT", "Guyana Time");
        shortCodeNamesBuilder.put("HDT", "Hawaii–Aleutian Daylight Time");
        shortCodeNamesBuilder.put("HAEC", "Heure Avancée d'Europe Centrale French-language name for CEST");
        shortCodeNamesBuilder.put("HST", "Hawaii–Aleutian Standard Time");
        shortCodeNamesBuilder.put("HKT", "Hong Kong Time");
        shortCodeNamesBuilder.put("HMT", "Heard and McDonald Islands Time");
        shortCodeNamesBuilder.put("HOVST", "Hovd Summer Time (not used from 2017-present)");
        shortCodeNamesBuilder.put("HOVT", "Hovd Time");
        shortCodeNamesBuilder.put("ICT", "Indochina Time");
        shortCodeNamesBuilder.put("IDLW", "International Day Line West time zone");
        shortCodeNamesBuilder.put("IDT", "Israel Daylight Time");
        shortCodeNamesBuilder.put("IOT", "Indian Ocean Time");
        shortCodeNamesBuilder.put("IRDT", "Iran Daylight Time");
        shortCodeNamesBuilder.put("IRKT", "Irkutsk Time");
        shortCodeNamesBuilder.put("IRST", "Iran Standard Time");
        shortCodeNamesBuilder.put("IST", "Indian Standard Time");
        shortCodeNamesBuilder.put("JST", "Japan Standard Time");
        shortCodeNamesBuilder.put("KALT", "Kaliningrad Time");
        shortCodeNamesBuilder.put("KGT", "Kyrgyzstan Time");
        shortCodeNamesBuilder.put("KOST", "Kosrae Time");
        shortCodeNamesBuilder.put("KRAT", "Krasnoyarsk Time");
        shortCodeNamesBuilder.put("KST", "Korea Standard Time");
        shortCodeNamesBuilder.put("LHST", "Lord Howe Standard Time");
        shortCodeNamesBuilder.put("LINT", "Line Islands Time");
        shortCodeNamesBuilder.put("MAGT", "Magadan Time");
        shortCodeNamesBuilder.put("MART", "Marquesas Islands Time");
        shortCodeNamesBuilder.put("MAWT", "Mawson Station Time");
        shortCodeNamesBuilder.put("MDT", "Mountain Daylight Time (North America)");
        shortCodeNamesBuilder.put("MET", "Middle European Time (same zone as CET)");
        shortCodeNamesBuilder.put("MEST", "Middle European Summer Time (same zone as CEST)");
        shortCodeNamesBuilder.put("MHT", "Marshall Islands Time");
        shortCodeNamesBuilder.put("MIST", "Macquarie Island Station Time");
        shortCodeNamesBuilder.put("MIT", "Marquesas Islands Time");
        shortCodeNamesBuilder.put("MMT", "Myanmar Standard Time");
        shortCodeNamesBuilder.put("MSK", "Moscow Time");
        shortCodeNamesBuilder.put("MST", "Mountain Standard Time (North America)");
        shortCodeNamesBuilder.put("MUT", "Mauritius Time");
        shortCodeNamesBuilder.put("MVT", "Maldives Time");
        shortCodeNamesBuilder.put("MYT", "Malaysia Time");
        shortCodeNamesBuilder.put("NCT", "New Caledonia Time");
        shortCodeNamesBuilder.put("NDT", "Newfoundland Daylight Time");
        shortCodeNamesBuilder.put("NFT", "Norfolk Island Time");
        shortCodeNamesBuilder.put("NOVT", "Novosibirsk Time [9]");
        shortCodeNamesBuilder.put("NPT", "Nepal Time");
        shortCodeNamesBuilder.put("NST", "Newfoundland Standard Time");
        shortCodeNamesBuilder.put("NT", "Newfoundland Time");
        shortCodeNamesBuilder.put("NUT", "Niue Time");
        shortCodeNamesBuilder.put("NZDT", "New Zealand Daylight Time");
        shortCodeNamesBuilder.put("NZST", "New Zealand Standard Time");
        shortCodeNamesBuilder.put("OMST", "Omsk Time");
        shortCodeNamesBuilder.put("ORAT", "Oral Time");
        shortCodeNamesBuilder.put("PDT", "Pacific Daylight Time (North America)");
        shortCodeNamesBuilder.put("PET", "Peru Time");
        shortCodeNamesBuilder.put("PETT", "Kamchatka Time");
        shortCodeNamesBuilder.put("PGT", "Papua New Guinea Time");
        shortCodeNamesBuilder.put("PHOT", "Phoenix Island Time");
        shortCodeNamesBuilder.put("PHT", "Philippine Time");
        shortCodeNamesBuilder.put("PHST", "Philippine Standard Time");
        shortCodeNamesBuilder.put("PKT", "Pakistan Standard Time");
        shortCodeNamesBuilder.put("PMDT", "Saint Pierre and Miquelon Daylight Time");
        shortCodeNamesBuilder.put("PMST", "Saint Pierre and Miquelon Standard Time");
        shortCodeNamesBuilder.put("PONT", "Pohnpei Standard Time");
        shortCodeNamesBuilder.put("PST", "Pacific Standard Time (North America)");
        shortCodeNamesBuilder.put("PWT", "Palau Time[10]");
        shortCodeNamesBuilder.put("PYST", "Paraguay Summer Time[11]");
        shortCodeNamesBuilder.put("PYT", "Paraguay Time[12]");
        shortCodeNamesBuilder.put("RET", "Réunion Time");
        shortCodeNamesBuilder.put("ROTT", "Rothera Research Station Time");
        shortCodeNamesBuilder.put("SAKT", "Sakhalin Island Time");
        shortCodeNamesBuilder.put("SAMT", "Samara Time");
        shortCodeNamesBuilder.put("SAST", "South African Standard Time");
        shortCodeNamesBuilder.put("SBT", "Solomon Islands Time");
        shortCodeNamesBuilder.put("SCT", "Seychelles Time");
        shortCodeNamesBuilder.put("SDT", "Samoa Daylight Time");
        shortCodeNamesBuilder.put("SGT", "Singapore Time");
        shortCodeNamesBuilder.put("SLST", "Sri Lanka Standard Time");
        shortCodeNamesBuilder.put("SRET", "Srednekolymsk Time");
        shortCodeNamesBuilder.put("SRT", "Suriname Time");
        shortCodeNamesBuilder.put("SST", "Samoa Standard Time");
        shortCodeNamesBuilder.put("SYOT", "Showa Station Time");
        shortCodeNamesBuilder.put("TAHT", "Tahiti Time");
        shortCodeNamesBuilder.put("THA", "Thailand Standard Time");
        shortCodeNamesBuilder.put("TFT", "French Southern and Antarctic Time[13]");
        shortCodeNamesBuilder.put("TJT", "Tajikistan Time");
        shortCodeNamesBuilder.put("TKT", "Tokelau Time");
        shortCodeNamesBuilder.put("TLT", "Timor Leste Time");
        shortCodeNamesBuilder.put("TMT", "Turkmenistan Time");
        shortCodeNamesBuilder.put("TRT", "Turkey Time");
        shortCodeNamesBuilder.put("TOT", "Tonga Time");
        shortCodeNamesBuilder.put("TVT", "Tuvalu Time");
        shortCodeNamesBuilder.put("ULAST", "Ulaanbaatar Summer Time");
        shortCodeNamesBuilder.put("ULAT", "Ulaanbaatar Standard Time");
        shortCodeNamesBuilder.put("UTC", "Coordinated Universal Time");
        shortCodeNamesBuilder.put("UYST", "Uruguay Summer Time");
        shortCodeNamesBuilder.put("UYT", "Uruguay Standard Time");
        shortCodeNamesBuilder.put("UZT", "Uzbekistan Time");
        shortCodeNamesBuilder.put("VET", "Venezuelan Standard Time");
        shortCodeNamesBuilder.put("VLAT", "Vladivostok Time");
        shortCodeNamesBuilder.put("VOLT", "Volgograd Time");
        shortCodeNamesBuilder.put("VOST", "Vostok Station Time");
        shortCodeNamesBuilder.put("VUT", "Vanuatu Time");
        shortCodeNamesBuilder.put("WAKT", "Wake Island Time");
        shortCodeNamesBuilder.put("WAST", "West Africa Summer Time");
        shortCodeNamesBuilder.put("WAT", "West Africa Time");
        shortCodeNamesBuilder.put("WEST", "Western European Summer Time");
        shortCodeNamesBuilder.put("WET", "Western European Time");
        shortCodeNamesBuilder.put("WIB", "Western Indonesian Time");
        shortCodeNamesBuilder.put("WIT", "Eastern Indonesian Time");
        shortCodeNamesBuilder.put("WITA", "Central Indonesia Time");
        shortCodeNamesBuilder.put("WGST", "West Greenland Summer Time[14]");
        shortCodeNamesBuilder.put("WGT", "West Greenland Time[15]");
        shortCodeNamesBuilder.put("WST", "Western Standard Time");
        shortCodeNamesBuilder.put("YAKT", "Yakutsk Time");
        shortCodeNamesBuilder.put("YEKT", "Yekaterinburg Time");


        this.shortCodeUTCOffsets = ImmutableMap.copyOf(offsetsMapBuilder);
        this.shortCodesNames = ImmutableMap.copyOf(shortCodeNamesBuilder);

    }

}
