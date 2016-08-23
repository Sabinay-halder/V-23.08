package com.widevision.pillreminder.mailSending;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mercury-one on 6/2/16.
 */
public class JsonParse {
    public JsonParse(){}

    public List<String> getParseJsonWCF(String sName)
    {
        List<String> ListData = new ArrayList<>();
        try {
            String temp=sName.replace(" ", "%20");
            URL js = new URL("http://www.truemd.in/api/medicine_suggestions?id="+temp+"&key=480aec3cf84ebcbb6a2d0a44d494b5");
            //URL js = new URL("http://mapi-us.iterar.co/api/autocomplete?query="+temp);
            URLConnection jc = js.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            String line = reader.readLine();
            JSONObject jsonResponse = new JSONObject(line);
            JSONObject jsonResponse1 = new JSONObject(jsonResponse.getString("response"));
            JSONArray jsonArray = jsonResponse1.getJSONArray("suggestions");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject contact = jsonArray.getJSONObject(i);
                String name = contact.getString("suggestion");
                String r = jsonArray.getString(i);
                ListData.add(name);
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return ListData;

    }

}

