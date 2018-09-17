/*
    Handles msg type 4
 */
public class AISTypeFourDecoder extends AISDecoder {

    private int year; //1-9999; 0=N/A
    private int month; //1-12; 0=N/A
    private int day; //1-31
    private int hour;
    private int minute;
    private int second;
    private int fixQuality;
    private int epfp; //Electronic Position Fixing Device


    AISTypeFourDecoder(String payload){

        StringBuilder sb = binaryPayloadToStringBuilder(payload);

        msgType = Integer.parseInt(sb.substring(0,6),2) + "";
        repeats = Integer.parseInt(sb.substring(6,8),2) + "";
        MMSI = String.valueOf(Integer.parseInt(sb.substring(8,38),2));
        year = Integer.parseInt(sb.substring(38,52),2);
        month = Integer.parseInt(sb.substring(52,56),2);
        day = Integer.parseInt(sb.substring(56,61),2);
        hour = Integer.parseInt(sb.substring(61,66),2);
        minute = Integer.parseInt(sb.substring(66,72),2);
        second = Integer.parseInt(sb.substring(72,78),2);
        fixQuality = Integer.parseInt(sb.substring(78,79),2);

        String binLng = sb.substring(79,107);
        negLng = binLng.startsWith("1");
        lng = Integer.parseInt(binLng,2);
        String binLat = sb.substring(107,134);
        negLat = binLat.startsWith("1");
        lat = Integer.parseInt(binLat,2);

        epfp = Integer.parseInt(sb.substring(134,138),2);
        //trailing random stuff
    }




    @Override
    public String decode(){
        String year_s,month_s,day_s,hour_s,minute_s,second_s,lng_s,lat_s, epfd_s, fix_quality;

        //Could possibly use Calendar instance of the ints
        year_s = String.valueOf(year);
//        month_s = AISCalendar.MONTH.getMonthString(month);
//        day_s = AISCalendar.DAY.getDayString(day);
        month_s = String.valueOf(month);
        day_s = String.valueOf(day);
        hour_s = String.valueOf(hour);
        minute_s = String.valueOf(minute);
        second_s = String.valueOf(second);

        fix_quality = positionalAccuracy(fixQuality);

        lng_s = calcLng(lng);
        lat_s = calcLat(lat);

        epfd_s = AISStatus.EPFD.getEnumStatus(epfp);

        return String.format(
                        "AISTypeFourDecoder{\n" +
                                "msgType='%s'\n" +
                                "repeats='%s'\n" +
                                "MMSI='%s'\n" +
                                "year='%s'\n" +
                                "month='%s'\n" +
                                "day='%s'\n" +
                                "hour='%s'\n" +
                                "minutes='%s'\n" +
                                "seconds='%s'\n" +
                                "longitude='%s'\n" +
                                "latitude='%s'\n" +
                                "accuracy='%s'\n" +
                                "EPFD='%s'\n" +
                                "}",
                msgType,repeats,MMSI,year_s,month_s,day_s,
                hour_s,minute_s,second_s,lng_s,lat_s,fix_quality,epfd_s);
    }

    @Override
    public String toString() {
        //do better :)
        return "AISTypeFourDecoder{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                ", fixQuality=" + fixQuality +
                ", epfp=" + epfp +
                ", msgType='" + msgType + '\'' +
                ", repeats='" + repeats + '\'' +
                ", MMSI='" + MMSI + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
