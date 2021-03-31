package com.pk.developer.downloader.insta;

public class ApiUtils {
    public static final String BASE_URL = "https://instagram.com/";

    public static String getUsernameUrl(String username){
        return BASE_URL + username + "/";
    }
}
