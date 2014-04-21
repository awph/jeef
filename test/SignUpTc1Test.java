

import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SignUpTc1Test {
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
  public void testSignUpTc1() throws Exception {
    driver.get(baseUrl + "/");
    driver.findElement(By.linkText("Login")).click();
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    driver.findElement(By.id("signup:username")).clear();
    driver.findElement(By.id("signup:username")).sendKeys("user2");
    driver.findElement(By.id("signup:email")).clear();
    driver.findElement(By.id("signup:email")).sendKeys("user2@user2.com");
    driver.findElement(By.id("signup:password")).clear();
    driver.findElement(By.id("signup:password")).sendKeys("123456");
    driver.findElement(By.id("signup:confirm")).clear();
    driver.findElement(By.id("signup:confirm")).sendKeys("123456");
    //Thread.sleep(1000);
    
    driver.findElement(By.name("signup:btnSignup")).click();
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    assertTrue("User was successfully created.".equals(driver.findElement(By.id("messagePanel")).findElement(By.tagName("td")).getText().trim()));
    
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
