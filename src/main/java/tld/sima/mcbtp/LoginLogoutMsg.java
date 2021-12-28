package tld.sima.mcbtp;

import org.bukkit.entity.Player;

public class LoginLogoutMsg {

    private final String loginMsg, logoutMsg;
    private final String loginPrefix, logoutPrefix;

    public LoginLogoutMsg(String loginMsg, String logoutMsg, String loginPrefix, String logoutPrefix){
        this.loginMsg = loginMsg;
        this.logoutMsg = logoutMsg;
        this.loginPrefix = loginPrefix;
        this.logoutPrefix = logoutPrefix;
    }

    public String getLoginMsg(Player player){
        return loginMsg.replaceAll("\\{username}", player.getName());
    }

    public String getLogoutMsg(Player player){
        return logoutMsg.replaceAll("\\{username}", player.getName());
    }

    public String getLoginPrefix(){
        return loginPrefix;
    }

    public String getLogoutPrefix(){
        return logoutPrefix;
    }
}
