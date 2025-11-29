package com.mrk.jsoupysails;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class WebClient {
    public WebClient() {

    }
    public static String getTemplates() {
        String localUrl = "https://192.168.2.1/templates.txt";
        String text = "";
        try {
            Document doc = Jsoup.connect(localUrl).get();
            text = doc.body().text();            // visible text
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}
