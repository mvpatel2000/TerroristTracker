from bs4 import BeautifulSoup
import selenium.webdriver as webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

with open("users.txt") as f:
    users = f.readlines()

g = open("flagged.txt", "a+")
h = open("notflagged.txt", "a+")

g.seek(0)
s = g.read()
h.seek(0)
r = h.read()
formerusers = r.splitlines() + s.splitlines()

driver = webdriver.Firefox()

for thisUser in users:
    if thisUser.rstrip() not in formerusers:
        formerusers.append(thisUser)
        url = "https://instagram.com/" + thisUser
        driver.get(url)
        flag = input('Flag?')
        if(flag=="y"):
            g.write(thisUser)
        elif (flag=="quit"):
            break
        else:
            h.write(thisUser)
    else:
        print ("all dank in detroit")
f.close()
g.close()
h.close()