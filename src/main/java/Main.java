
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
        JSONArray fileIn = (JSONArray) parser.parse(Files.newBufferedReader(Paths.get(pathFileIn), Charset.forName("UTF-8")));
        JSONArray fileIn2 = (JSONArray) parser.parse(Files.newBufferedReader(Paths.get(pathFileIn2), Charset.forName("UTF-8")));
        JSONArray fileOut = (JSONArray) parser.parse(Files.newBufferedReader(Paths.get(pathFileOut), Charset.forName("UTF-8")));
        JSONArray fileOut2 = (JSONArray) parser.parse(Files.newBufferedReader(Paths.get(pathFileOut2), Charset.forName("UTF-8")));
        JSONArray fileArr, fileArr2;
        ChromeDriverManager.getInstance(DriverManagerType.CHROME).version("76.0.3809.126").setup();
        Configuration.startMaximized = true;
        open("https://camel.apache.org/components/latest/ldap-component.html");
        ElementsCollection rows = $$x("//div[@class=\"sect2\"][2]//tbody/tr");
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
            String[] data = element.split("\n") ;
            String[] names = data[0].split(" ");
            String nameEn = names.length==3 ? names[0]+names[1] : names[0];
            if (nameEn.equals("separator")) {
                System.out.println();
            }
            fileArr = fileIn;
            fileArr2 = fileIn2;
            current.put("nameEn", nameEn);
            if (names[1].equals("(producer)")) {
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
                        sleep(500);
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

            if (!names[1].equals("(consumer)")) producerArray.add(current);
            if (!names[1].equals("(producer)")) consumerArray.add(current);
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
