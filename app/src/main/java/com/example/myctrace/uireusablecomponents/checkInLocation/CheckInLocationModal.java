package com.example.myctrace.uireusablecomponents.checkInLocation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CheckInLocationModal {
    private String location;
    private String dateTime;

    public CheckInLocationModal(){}

    public String getLocation() {
        return location;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDateTime(Long dateTime) {
        Date date = new Date(dateTime*1000);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        String formatted = format.format(date);
        this.dateTime = formatted;
    }
}
