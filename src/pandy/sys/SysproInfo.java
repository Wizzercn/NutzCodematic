package pandy.sys;

import java.util.Properties;

public class SysproInfo {
    String driver;
    String url;
    String user;
    String password;
    String dbname;
    String maxcon;
    String author;

    public SysproInfo() {

    }

    public SysproInfo(Properties props) {
        driver = props.getProperty("driver");
        url = props.getProperty("sqlserverbase.url");
        user = props.getProperty("sqlserverbase.user");
        password = props.getProperty("sqlserverbase.password");
        maxcon = props.getProperty("sqlserverbase.maxconn", "0");
        author = props.getProperty("author");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getMaxcon() {
        return maxcon;
    }

    public void setMaxcon(String maxcon) {
        this.maxcon = maxcon;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
