import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class CreateTopicTc1Test {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8080/";
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
  }

  @Test
  public void testCreateTopicTC1() throws Exception {
    driver.get(baseUrl + "/");
    driver.findElement(By.linkText("Login")).click();
    driver.findElement(By.id("signin:username")).clear();
    driver.findElement(By.id("signin:username")).sendKeys("user1");
    driver.findElement(By.id("signin:password")).clear();
    driver.findElement(By.id("signin:password")).sendKeys("123456");
    driver.findElement(By.id("signin:btnSignin")).click();
    driver.findElement(By.linkText("Categorie1")).click();
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    driver.findElement(By.id("CreateTopic:Title")).clear();
    driver.findElement(By.id("CreateTopic:Title")).sendKeys("MyTopic");
    driver.findElement(By.id("CreateTopic:Content")).clear();
    driver.findElement(By.id("CreateTopic:Content")).sendKeys("MyContent");
    driver.findElement(By.id("CreateTopic:Create")).click();
    assertTrue(driver.findElements(By.linkText("user1")).size() > 0);
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
