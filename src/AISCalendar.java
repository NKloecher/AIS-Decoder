class AISCalendar {

    //Lol.......................

    public enum MONTH {
        NULL("N/A"),
        JANUARY("January"),
        FEBRUARY("February"),
        MARCH("March"),
        APRIL("April"),
        MAY("May"),
        JUNE("June"),
        JULY("July"),
        AUGUST("August"),
        SEPTEMBER("September"),
        OCTOBER("October"),
        NOVEMBER("November"),
        DECEMBER("December");

        private String month;
        MONTH(String month){this.month = month;}
        public static String getMonthString(int month){return MONTH.values()[month].month;}
    }

    public enum DAY {
        MONDAY("Monday"),
        TUESDAY("Tuesday"),
        WEDNESDAY("Wednesday"),
        THURSDAY("Thursday"),
        FRIDAY("Friday"),
        SATURDAY("Saturday"),
        SUNDAY("Sunday");

        private String day;
        DAY(String day){this.day = day;}
        public static String getDayString(int day){return DAY.values()[day].day;}
    }

}
