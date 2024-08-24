package org.ips.xml.signer.xmlsigner.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static void main(String[] args) {

        Instant instant = Instant.now();

        ZoneId zoneId = ZoneId.of("Europe/Paris");

        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
// convert the zoned date time object to a string in the ISO 8601 format
        String isoDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        ZonedDateTime offsettedZonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
// convert the zoned date time object to a string in the ISO 8601 format
        String offsettedIsoDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
// print the result
        OffsetDateTime odt = instant.atOffset(ZoneOffset.UTC);
        String output = odt.toString();
        System.out.println(isoDate);
        System.out.println(output);
        System.out.println(offsettedIsoDate);
        System.out.println(offsettedZonedDateTime);
    }

    public static String iso86ZonedCurrentTime(String zone) {

        Instant instant = Instant.now();
//zone ="Europe/Paris"
        ZoneId zoneId = ZoneId.of(zone);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        String isoDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println(isoDate);
        return isoDate;

    }

    public static String iso86ZonedCurrentTime(String zone,long offsetInSec,long offsetDay) {

        Instant instant = Instant.now();
//zone ="Europe/Paris"
        Instant futureTime = instant.plusSeconds(offsetInSec).plus(offsetDay, ChronoUnit.DAYS);
        ZoneId zoneId = ZoneId.of(zone);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(futureTime, zoneId);
        String isoDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println(isoDate);
        return isoDate;

    }

    public static String iso86CurrentTime() {
        Instant instant = Instant.now();
        OffsetDateTime odt = instant.atOffset(ZoneOffset.UTC);
        String output = odt.toString();
        System.out.println(output);
        return output;
    }

    public static String iso86CurrentTime(long offsetInSec,long offsetDay) {
        Instant instant = Instant.now();
        Instant futureTime = instant.plusSeconds(offsetInSec).plus(offsetDay, ChronoUnit.DAYS);
        OffsetDateTime odt = futureTime.atOffset(ZoneOffset.UTC);
        String output = odt.toString();
        System.out.println(output);
        return output;
    }

    public static String isoCurrentDate() {

        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        String isoDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE);
        System.out.println("Specific ISO Date: " + isoDate);
        return isoDate;
    }

    public static String isoCurrentDate(Long offsetInSec,long offsetDay) {

        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plusSeconds(offsetInSec);
        localDateTime.plusDays(offsetDay);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        String isoDate = zonedDateTime.format(DateTimeFormatter.ISO_DATE);
        System.out.println("Specific ISO Date: " + isoDate);
        return isoDate;
    }
    public static String isoCurrentTime() {

        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        String isoDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println("Specific ISO Date: " + isoDate);
        return isoDate;
    }

    public static String isoCurrentTime(Long offsetInSec,long offsetDay) {

        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plusSeconds(offsetInSec);
        localDateTime.plusDays(offsetDay);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        String isoDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println("Specific ISO Date: " + isoDate);
        return isoDate;
    }


}
