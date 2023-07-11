package lk.ijse.chatapplication.service.interfaces;

import lk.ijse.chatapplication.service.SuperService;

public interface LoginService extends SuperService {
    boolean validateName(String name);
    boolean checkDuplicateName(String name);
}
