package com.ctdp.springproject.dto;

public class ChangePasswordDto {
    private String oldPassword;
    private String newPassword;
    private String newConfirmedPassword;

    public String getNewConfirmedPassword() {
        return newConfirmedPassword;
    }

    public void setNewConfirmedPassword(String newConfirmedPassword) {
        this.newConfirmedPassword = newConfirmedPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public ChangePasswordDto() {
    }
}
