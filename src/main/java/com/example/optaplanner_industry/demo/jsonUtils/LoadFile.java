package com.example.optaplanner_industry.demo.jsonUtils;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.example.optaplanner_industry.demo.domain.Input;
import com.example.optaplanner_industry.demo.domain.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LoadFile {


    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFile.class);


    private static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty, // 字符类型字段如果为null，输出为""，而不是null

            //开发环境调试使用，线上环境请取消,仅是格式化输出json设置，会输出太多无用空格
            SerializerFeature.PrettyFormat
    };


    public static void writeJsonFile(Object output, String path) {
        String jsonString = convertObjectToJSON(output); // job转string
        write(jsonString, path);
    }

    public static String convertObjectToJSON(Object object) {
        return JSON.toJSONString(object);
    }

    public static void write(String str, String path) {


        try (FileWriter writer = new FileWriter(path)) {
            writer.write("");//清空原文件内容
            writer.write(str);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void saveJson(String filePath) {
        String writeString = JSON.toJSONString(SerializerFeature.PrettyFormat);
        BufferedWriter writer = null;
        File file = new File(filePath);
        //如果多次执行同一个流程，会导致json文件内容不断追加，在写入之前清空文件
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8));
            writer.write("");
            writer.write(writeString);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    // 用于读取JSON文件
    public static Input readJsonFile(String filePath) {

        StringBuilder readJson = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(LoadFile.class.getClassLoader()
                        .getResourceAsStream(filePath)), StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String tempString;
            while ((tempString = reader.readLine()) != null) {
                readJson.append(tempString);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        // 获取json
        Input jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(readJson.toString(), Input.class);
//            System.out.println(jsonObject.getResourcePool());
//            JSONArray objects = JSONArray.parseArray(jsonObject.getResourcePool());
//            List<JSONObject> jsonObjects = objects.toJavaList(JSONObject.class);
//            jsonObjects.forEach(i->System.out.println(i.get("id")));


//            LOGGER.info(JSON.toJSONString(jsonObject));
        } catch (JSONException e) {
            LOGGER.error(e.getMessage());
        }
        return jsonObject;
    }


}


