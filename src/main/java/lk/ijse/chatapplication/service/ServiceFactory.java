package lk.ijse.chatapplication.service;

import lk.ijse.chatapplication.service.impl.ChatServiceImpl;
import lk.ijse.chatapplication.service.impl.LoginServiceImpl;

public class ServiceFactory {
    private static ServiceFactory serviceFactory;

    public enum ServiceType{
        CHAT,LOGIN
    }
    private ServiceFactory() {
    }
    public static ServiceFactory getServiceFactory(){
        if(serviceFactory==null){
            serviceFactory=new ServiceFactory();
        }
        return serviceFactory;
    }
    public SuperService getService(ServiceType serviceType){
        switch (serviceType){
            case CHAT:
                return new ChatServiceImpl();
            case LOGIN:
                return new LoginServiceImpl();
            default:
                return null;
        }
    }
}
