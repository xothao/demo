package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;

public class App {

    static String email;
    static String pwd;
    static String price;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        System.out.println("Begin...");

        email = args[0];
        pwd = args[1];
        price = args[2];

        // https://www.tesla.com/inventory/api/v1/inventory-results?query={"query":{"model":"my","condition":"new","options":{"TRIM":["LRAWD"]},"arrangeby":"Relevance","order":"desc","market":"US","language":"en","super_region":"north america","lng":-93.2860769,"lat":45.31968819999999,"zip":"55011","range":200,"region":"MN"},"offset":0,"count":50,"outsideOffset":0,"outsideSearch":false}

        // model = m3 (model 3)
        // TRIM = PAWD (m3 performance awd)

        // model = my (model y)
        // TRIM = LRAWD (y long range awd)

        // model = my (model y)
        // TRIM = MYAWD (y std range awd)

        String encodedURL = URLEncoder.encode("{\"query\":{\"model\":\"my\",\"condition\":\"new\",\"options\":{\"TRIM\":[\"MYAWD\"]},\"arrangeby\":\"Relevance\",\"order\":\"desc\",\"market\":\"US\",\"language\":\"en\",\"super_region\":\"north america\",\"lng\":-93.2860769,\"lat\":45.31968819999999,\"zip\":\"55011\",\"range\":200,\"region\":\"MN\"},\"offset\":0,\"count\":50,\"outsideOffset\":0,\"outsideSearch\":false}", "UTF-8");

        URL url = new URL("https://www.tesla.com/inventory/api/v1/inventory-results?query=" + encodedURL);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        InputStream responseStream = connection.getInputStream();

        BufferedReader rd = new BufferedReader(new InputStreamReader(responseStream, Charset.forName("UTF-8")));
        StringBuffer strBuf = new StringBuffer();

        while (rd.ready()) {
            strBuf.append(rd.readLine());
        }

        System.out.println("text: " + strBuf.toString());

        Map<String, Object> myMap = new HashMap<String, Object>();

        ObjectMapper objectMapper = new ObjectMapper();
        myMap = objectMapper.readValue(strBuf.toString().getBytes(), HashMap.class);
        System.out.println("Map is: " + myMap.size());

        Set<String> keys = myMap.keySet();
        ArrayList list = null;
        for (String string : keys) {
            System.out.println("key: " + string);
            if ("results".equalsIgnoreCase(string)) {
                Object obj = myMap.get(string);
                if(obj instanceof ArrayList) {
                    list = (ArrayList) obj;
                }
            } else {
                System.out.println("val: " + myMap.get(string));
            }
        }
        int num = 0;
        if(list != null) {
            System.out.println("list count: " + list.size());
            for (int i = 0; i < list.size(); i++) {
                // System.out.println("item: " + object.toString());
                System.out.println("item: " + i);
                LinkedHashMap obj = (LinkedHashMap) list.get(i);
                Set set = obj.keySet();
                for (Object object : set) {
                    // System.out.println(" key: " + object.toString() + ", val: " +
                    // obj.get(object));
                    if ("PurchasePrice".equalsIgnoreCase(object.toString())) {
                        int pp = (int) obj.get(object);
                        System.out.println("    key: " + object.toString() + ", val: " + pp);
                        if (pp < Integer.parseInt(price)) {
                            num++;
                        }
                    }
                    // System.out.println("val: " + obj.get("Price"));
                }
            }
        }
        if (num > 0) {
            emailMe(num);
        }
    }

    private static void emailMe(int num) {
        String to = email;
        String from = email;

        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.port", "587");
        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pwd);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Ping");
            message.setText("Hello, there is/are " + num + " car(s) available.");

            // Send message
            Transport.send(message);
            System.out.println("message sent successfully....");

        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

    }
}
