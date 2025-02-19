package base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;

public class FileUtilLockDemo extends TestCase {

    public static ObjectMapper objectMapper = new ObjectMapper();

    // 正则表达式，匹配 "dbAdGroupId":"xxx"
    static String regex = "dbAdGroupId";

    // 编译正则表达式
    static Pattern pattern = Pattern.compile(regex);



    public void testReadContent() {
        try {
            String reportContent = FileUtil.readContent("files/logs.json");
            JsonNode jsonNode = objectMapper.readTree(reportContent);
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode chile = it.next();
                readLogsNode(chile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readLogsNode(JsonNode jsonNode) throws JsonProcessingException {
        String msg = jsonNode.get("message").toString();
        msg = msg.split("Parameters: ")[1];
        msg = StringEscapeUtils.unescapeJava(msg);
        JsonNode msgNode = objectMapper.readTree(msg);
        for (Iterator<JsonNode> it = msgNode.elements(); it.hasNext(); ) {
            JsonNode adGroups = it.next();
            for(Iterator<JsonNode> itj = adGroups.elements(); itj.hasNext(); ){
                JsonNode adGroup = itj.next();
                String profileId = jsonNode.get("profileId").toString();
                System.out.print(profileId + "\t");
                String id = adGroup.get("dbAdGroupId").toString();
                String updateFields = adGroup.get("updateFields").toString();
                System.out.print(id + "\t");
                System.out.print(updateFields + "\t");
                String userName = jsonNode.get("userName").toString();
                System.out.println(userName + "\t");
            }
        }


    }

    public void testCreateFile() {
        try {
            File file = FileUtil.createFile("/files/asd/report.txt");
            System.out.println(file.exists());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}