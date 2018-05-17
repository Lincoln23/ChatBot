package com.company;

import java.io.*;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Main {


    public static void main(String[] args) {
        String text = "what is Lincoln's phone number who works work Amazon";

        List<Entity> list;
        Map<String, String> map = new HashMap<>(); // key String: text , Value Map: type


        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();

        AmazonComprehend comprehendClient =
                AmazonComprehendClientBuilder.standard()
                        .withCredentials(awsCreds)
                        .withRegion(Regions.US_EAST_1)
                        .build();

        // Call detectKeyPhrases API
        System.out.println("Calling DetectEntities");
        DetectEntitiesRequest detectEntitiesRequest = new DetectEntitiesRequest().withText(text)
                .withLanguageCode("en");
        DetectEntitiesResult detectEntitiesResult = comprehendClient.detectEntities(detectEntitiesRequest);
        list = detectEntitiesResult.getEntities();
        System.out.println(list + "\n");

        System.out.println("End of DetectEntities\n");

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getScore() > 0.9) {
                //System.out.println(list.get(i).getText());
                map.put(list.get(i).getText().replaceAll("[,]", ""), list.get(i).getType());
            }
        }

        JSONObject obj = new JSONObject();
        for (String key : map.keySet()) {
            try {
                obj.put(map.get(key), key);
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }

        }

        String jsonStr = obj.toString();
        System.out.println(obj);

        Connection client = new Connection();

        try{
            String whatis = client.post("http://fastchat/api/customer/select", jsonStr);
            System.out.println(whatis);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }


        try {
            String result = client.run("http://fastchat/api/customer/Lincoln");
            //System.out.println(result);
        } catch (IOException e) {
           // System.out.println(e.getMessage());
        }

    }
}
