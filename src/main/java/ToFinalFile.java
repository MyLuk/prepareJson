import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ToFinalFile {

    public static void main(String[] args) throws IOException, ParseException {
//        String pathFileIn = "C:\\Users\\wunsh\\IdeaProjects\\spring-jms-master\\prepareJson\\consumer.json";
        String pathFileIn = "C:\\Users\\wunsh\\IdeaProjects\\spring-jms-master\\prepareJson\\producer.json";
        JSONParser parser = new JSONParser();
        JSONArray fileIn = (JSONArray) parser.parse(Files.newBufferedReader(Paths.get(pathFileIn), Charset.forName("UTF-8")));
        JSONArray array = new JSONArray();
        for (Object object: fileIn) {
            JSONObject el = (JSONObject) object;
            JSONObject current = new JSONObject();
            current.put("nameRu", el.get("nameRu"));
            current.put("nameEn", el.get("nameEn"));
            current.put("defaultValue", el.get("defaultValue"));
            current.put("description", el.get("description"));
            current.put("type", el.get("type"));
            if (el.get("type").equals("SELECT")) current.put("acceptableValues", el.get("acceptableValues"));
            array.add(current);
        }
//        FileWriter consumerFile = new FileWriter("consumerFinal.json");
        FileWriter consumerFile = new FileWriter("producerFinal.json");
        consumerFile.write(array.toJSONString());
        consumerFile.close();
    }

}
