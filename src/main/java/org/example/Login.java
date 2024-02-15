package org.example;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Login {
    private String login;
    private String password;

    public Login() {
    }

    public Login(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
