package lk.ijse.chatapplication.service.impl;

import lk.ijse.chatapplication.service.interfaces.LoginService;

public class LoginServiceImpl implements LoginService {
    @Override
    public boolean validateName(String name) {
        return name.matches("^[a-zA-Z]{3,}$");
    }
}
