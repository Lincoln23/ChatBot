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
        String text = "What is Lincoln's phone number who works for Amazon";

        List<Entity> list;
        List<KeyPhrase> keyList;
        Map<String, String> map = new HashMap<>(); // key String: text (use this because key cannot be duplicate) ,
        // Value Map: type


        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();

        AmazonComprehend comprehendClient =
                AmazonComprehendClientBuilder.standard()
                        .withCredentials(awsCreds)
                        .withRegion(Regions.US_EAST_1)
                        .build();


        System.out.println("Detecting");
        // Call detectEntities API
        DetectEntitiesRequest detectEntitiesRequest = new DetectEntitiesRequest().withText(text)
                .withLanguageCode("en");
        // Call detectKeyPhrases API
        DetectKeyPhrasesRequest detectKeyPhrasesRequest = new DetectKeyPhrasesRequest().withText(text)
                .withLanguageCode("en");
        //Entities Result
        DetectEntitiesResult detectEntitiesResult = comprehendClient.detectEntities(detectEntitiesRequest);
        //KeyPhrases Result
        DetectKeyPhrasesResult detectKeyPhrasesResult = comprehendClient.detectKeyPhrases(detectKeyPhrasesRequest);

        keyList = detectKeyPhrasesResult.getKeyPhrases();
        list = detectEntitiesResult.getEntities();

        System.out.println(keyList);
        System.out.println(list + "\n");

        System.out.println("Finish Detecting\n");

        //put the Entities Result into a Hashmap to obtain the relevant information
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getScore() > 0.9) {
                //System.out.println(list.get(i).getText());
                map.put(list.get(i).getText().replaceAll("[,]", ""), list.get(i).getType());
            }
        }
        // putting the keyPhrases into the Hashmap and ignoring values that are already there
        for (int i = 0; i < keyList.size(); i++) {
                if (map.containsKey(keyList.get(i).getText())){
                    continue;
                }
                map.put(keyList.get(i).getText(),"Type");
        }

        System.out.println(map);

        // converting the map into a JSON object since that is what the PHP API POST request requires
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

        //HTTP connect to API
        Connection client = new Connection();

        try{
            String whatis = client.post("http://fastchat/api/customer/select", jsonStr);
            System.out.println(whatis);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
