package org.d3ifcool.addictcontrol.Database;

/**
 * Created by cool on 10/14/2018.
 */

public class User {
    public String username;
    public String password;
    public String UUIDImage;
    public String typeAccount;
    public String smartphoneOn;
    public boolean isLock;

    public User() {
    }

    public User(String username, String password, String UUIDImage, String typeAccount, String smartphoneOn, boolean isLock) {
        this.username = username;
        this.password = password;
        this.UUIDImage = UUIDImage;
        this.typeAccount = typeAccount;
        this.smartphoneOn = smartphoneOn;
        this.isLock = isLock;
    }

}
