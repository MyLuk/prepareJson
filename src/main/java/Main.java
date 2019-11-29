
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.DriverManagerType;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.codeborne.selenide.Selenide.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        String pathFileIn = "C:\\Users\\wunsh\\Downloads\\project\\server\\fesb-manager\\web\\static\\src\\pages\\sops\\endpoint\\file\\properties\\InFTPEndpointProperties.json";
        String pathFileIn2 = "C:\\Users\\wunsh\\Downloads\\project\\server\\fesb-manager\\web\\static\\src\\pages\\sops\\endpoint\\file\\properties\\InFTPSEndpointProperties.json";
        String pathFileOut = "C:\\Users\\wunsh\\Downloads\\project\\server\\fesb-manager\\web\\static\\src\\pages\\sops\\endpoint\\file\\properties\\OutFTPEndpointProperties.json";
        String pathFileOut2 = "C:\\Users\\wunsh\\Downloads\\project\\server\\fesb-manager\\web\\static\\src\\pages\\sops\\endpoint\\file\\properties\\OutFTPSEndpointProperties.json";
        boolean blank = true;
        JSONParser parser = new JSONParser();
        JSONArray fileIn = new JSONArray();
        JSONArray fileIn2 = new JSONArray();
        JSONArray fileOut = new JSONArray();
        JSONArray fileOut2 = new JSONArray();
        if (!blank) {
             fileIn = (JSONArray) parser.parse(Files.newBufferedReader(Paths.get(pathFileIn), Charset.forName("UTF-8")));
             fileIn2 = (JSONArray) parser.parse(Files.newBufferedReader(Paths.get(pathFileIn2), Charset.forName("UTF-8")));
             fileOut = (JSONArray) parser.parse(Files.newBufferedReader(Paths.get(pathFileOut), Charset.forName("UTF-8")));
             fileOut2 = (JSONArray) parser.parse(Files.newBufferedReader(Paths.get(pathFileOut2), Charset.forName("UTF-8")));
        }
        JSONArray fileArr = new JSONArray();
        JSONArray fileArr2 = new JSONArray();
        ChromeDriverManager.getInstance(DriverManagerType.CHROME).version("76.0.3809.126").setup();
        Configuration.startMaximized = true;
        open("https://camel.apache.org/manual/latest/aggregate-eip.html");
        ElementsCollection rows = $$x("//div[@class=\"sect1\"][1]//tbody/tr");
        JSONObject consumer = new JSONObject();
        JSONArray consumerArray = new JSONArray();
        JSONObject producer = new JSONObject();
        JSONArray producerArray = new JSONArray();
        ArrayList<String> strings = new ArrayList<>();
        for (SelenideElement element: rows) {
            strings.add(element.getText());
        }
        open("https://translate.google.com/?hl=ru#view=home&op=translate&sl=en&tl=ru");
        for (String element: strings) {
            JSONObject current = new JSONObject();
            String[] data = element.split("\n");
            List<String> names = new ArrayList<String>();
            String[] namesAr = data[0].split(" ");
            Collections.addAll(names, namesAr);
            if (names.size() == 1) names.add("general");
            String nameEn = names.size()==3 ? names.get(0)+names.get(1) : names.get(0);
            if (!blank) {
                fileArr = fileIn;
                fileArr2 = fileIn2;
            }
            current.put("nameEn", nameEn);
            if (!blank && names.get(1).equals("(producer)")) {
                fileArr = fileOut;
                fileArr2 = fileOut2;
            }
            if (!blank && fileArr.stream().anyMatch(el -> ((JSONObject)el).get("nameEn").equals(nameEn))) {
                JSONObject object = (JSONObject) fileArr.stream().filter(el -> ((JSONObject)el).get("nameEn").equals(nameEn)).findAny().orElse(null);
                putCur(current, object);
            } else {
                if (!blank && fileArr2.stream().anyMatch(el -> ((JSONObject)el).get("nameEn").equals(nameEn))) {
                    JSONObject object = (JSONObject) fileArr2.stream().filter(el -> ((JSONObject) el).get("nameEn").equals(nameEn)).findAny().orElse(null);
                    putCur(current, object);
                } else {
                    current.put("nameRu", "");
                    if (data.length == 4) {
                        current.put("defaultValue", data[3].equals("boolean") ? Boolean.valueOf(data[2]) : data[2]);
                        current.put("type", getType(data[3]));
                        $x("//textarea[@id='source']").clear();
                        $x("//textarea[@id='source']").sendKeys(data[1]);
                        sleep(1500);
                        String text = $x("//span[@class='tlid-translation translation']").getText();
                        current.put("descriptionEnTRANS", text);
                        current.put("description", text);
                    } else {
                        current.put("defaultValue", null);
                        current.put("type", getType(data[2]));
                        $x("//textarea[@id='source']").clear();
                        $x("//textarea[@id='source']").sendKeys(data[1]);
                        sleep(1000);
                        String text = $x("//span[@class='tlid-translation translation']").getText();
                        current.put("descriptionEnTRANS", text);
                        current.put("description", text);
                    }
                }
            }
            current.put("descriptionEn", data[1]);

            if (!names.get(1).equals("(consumer)")) producerArray.add(current);
            if (!names.get(1).equals("(producer)")) consumerArray.add(current);
        }

        // Create a new FileWriter object
        FileWriter consumerFile = new FileWriter("consumer.json");
        FileWriter producerFile = new FileWriter("producer.json");

        // Writting the jsonObject into sample.json
        consumerFile.write(consumerArray.toJSONString());
        producerFile.write(producerArray.toJSONString());
        consumerFile.close();
        producerFile.close();
    }

    private static void putCur(JSONObject current, JSONObject object) {
        current.put("nameRu", object.get("nameRu"));
        current.put("description", object.get("description"));
        current.put("defaultValue", object.get("defaultValue"));
        current.put("type", object.get("type"));
        if (object.get("type").equals("SELECT")) current.put("acceptableValues", object.get("acceptableValues"));
    }

    private static String getType(String datum) {
        String type;
        switch (datum) {
            case "boolean":
                type = "BOOL";
                break;
            case "String":
                type = "STRING";
                break;
            case "int":
                type = "INT";
                break;
            case "Integer":
                type = "INT";
                break;
            case "long":
                type = "INT";
                break;
            default:
                type = "BEAN";
        }
        return type;
    }

}
