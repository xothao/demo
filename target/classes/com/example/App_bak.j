package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    
    public static void main(String args[]) throws IOException {
        // https://www.tesla.com/inventory/api/v1/inventory-results?query={"query":{"model":"my","condition":"new","options":{"TRIM":["LRAWD"],"PAINT":["WHITE"]},"arrangeby":"Price","order":"asc","market":"US","language":"en","super_region":"north
        // america","lng":-93.2860769,"lat":45.31968819999999,"zip":"55011","range":200,"region":"MN"},"offset":0,"count":50,"outsideOffset":0,"outsideSearch":false}
        URL url = new URL(
                "https://www.tesla.com/inventory/api/v1/inventory-results?query=%7B%22query%22%3A%7B%22model%22%3A%22my%22%2C%22condition%22%3A%22new%22%2C%22options%22%3A%7B%22TRIM%22%3A%5B%22LRAWD%22%5D%7D%2C%22arrangeby%22%3A%22Price%22%2C%22order%22%3A%22asc%22%2C%22market%22%3A%22US%22%2C%22language%22%3A%22en%22%2C%22super_region%22%3A%22north%20america%22%2C%22lng%22%3A-93.2860769%2C%22lat%22%3A45.31968819999999%2C%22zip%22%3A%2255011%22%2C%22range%22%3A200%2C%22region%22%3A%22MN%22%7D%2C%22offset%22%3A0%2C%22count%22%3A50%2C%22outsideOffset%22%3A0%2C%22outsideSearch%22%3Afalse%7D"
                );
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // print in String
        // System.out.println(response.toString());
        // Read JSON response and print
        // read json file data to String
        byte[] jsonData = response.toString().getBytes();

        // create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper objectMapper2 = new ObjectMapper();

        // read JSON like DOM Parser
        JsonNode rootNode = objectMapper.readTree(jsonData);
        JsonNode resultsNode = rootNode.path("results");
        // System.out.println("node = " + idNode.);
        // JsonNode approxNode = resultsNode.path("approximate");
        Iterator<Entry<String, JsonNode>> elements = resultsNode.fields();
        while (elements.hasNext()) {
            Entry<String, JsonNode> next = (Entry<String, JsonNode>) elements.next();
            System.out.println("key = " + next.getKey());
            // System.out.println("val = " + next.getValue());
            if(next.getKey().equalsIgnoreCase("approximate")) {
                JsonNode approxNode = objectMapper2.readTree(next.getValue().asText().getBytes());
                JsonNode newNode = approxNode.path("approximate");
                Iterator<Entry<String, JsonNode>> elements2 = newNode.fields();
                while (elements2.hasNext()) {
                    Entry<String, JsonNode> next2 = (Entry<String, JsonNode>) elements2.next();
                    System.out.println("key = " + next2.getKey());
                }
            }
            // JsonNode priceNode = idNode.path("PurchasePrice");
            // Iterator<JsonNode> priceElements = priceNode.elements();
            // while (elements.hasNext()) {
            //     JsonNode price = priceElements.next();
            //     System.out.println("Price = " + price.asLong());
            // }
        }
    }
}
