package lk.ijse.chatapplication.DAO;

import lk.ijse.chatapplication.DAO.impl.ChatDAOImpl;

public class DAOFactory {
    private static DAOFactory daoFactory;

    public enum DAOType{
        CHAT,LOGIN
    }
    private DAOFactory() {
    }
    public static DAOFactory getDAOFactory(){
        if(daoFactory==null){
            daoFactory=new DAOFactory();
        }
        return daoFactory;
    }
    public SuperDAO getDAO(DAOType daoType){
        switch (daoType){
            case CHAT:
                return new ChatDAOImpl();
            default:
                return null;
        }
    }
}
