import java.lang.reflect.Array;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class Runner {

    public static void main(String... args) throws Exception {
        HttpRequest request = new HttpRequest("https://omsklotus.gemsdev.ru/api/lotus/newLotusDoc");
        request.setQuery("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<IsogdCoverLetter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <Num>Num0</Num>\n" +
                "    <DateDoc>2006-05-04T18:13:51.0Z</DateDoc>\n" +
                "    <Senders>\n" +
                "        <Sender>\n" +
                "            <SubjPerson>               \n" +
                "                <LastName>Lastname0</LastName>\n" +
                "                <FirstName>Name0</FirstName>\n" +
                "            </SubjPerson>\n" +
                "        </Sender>\n" +
                "    </Senders>\n" +
                "</IsogdCoverLetter>");
        request.addProperty("Content-Type", "application/xml");
        request.addProperty("authorization", "0fdbf21e69e34f7dab68bb207dbe45eb");
        sleep(10);
        System.out.println(request.InvokeHTTP_POST());
    }
}
