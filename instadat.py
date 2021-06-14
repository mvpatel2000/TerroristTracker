from bs4 import BeautifulSoup
import selenium.webdriver as webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from pyvirtualdisplay import Display


userdata = []
user = []
arr = []
user.append('mohnd.mo')
userNumber = 1;
g = open("users.txt", "a+")
g.seek(0)
s = g.read()
formerusers = s.splitlines()
print(s)

driver = webdriver.Firefox()
url = 'https://instagram.com/accounts/login'
driver.get(url)
driver.implicitly_wait(3)
username = driver.find_element_by_name('username')
password = driver.find_element_by_name('password')
username.send_keys('*username*')
password.send_keys('*password*')
driver.find_element_by_xpath("//button[contains(.,'Log in')]").click()
element = WebDriverWait(driver, 20).until(EC.presence_of_element_located((By.CLASS_NAME, "_9sg67")))


while (len(user) > 0):

    notPrivate = True;

    currentUser = user.pop()
    formerusers.append(currentUser);
    url = 'https://instagram.com/' + currentUser
    driver.get(url)

    soup = BeautifulSoup(driver.page_source, "html.parser")

# Click the 'Follower(s)' link
    WebDriverWait(driver, 1).until(EC.presence_of_element_located((By.CLASS_NAME, "_5axto")))
    driver.implicitly_wait(0)
    private = driver.find_elements_by_partial_link_text("follower")
    if len(private) > 0:
        private[0].click()
    else:
        notPrivate = False;

    if notPrivate:
        g.write(currentUser + '\n')
        f = open("Data/" + currentUser + ".txt", "w")
        data = driver.find_elements_by_class_name('_218yx')
        userdata.append(data)

        for d in data:
            f.write(d.text + '\n')
            print(d.text)
        if userNumber < 10:
            # Wait for the followers modal to load
            xpath = "//div[@style='position: relative; z-index: 1;']/div/div[2]/div/div[1]"
            WebDriverWait(driver, 1).until(
                EC.presence_of_element_located((By.XPATH, xpath)))

            # http://stackoverflow.com/questions/38040300/scraping-instagram-followers-page-using-selenium-and-python
            # Finally, scrape the followers
            xpath = "//div[@style='position: relative; z-index: 1;']//ul/li/div/div/div/div/a"
            followers_elems = driver.find_elements_by_xpath(xpath)
            for e in followers_elems:
                if (e.text not in user) and (e.text not in formerusers):
                    user.append(e.text)
                else:
                    arr.append(e.text)
                print(e.text)
            f.close()
        print(userNumber)
        userNumber += 1

# searching the followers of people we've already seen, because chances are we haven't seen their followers
while len(arr) > 0:

    notPrivate = True;

    currentUser = arr.pop()
    url = 'https://instagram.com/' + currentUser
    driver.get(url)

    soup = BeautifulSoup(driver.page_source, "html.parser")

# Click the 'Follower(s)' link
    WebDriverWait(driver, 1).until(EC.presence_of_element_located((By.CLASS_NAME, "_5axto")))
    driver.implicitly_wait(0)
    private = driver.find_elements_by_partial_link_text("follower")
    if len(private) > 0:
        private[0].click()
    else:
        notPrivate = False;

    if notPrivate:
        g.write(currentUser + '\n')
        f = open("Data/" + currentUser + ".txt", "w")
        data = driver.find_elements_by_class_name('_218yx')
        userdata.append(data)

        for d in data:
            f.write(d.text + '\n')
            print(d.text)
        if userNumber < 10:
            # Wait for the followers modal to load
            xpath = "//div[@style='position: relative; z-index: 1;']/div/div[2]/div/div[1]"
            WebDriverWait(driver, 1).until(
                EC.presence_of_element_located((By.XPATH, xpath)))

            # http://stackoverflow.com/questions/38040300/scraping-instagram-followers-page-using-selenium-and-python
            # Finally, scrape the followers
            xpath = "//div[@style='position: relative; z-index: 1;']//ul/li/div/div/div/div/a"
            followers_elems = driver.find_elements_by_xpath(xpath)
            for e in followers_elems:
                if (e.text not in user) and (e.text not in formerusers):
                    user.append(e.text)
                print(e.text)
            f.close()
        print(userNumber)
        userNumber += 1
g.close()
