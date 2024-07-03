import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegexTest {

    @Test
    public void test(){
        String login = "aaaaaa/login";
        System.out.println(login.matches(".*/login.*"));
    }

}
