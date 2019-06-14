package com.lombardrisk.commons;

public class PropHelper extends org.yiwan.webcore.util.PropHelper {

    public static void setProperty(String key, String value){
        System.setProperty(key,value);
    }
}
