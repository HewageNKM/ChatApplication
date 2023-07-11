package lk.ijse.chatapplication.service.impl;

import lk.ijse.chatapplication.service.interfaces.LoginService;

import java.util.ArrayList;

public class LoginServiceImpl implements LoginService {
    private ArrayList<String> names = new ArrayList<>();
    @Override
    public boolean validateName(String name) {
        return name.matches("^[a-zA-Z]{3,}$");
    }

    @Override
    public boolean checkDuplicateName(String name) {
        for (String s : names) {
            if (s.equalsIgnoreCase(name)) {
                return false;
            }
        }
        names.add(name);
        return true;
    }
}
