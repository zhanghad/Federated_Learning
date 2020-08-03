package com.fedserver;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.nd4j.shade.jackson.databind.JsonNode;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class test1 {

    public static void main(String[] args) throws IOException {

/*        Map<String, String> testDict = new HashMap<>();
        ObjectMapper M = new ObjectMapper();
        String json = "";
        JsonNode deJson = null;

        testDict.put("name", "piperck");
        testDict.put("age", "18");
        testDict.put("company", "NIO");

        System.out.println(testDict);

// MAP COVERT TO JSON
        try {
            json = M.writeValueAsString(testDict);
        } catch (JsonProcessingException e) {
            System.out.println(e.getLocalizedMessage());
        }
        System.out.println(json);


        try {
            Map<String, String> map = M.readValue(json, Map.class);
            System.out.println(map);
        } catch(IOException e) {
            System.out.println(e.getLocalizedMessage());
        }*/


        Map<String, byte[]> testDict = new HashMap<>();
        ObjectMapper M = new ObjectMapper();
        String json = "";
        JsonNode deJson = null;

        testDict.put("name", Nd4j.toByteArray(Nd4j.ones(1,3)));
        testDict.put("age", Nd4j.toByteArray(Nd4j.ones(1,3)));
        testDict.put("company", Nd4j.toByteArray(Nd4j.ones(1,3)));

        System.out.println(testDict);

// MAP COVERT TO JSON
        try {
            json = M.writeValueAsString(testDict);
        } catch (JsonProcessingException e) {
            System.out.println(e.getLocalizedMessage());
        }
        System.out.println(json);


        try {
            Map<String, byte[]> map = M.readValue(json, Map.class);
            System.out.println(map);
            System.out.println(Nd4j.fromByteArray(map.get("name")));
        } catch(IOException e) {
            System.out.println(e.getLocalizedMessage());
        }


    }

}
