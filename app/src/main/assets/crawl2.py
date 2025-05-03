import sqlite3
from bs4 import BeautifulSoup
import re

# Kết nối đến file data.db (nếu chưa có thì SQLite sẽ tự tạo)
conn = sqlite3.connect('LearningEnglish.db')

# Tạo một đối tượng cursor để thực thi câu lệnh SQL
cursor = conn.cursor()


from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
import time

# Đường dẫn đến tệp chromedriver
chromedriver_path = 'D:/selenium driver/chromedriver-win64d/chromedriver-win64/chromedriver.exe'  # Thay bằng đường dẫn thực tế

# Khởi tạo trình điều khiển Chrome
service = Service(executable_path=chromedriver_path)
driver = webdriver.Chrome(service=service)

# Truy cập trang web
url = 'https://vn.elsaspeak.com/bang-360-dong-tu-bat-quy-tac-trong-tieng-anh/'
driver.get(url)

# Chờ tải trang
time.sleep(1)

# Lấy HTML sau khi trang đã render đầy đủ
html = driver.page_source
driver.quit()

# Phân tích HTML bằng BeautifulSoup
soup = BeautifulSoup(html, 'html.parser')

# Tạo từ điển lưu kết quả
vocab_data = {}
index = 0
# Tìm tất cả h3 có class wp-block-heading
sections = soup.find('figure', class_='wp-block-table')
if sections:
	topic = sections.get_text(strip=True)

	# Tìm table kế tiếp
	table = sections.find('table', class_='has-fixed-layout')
	if table:
		tbody = table.find('tbody')
		if tbody:
			rows = tbody.find_all('tr')

			for row in rows:
				index+=1
				if index==1:
					continue
				
				columns = row.find_all(['td', 'th'])  # Phòng khi bảng dùng th
				if columns:
					v1 = columns[0].get_text(strip=True)
					v2 = columns[1].get_text(strip=True)
					v3 = columns[2].get_text(strip=True)
					mean = columns[3].get_text(strip=True)
					print(f'{v1} {v2} {v3} {mean}')
					cursor.execute("INSERT INTO BatQuyTac (v1, v2, v3, mean) VALUES (?, ?, ?, ?)", (v1, v2, v3, mean))


conn.commit()
conn.close()