/*
    This class handles message types 1,2,3
    Error messages {
        "Undefined" = bad data value
        "Unknown" = correct data, value not sent
        }
 */
public class AISTypeOneDecoder extends AISDecoder {

    private int navStatus; //see enum NavigationStatus
    private double ROT; //rate of turn
    private double SOG; //speed over ground
    private int posAcc; //position accuracy
    private double COG; //course over ground
    private int heading; //true heading
    private int timestamp; //second of UTC timestamp when msg was received
    private int maneuver; //part of a special maneuver

    AISTypeOneDecoder(String payload){

        StringBuilder sb = binaryPayloadToStringBuilder(payload);

        msgType = Integer.parseInt(sb.substring(0,6),2) + "";
        repeats = Integer.parseInt(sb.substring(6,8),2) + "";
        MMSI = String.valueOf(Integer.parseInt(sb.substring(8,38),2));
        navStatus = Integer.parseInt(sb.substring(38,42),2);
        ROT = Integer.parseInt(sb.substring(42,50),2);
        SOG = Integer.parseInt(sb.substring(50,60),2);
        posAcc = Integer.parseInt(sb.substring(60,61),2);

        String binLng = sb.substring(61,89);
        negLng = binLng.startsWith("1");
        lng = Integer.parseInt(binLng,2);

        String binLat = sb.substring(89,116);
        negLat = binLat.startsWith("1");
        lat = Integer.parseInt(binLat,2);

        COG = Integer.parseInt(sb.substring(116,128),2);
        heading = Integer.parseInt(sb.substring(128,137),2);
        timestamp = Integer.parseInt(sb.substring(137,143),2);
        maneuver = Integer.parseInt(sb.substring(143,145),2);

    }

    @Override
    public String decode(){
        String navStatus_s,ROT_s,SOG_s,posAcc_s,lng_s,lat_s,COG_s,heading_s,timestamp_s,maneuver_s;

        //get nav status from the enum
        navStatus_s = AISStatus.NavigationStatus.getEnumStatus(navStatus);

        ROT_s = rateOfTurn(ROT);
        SOG_s = speedOverGround(SOG);
        posAcc_s = positionalAccuracy(posAcc);

        lng_s = calcLng(lng);
        lat_s = calcLat(lat);

        COG_s = calcCourse(COG);
        heading_s = calcHeading(heading);
        timestamp_s = calcTimestamp(timestamp);
        maneuver_s = decodeManeuver(maneuver);

        //todo String.format / Stringbuilder for better message handling (decimals)

        return "AISTypeOneDecoder{decoded\n" +
                "msgType='" + msgType + '\'' +
                "\nrepeats='" + repeats + '\'' +
                "\nMMSI='" + MMSI + '\'' +
                "\nnavStatus='" + navStatus_s + '\'' +
                "\nROT='" + ROT_s + '\'' +
                "\nSOG='" + SOG_s + '\'' +
                "\nposAcc='" + posAcc_s + '\'' +
                "\nlng='" + lng_s + '\'' +
                "\nlat='" + lat_s + '\'' +
                "\nCOG='" + COG_s + '\'' +
                "\nheading='" + heading_s + '\'' +
                "\ntimestamp='" + timestamp_s + '\'' +
                "\nmaneuver='" + maneuver_s + '\'' +
                "\n}";

    }




    @Override
    public String toString() {
        return "AISTypeOneDecoder{encoded\n" +
                "msgType='" + msgType + '\'' +
                "\nrepeats='" + repeats + '\'' +
                "\nMMSI='" + MMSI + '\'' +
                "\nnavStatus='" + navStatus + '\'' +
                "\nROT='" + ROT + '\'' +
                "\nSOG='" + SOG + '\'' +
                "\nposAcc='" + posAcc + '\'' +
                "\nlng='" + lng + '\'' +
                "\nlat='" + lat + '\'' +
                "\nCOG='" + COG + '\'' +
                "\nheading='" + heading + '\'' +
                "\ntimestamp='" + timestamp + '\'' +
                "\nmaneuver='" + maneuver + '\'' +
                "\n}";
    }
}
