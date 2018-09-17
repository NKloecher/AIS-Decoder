abstract class AISStatus {

    public enum NavigationStatus {
        ZERO("Under way using engine"),
        ONE("At anchor"),
        TWO("Not under command"),
        THREE("Restricted manoeuverability"),
        FOUR("Constrained by draught"),
        FIVE("Moored"),
        SIX("Aground"),
        SEVEN("Engaged in Fishing"),
        EIGHT("Under way sailing"),
        NINE("Reserved for future amendment of Navigational Status for HSC"),
        TEN("Reserved for future amendment of Navigational Status for WIG"),
        ELEVEN("Reserved for future use"),
        TWELVE("Reserved for future use"),
        THIRTEEN("Reserved for future use"),
        FOURTEEN("AIS-SART is active"),
        FIFTEEN("Not defined");


        private String status;

        NavigationStatus(String status){
            this.status = status;
        }

        /**
         *
         * @param statusNumber Binary string (non-decoded from nmea/ais data payload)
         * @return String with navigational status
         */
        public static String getEnumStatus(int statusNumber){
            NavigationStatus[] values = NavigationStatus.values();
            if (statusNumber >= values.length) return "Not defined";
            return values[statusNumber].status;
        }
    }

    public enum EPFD {
        ZERO("Undefined"),
        ONE("GPS"),
        TWO("GLONASS"),
        THREE("Combined GPS / GLONASS"),
        FOUR("Loran-C"),
        FIVE("Chayka"),
        SIX("Integrated navigation system"),
        SEVEN("Surveyed"),
        EIGHT("Galileo");

        private String fix_type;
        EPFD(String fix_type){this.fix_type = fix_type;}
        public static String getEnumStatus(int statusNumber){return EPFD.values()[statusNumber].fix_type;}
    }


}
