package com.xuchongyang.easyphone.call;

public class SipConfigEntity {

    private String domain;
    private String password;
    private String proxy;
    private String userid;
    private String masterid;

    public final static int SIP_SERVER_PORT_DEFAULT = 5055;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMasterid() {
        return masterid;
    }

    public void setMasterid(String masterid) {
        this.masterid = masterid;
    }

    public String getServerHost(){
        if(proxy == null || proxy.isEmpty()){
            return "";
        }
        if(proxy.contains(":")){
            return proxy.substring(0,proxy.lastIndexOf(":"));
        }else{
            return proxy;
        }
    }

    public int getServerPort(){
        if(proxy == null || proxy.isEmpty() || !proxy.contains(":")){
            return SIP_SERVER_PORT_DEFAULT;
        }
        String port =  proxy.substring(proxy.lastIndexOf(":"));
        try{
            return Integer.valueOf(port);
        }catch (Exception e){
            return SIP_SERVER_PORT_DEFAULT;
        }
    }
}
