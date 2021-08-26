package com.example.soundapp.Responses;

public class User {
    private String name;
    private String email;
    private String deviceId;
    private String soundLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSoundLevel() {
        return soundLevel;
    }

    public void setSoundLevel(String soundLevel) {
        this.soundLevel = soundLevel;
    }
}
