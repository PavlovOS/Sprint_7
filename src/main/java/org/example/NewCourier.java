package org.example;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewCourier {
    private String login;
    private String password;
    private String firstName;

    public NewCourier() {
    }

    public NewCourier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }
}
