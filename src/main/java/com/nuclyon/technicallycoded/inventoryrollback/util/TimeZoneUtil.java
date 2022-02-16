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
        if (timeZone.equals("UTC")) {
            return 0L;
        } else {
            char signChar = timeZone.length() > 3 ? timeZone.charAt(3) : ' ';

            String utcOffsetFormat;
            if (timeZone.length() > 4 &&
                    timeZone.startsWith("UTC") &&
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

        if (!mainParts[0].equals("UTC")) {
            throw new IllegalArgumentException("UTC format provided (" + utcFormat + ") does not follow the UTC+00:00 format");
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

        offsetsMapBuilder.put("ACDT", "UTC+10:30");
        offsetsMapBuilder.put("ACST", "UTC+09:30");
        offsetsMapBuilder.put("ACT", "UTC+10");
        offsetsMapBuilder.put("ACWST", "UTC+08:45");
        offsetsMapBuilder.put("ADT", "UTC-03");
        offsetsMapBuilder.put("AEDT", "UTC+11");
        offsetsMapBuilder.put("AEST", "UTC+10");
        offsetsMapBuilder.put("AET", "UTC+10");
        offsetsMapBuilder.put("AFT", "UTC+04:30");
        offsetsMapBuilder.put("AKDT", "UTC-08");
        offsetsMapBuilder.put("AKST", "UTC-09");
        offsetsMapBuilder.put("ALMT", "UTC+06");
        offsetsMapBuilder.put("AMST", "UTC-03");
        offsetsMapBuilder.put("AMT", "UTC+04");
        offsetsMapBuilder.put("ANAT", "UTC+12");
        offsetsMapBuilder.put("AQTT", "UTC+05");
        offsetsMapBuilder.put("ART", "UTC-03");
        offsetsMapBuilder.put("AST", "UTC-04");
        offsetsMapBuilder.put("AWST", "UTC+08");
        offsetsMapBuilder.put("AZOST", "UTC+00");
        offsetsMapBuilder.put("AZOT", "UTC-01");
        offsetsMapBuilder.put("AZT", "UTC+04");
        offsetsMapBuilder.put("BNT", "UTC+08");
        offsetsMapBuilder.put("BIOT", "UTC+06");
        offsetsMapBuilder.put("BIT", "UTC-12");
        offsetsMapBuilder.put("BOT", "UTC-04");
        offsetsMapBuilder.put("BRST", "UTC-02");
        offsetsMapBuilder.put("BRT", "UTC-03");
        offsetsMapBuilder.put("BST", "UTC+01");
        offsetsMapBuilder.put("BTT", "UTC+06");
        offsetsMapBuilder.put("CAT", "UTC+02");
        offsetsMapBuilder.put("CCT", "UTC+06:30");
        offsetsMapBuilder.put("CDT", "UTC-05");
        offsetsMapBuilder.put("CEST", "UTC+02");
        offsetsMapBuilder.put("CET", "UTC+01");
        offsetsMapBuilder.put("CHADT", "UTC+13:45");
        offsetsMapBuilder.put("CHAST", "UTC+12:45");
        offsetsMapBuilder.put("CHOT", "UTC+08");
        offsetsMapBuilder.put("CHOST", "UTC+09");
        offsetsMapBuilder.put("CHST", "UTC+10");
        offsetsMapBuilder.put("CHUT", "UTC+10");
        offsetsMapBuilder.put("CIST", "UTC-08");
        offsetsMapBuilder.put("CKT", "UTC-10");
        offsetsMapBuilder.put("CLST", "UTC-03");
        offsetsMapBuilder.put("CLT", "UTC-04");
        offsetsMapBuilder.put("COST", "UTC-04");
        offsetsMapBuilder.put("COT", "UTC-05");
        offsetsMapBuilder.put("CST", "UTC-06");
        offsetsMapBuilder.put("CT", "UTC-06");
        offsetsMapBuilder.put("CVT", "UTC-01");
        offsetsMapBuilder.put("CWST", "UTC+08:45");
        offsetsMapBuilder.put("CXT", "UTC+07");
        offsetsMapBuilder.put("DAVT", "UTC+07");
        offsetsMapBuilder.put("DDUT", "UTC+10");
        offsetsMapBuilder.put("DFT", "UTC+01");
        offsetsMapBuilder.put("EASST", "UTC-05");
        offsetsMapBuilder.put("EAST", "UTC-06");
        offsetsMapBuilder.put("EAT", "UTC+03");
        offsetsMapBuilder.put("ECT", "UTC-05");
        offsetsMapBuilder.put("EDT", "UTC-04");
        offsetsMapBuilder.put("EEST", "UTC+03");
        offsetsMapBuilder.put("EET", "UTC+02");
        offsetsMapBuilder.put("EGST", "UTC+00");
        offsetsMapBuilder.put("EGT", "UTC-01");
        offsetsMapBuilder.put("EST", "UTC-05");
        offsetsMapBuilder.put("ET", "UTC-05");
        offsetsMapBuilder.put("FET", "UTC+03");
        offsetsMapBuilder.put("FJT", "UTC+12");
        offsetsMapBuilder.put("FKST", "UTC-03");
        offsetsMapBuilder.put("FKT", "UTC-04");
        offsetsMapBuilder.put("FNT", "UTC-02");
        offsetsMapBuilder.put("GALT", "UTC-06");
        offsetsMapBuilder.put("GAMT", "UTC-09");
        offsetsMapBuilder.put("GET", "UTC+04");
        offsetsMapBuilder.put("GFT", "UTC-03");
        offsetsMapBuilder.put("GILT", "UTC+12");
        offsetsMapBuilder.put("GIT", "UTC-09");
        offsetsMapBuilder.put("GMT", "UTC+00");
        offsetsMapBuilder.put("GST", "UTC+04");
        offsetsMapBuilder.put("GYT", "UTC-04");
        offsetsMapBuilder.put("HDT", "UTC-09");
        offsetsMapBuilder.put("HAEC", "UTC+02");
        offsetsMapBuilder.put("HST", "UTC-10");
        offsetsMapBuilder.put("HKT", "UTC+08");
        offsetsMapBuilder.put("HMT", "UTC+05");
        offsetsMapBuilder.put("HOVST", "UTC+08");
        offsetsMapBuilder.put("HOVT", "UTC+07");
        offsetsMapBuilder.put("ICT", "UTC+07");
        offsetsMapBuilder.put("IDLW", "UTC-12");
        offsetsMapBuilder.put("IDT", "UTC+03");
        offsetsMapBuilder.put("IOT", "UTC+03");
        offsetsMapBuilder.put("IRDT", "UTC+04:30");
        offsetsMapBuilder.put("IRKT", "UTC+08");
        offsetsMapBuilder.put("IRST", "UTC+03:30");
        offsetsMapBuilder.put("IST", "UTC+05:30");
        offsetsMapBuilder.put("JST", "UTC+09");
        offsetsMapBuilder.put("KALT", "UTC+02");
        offsetsMapBuilder.put("KGT", "UTC+06");
        offsetsMapBuilder.put("KOST", "UTC+11");
        offsetsMapBuilder.put("KRAT", "UTC+07");
        offsetsMapBuilder.put("KST", "UTC+09");
        offsetsMapBuilder.put("LHST", "UTC+10:30");
        offsetsMapBuilder.put("LINT", "UTC+14");
        offsetsMapBuilder.put("MAGT", "UTC+12");
        offsetsMapBuilder.put("MART", "UTC-09:30");
        offsetsMapBuilder.put("MAWT", "UTC+05");
        offsetsMapBuilder.put("MDT", "UTC-06");
        offsetsMapBuilder.put("MET", "UTC+01");
        offsetsMapBuilder.put("MEST", "UTC+02");
        offsetsMapBuilder.put("MHT", "UTC+12");
        offsetsMapBuilder.put("MIST", "UTC+11");
        offsetsMapBuilder.put("MIT", "UTC-09:30");
        offsetsMapBuilder.put("MMT", "UTC+06:30");
        offsetsMapBuilder.put("MSK", "UTC+03");
        offsetsMapBuilder.put("MST", "UTC-07");
        offsetsMapBuilder.put("MUT", "UTC+04");
        offsetsMapBuilder.put("MVT", "UTC+05");
        offsetsMapBuilder.put("MYT", "UTC+08");
        offsetsMapBuilder.put("NCT", "UTC+11");
        offsetsMapBuilder.put("NDT", "UTC-02:30");
        offsetsMapBuilder.put("NFT", "UTC+11");
        offsetsMapBuilder.put("NOVT", "UTC+07");
        offsetsMapBuilder.put("NPT", "UTC+05:45");
        offsetsMapBuilder.put("NST", "UTC-03:30");
        offsetsMapBuilder.put("NT", "UTC-03:30");
        offsetsMapBuilder.put("NUT", "UTC-11");
        offsetsMapBuilder.put("NZDT", "UTC+13");
        offsetsMapBuilder.put("NZST", "UTC+12");
        offsetsMapBuilder.put("OMST", "UTC+06");
        offsetsMapBuilder.put("ORAT", "UTC+05");
        offsetsMapBuilder.put("PDT", "UTC-07");
        offsetsMapBuilder.put("PET", "UTC-05");
        offsetsMapBuilder.put("PETT", "UTC+12");
        offsetsMapBuilder.put("PGT", "UTC+10");
        offsetsMapBuilder.put("PHOT", "UTC+13");
        offsetsMapBuilder.put("PHT", "UTC+08");
        offsetsMapBuilder.put("PHST", "UTC+08");
        offsetsMapBuilder.put("PKT", "UTC+05");
        offsetsMapBuilder.put("PMDT", "UTC-02");
        offsetsMapBuilder.put("PMST", "UTC-03");
        offsetsMapBuilder.put("PONT", "UTC+11");
        offsetsMapBuilder.put("PST", "UTC-08");
        offsetsMapBuilder.put("PWT", "UTC+09");
        offsetsMapBuilder.put("PYST", "UTC-03");
        offsetsMapBuilder.put("PYT", "UTC-04");
        offsetsMapBuilder.put("RET", "UTC+04");
        offsetsMapBuilder.put("ROTT", "UTC-03");
        offsetsMapBuilder.put("SAKT", "UTC+11");
        offsetsMapBuilder.put("SAMT", "UTC+04");
        offsetsMapBuilder.put("SAST", "UTC+02");
        offsetsMapBuilder.put("SBT", "UTC+11");
        offsetsMapBuilder.put("SCT", "UTC+04");
        offsetsMapBuilder.put("SDT", "UTC-10");
        offsetsMapBuilder.put("SGT", "UTC+08");
        offsetsMapBuilder.put("SLST", "UTC+05:30");
        offsetsMapBuilder.put("SRET", "UTC+11");
        offsetsMapBuilder.put("SRT", "UTC-03");
        offsetsMapBuilder.put("SST", "UTC-11");
        offsetsMapBuilder.put("SYOT", "UTC+03");
        offsetsMapBuilder.put("TAHT", "UTC-10");
        offsetsMapBuilder.put("THA", "UTC+07");
        offsetsMapBuilder.put("TFT", "UTC+05");
        offsetsMapBuilder.put("TJT", "UTC+05");
        offsetsMapBuilder.put("TKT", "UTC+13");
        offsetsMapBuilder.put("TLT", "UTC+09");
        offsetsMapBuilder.put("TMT", "UTC+05");
        offsetsMapBuilder.put("TRT", "UTC+03");
        offsetsMapBuilder.put("TOT", "UTC+13");
        offsetsMapBuilder.put("TVT", "UTC+12");
        offsetsMapBuilder.put("ULAST", "UTC+09");
        offsetsMapBuilder.put("ULAT", "UTC+08");
        offsetsMapBuilder.put("UTC", "UTC+00");
        offsetsMapBuilder.put("UYST", "UTC-02");
        offsetsMapBuilder.put("UYT", "UTC-03");
        offsetsMapBuilder.put("UZT", "UTC+05");
        offsetsMapBuilder.put("VET", "UTC-04");
        offsetsMapBuilder.put("VLAT", "UTC+10");
        offsetsMapBuilder.put("VOLT", "UTC+04");
        offsetsMapBuilder.put("VOST", "UTC+06");
        offsetsMapBuilder.put("VUT", "UTC+11");
        offsetsMapBuilder.put("WAKT", "UTC+12");
        offsetsMapBuilder.put("WAST", "UTC+02");
        offsetsMapBuilder.put("WAT", "UTC+01");
        offsetsMapBuilder.put("WEST", "UTC+01");
        offsetsMapBuilder.put("WET", "UTC+00");
        offsetsMapBuilder.put("WIB", "UTC+07");
        offsetsMapBuilder.put("WIT", "UTC+09");
        offsetsMapBuilder.put("WITA", "UTC+08");
        offsetsMapBuilder.put("WGST", "UTC-02");
        offsetsMapBuilder.put("WGT", "UTC-03");
        offsetsMapBuilder.put("WST", "UTC+08");
        offsetsMapBuilder.put("YAKT", "UTC+09");
        offsetsMapBuilder.put("YEKT", "UTC+05");

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
