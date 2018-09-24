//abstract class AISStatus {
//
//    //Eventual enum for ship flag (first ~3 nums in MMSI)
//
//    public enum NavigationStatus {
//        ZERO("Under way using engine"),
//        ONE("At anchor"),
//        TWO("Not under command"),
//        THREE("Restricted manoeuverability"),
//        FOUR("Constrained by draught"),
//        FIVE("Moored"),
//        SIX("Aground"),
//        SEVEN("Engaged in Fishing"),
//        EIGHT("Under way sailing"),
//        NINE("Reserved for future amendment of Navigational Status for HSC"),
//        TEN("Reserved for future amendment of Navigational Status for WIG"),
//        ELEVEN("Reserved for future use"),
//        TWELVE("Reserved for future use"),
//        THIRTEEN("Reserved for future use"),
//        FOURTEEN("AIS-SART is active"),
//        FIFTEEN("Not defined");
//
//
//        private String status;
//
//        NavigationStatus(String status){
//            this.status = status;
//        }
//
//        /**
//         *
//         * @param statusNumber Binary string (non-decoded from nmea/ais data payload)
//         * @return String with navigational status
//         */
//        public static String getNavStatus(int statusNumber){
//            NavigationStatus[] values = NavigationStatus.values();
//            if (statusNumber >= values.length) return "Not defined";
//            return values[statusNumber].status;
//        }
//    }
//
//    public enum EPFD {
//        ZERO("Unknown"), //'undefined', but changed to unknown for consistency
//        ONE("GPS"),
//        TWO("GLONASS"),
//        THREE("Combined GPS / GLONASS"),
//        FOUR("Loran-C"),
//        FIVE("Chayka"),
//        SIX("Integrated navigation system"),
//        SEVEN("Surveyed"),
//        EIGHT("Galileo");
//
//        private String fix_type;
//        EPFD(String fix_type){this.fix_type = fix_type;}
//        public static String getEPFD(int statusNumber){
//            if (statusNumber == 15) return EPFD.ZERO.fix_type;
//            return EPFD.values()[statusNumber].fix_type;
//        }
//    }
//
//    public enum SHIP_TYPE {;
////        ZERO("Not available"),
////        ONE_TO_NINETEEN("Reserved for future use"),
////        TWENTY("Wing in ground"),
////        THIRTY("Fishing"),
////        THIRTYONE("Towing");
//
//        private String type;
//        SHIP_TYPE(String type){this.type=type;}
//        public static String getShipType(int statusNumber){
//            if (statusNumber < 0) return "Undefined";
//            if (statusNumber == 0) return "Not available";
//            if (statusNumber < 20) return "Reserved for future use";
//            else if (statusNumber < 30) return "Wing in ground, various";
//            else if (statusNumber == 30) return "Fishing";
//            else if (statusNumber == 31 || statusNumber == 32) return "Towing";
//            else if (statusNumber == 33) return "Dredging or underwater ops";
//            else if (statusNumber == 34) return "Diving ops";
//            else if (statusNumber == 35) return "Military ops";
//            else if (statusNumber == 36) return "Sailing";
//            else if (statusNumber == 37) return "Pleasure craft";
//            else if (statusNumber >= 40 && statusNumber < 50) return "High speed craft";
//            else if (statusNumber == 50) return "Pilot vessel";
//            else if (statusNumber == 51) return "Search and rescue vessel";
//            else if (statusNumber == 52) return "Tug";
//            else if (statusNumber == 53) return "Port tender";
//            else if (statusNumber == 54) return "Anti-pollution equipment";
//            else if (statusNumber == 55) return "Law enforcement";
//            else if (statusNumber == 56 || statusNumber == 57) return "Spare - local vessel";
//            else if (statusNumber == 58) return "Medical transport";
//            else if (statusNumber == 59) return "Noncombatant ship according to RR Resolution No. 18";
//            else if (statusNumber >= 60 && statusNumber < 70) return "Passenger ship, all types";
//            else if (statusNumber >= 70 && statusNumber < 80) return "Cargo ship, all types";
//            else if (statusNumber >= 80 && statusNumber < 90) return "Tanker, all types";
//            else if (statusNumber >= 90 && statusNumber < 100) return "Other types";
//            else return "So many types, and you chose undefined";
//        }
//
//    }
//
//}
