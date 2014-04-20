

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
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testSignUpTc1() throws Exception {
    driver.get(baseUrl + "/");
    driver.findElement(By.linkText("Login")).click();
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    driver.findElement(By.id("signup:username")).clear();
    driver.findElement(By.id("signup:username")).sendKeys("user2435332334433");
    driver.findElement(By.id("signup:email")).clear();
    driver.findElement(By.id("signup:email")).sendKeys("user343345364242@user1.com");
    driver.findElement(By.id("signup:password")).clear();
    driver.findElement(By.id("signup:password")).sendKeys("123456");
    driver.findElement(By.id("signup:confirm")).clear();
    driver.findElement(By.id("signup:confirm")).sendKeys("123456");
    //Thread.sleep(1000);
    
    driver.findElement(By.name("signup:btnSignup")).click();
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    //Thread.sleep(100000);
    /*try{
    WebElement web = driver.findElement(By.id("messagePanel"));
    }
    catch(Exception e)
    {
        String ee = e.getMessage();
        System.out.println("");
    }*/
    //WebElement web2 =  web.findElement(By.tagName("td"));
    //String text =web2.getText();//.trim();
    /*WebElement baseTable = driver.findElement(By.className("table gradient myPage"));
    List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
    tableRows.get(index).getText();
    assertTrue(text.equals("User was successfully created."));*/
    
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
