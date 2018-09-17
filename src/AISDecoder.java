/**
 * Abstract base class for decoders.<br>
 * Error messages {<br>
 *         "Undefined" = bad data value<br>
 *         "Unknown" = correct data, value not sent<br>
 *         }
 */
abstract class AISDecoder {

    private final static int LONGITUDE_LENGTH = 28;
    private final static int LATITUDE_LENGTH = 27;
    private final static int LNG_ONES = (1 << LONGITUDE_LENGTH)-1;
    private final static int LAT_ONES = (1 << LATITUDE_LENGTH)-1;

    String msgType; //message type
    String repeats; //number of repeats
    String MMSI; //ship id
    double lng; //longitude
    boolean negLng; //if binary string is negative
    double lat; //latitude
    boolean negLat; //as negLng

    /**
     * Assumes 8bit string that can be split into 6bit pieces<br>
     * Assume the AIS ascii decoding<br>
     * Skips trailing white-space (any whitespace)
     * @param binary full binary string containing the callsign
     * @return callsign as a string
     */
    String binaryAscii6ToString(String binary){
        StringBuilder cb = new StringBuilder();
        for (int i = 0; i < binary.length();i+=6){
            int ascii6 = Integer.parseInt(binary.substring(i,i+6),2);
            if (ascii6 < 32) ascii6 += 64;
            if (ascii6 != 32) cb.append((char)ascii6);
        }
        return cb.toString();
    }

    StringBuilder binaryPayloadToStringBuilder(String payload) {
        StringBuilder sb = new StringBuilder();
        payload.chars().forEach(ch -> {
            int asciiVal = (ch - 48) > 40 ? ch - 48 - 8 : ch - 48;
            String binaryAscii = Integer.toBinaryString(asciiVal);
            String binaryPadded = String.format("%6s",binaryAscii).replace(" ", "0");
            //Better way than format + replace?
            sb.append(binaryPadded);
        });
        return sb;
    }

    /**
     * To be implemented per specific instance of a decoder
     */
    abstract String decode();

    /**
     * Calculates rate of turn
     * @param rate rate of turn as double
     * @return ROT as a string as per manual
     */
    String rateOfTurn(double rate){
        if (rate == 128) return "Unknown";
        if (rate == 127) return "Turning right at more than 5deg/30s (No TI available)";
        if (rate < 127 && rate >= 1) return Math.pow(rate / 4.733, 2) + "";
        if (rate == 0) return "Not turning";
        if (rate <= 1 && rate >= -126) return -Math.pow(rate / 4.733, 2) + ""; //right if..?
        if (rate == -127) return "Turning left at more than 5deg/30s (No TI available)";
        else return "Undefined";
    }

    /**
     * Accuracy of positional fixing device
     * @param accuracy positional accuracy => either 0 or 1
     * @return accuracy details as a string
     */
    String positionalAccuracy(int accuracy){
        //technically returns a boolean
        if (accuracy == 0) return "> 10m accuracy";
        if (accuracy == 1) return "< 10cm accuracy";
        else return "Unknown";
    }

    /**
     * Calculates speed over ground
     * @param speed speed as a double
     * @return knots as string
     */
    String speedOverGround(double speed){
        if (speed == 1023) return "Not available";
        if (speed == 1022) return "102.2kn or higher";
        else return speed / 10.0 + "";
    }

    /**
     * Calculates longitude.<br>
     * @param lng as a double
     * @return longitude as string
     */
    String calcLng(double lng){
        int longitude = (int) lng;
        double lngDbl = lng / 600000.0;
        if (negLng){
            longitude ^= LNG_ONES;
            longitude += 1;
            longitude = -longitude;
            lngDbl = longitude / 600000.0;
            if (lngDbl == 181) return "Unknown";
            if (lngDbl > 181 || lngDbl < -180) return "Undefined";
            return Double.toString(lngDbl);
        }
        if (lngDbl == 181) return "Unknown";
        if (lngDbl > 181 || lngDbl < -180) return "Undefined";
        return Double.toString(lngDbl);
    }

    /**
     * Calculates latitude.<br>
     * @param lat as a double
     * @return latitude as string
     */
    String calcLat(double lat){
        int latitude = (int) lat;
        double latDbl = lat / 600000.0;
        if (negLat){
            latitude ^= LAT_ONES;
            latitude += 1;
            latitude = -latitude;
            latDbl = latitude / 600000.0;
            if (latDbl == 91) return "Unknown";
            if (latDbl < -90 || latDbl > 91) return "Undefined";
            return Double.toString(latDbl);
        }
        if (latDbl == 91) return "Unknown";
        if (latDbl < -90 || latDbl > 91) return "Undefined";
        return Double.toString(latDbl);
    }

    /**
     * Calculates the course over ground
     * @param cog course over ground, double
     * @return cog in degrees as string
     */
    String calcCourse(double cog){
        if (cog == 3600) return "Unknown";
        else return cog % 360 + "";
    }

    /**
     * Just the seconds part of the UTC timestamp when the message was received
     * @param stamp int stamp
     * @return Seconds of the timestamp in UTC or manual message
     */
    String calcTimestamp(int stamp){
        if (stamp == 60) return "Unknown";
        if (stamp == 61) return "Manual input mode";
        if (stamp == 62) return "EPFS in estimated (dead reckoning) mode";
        if (stamp == 63) return "Inoperative";
        if (stamp < 0 || stamp > 63) return "Undefined";
        else return Integer.toString(stamp);
    }

    /**
     *
     * @param maneuver binary string
     * @return decoded maneuver as string
     */
    String decodeManeuver(int maneuver){
        //Could use enum here as well... meh.. the enums could be like this too...
        switch (maneuver){
            case 0:
                return "Not available";
            case 1:
                return "No special maneuver";
            case 2:
                return "Special maneuver";
            default:
                return "Undefined";
        }
    }

    /**
     * Calculates heading
     * @param trueHeading binary string
     * @return heading as string or unknown
     */
    String calcHeading(int trueHeading){
        if (trueHeading == 511) return "Unknown";
        return trueHeading % 360 + "";
    }

    String aisVersion(int versionNumber){
        switch (versionNumber){
            case 0:
                return "ITU1371 (default)";
            case 1: case 2: case 3:
                return "Future editions";
            default:
                return "Undefined";
        }
    }
}
