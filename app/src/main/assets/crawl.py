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
url = 'https://vn.elsaspeak.com/tu-vung-toeic-thong-dung-theo-chu-de-thi-toeic-moi-nhat/'
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
sections = soup.find_all('h3', class_='wp-block-heading')
for section in sections:
	topic = section.get_text(strip=True)
	if (topic.find("(")==-1):
		continue
	print(topic)
	index+=1;
	if (index > 4):
		cursor.execute("INSERT INTO ChuDe (tenChuDe, hinhAnh, type) VALUES (?, ?, ?)", (topic, "tvchude_"+str(index), "chude"+str(index)))
	# Tìm table kế tiếp
	figure = section.find_next_sibling('figure', class_='wp-block-table')
	if figure:
		table = figure.find('table')
		if table:
			tbody = table.find('tbody')
			if tbody:
				rows = tbody.find_all('tr')

				for row in rows:
					columns = row.find_all(['td', 'th'])  # Phòng khi bảng dùng th
					if columns:
						vocab = columns[0].get_text(strip=True)
						transcript = columns[1].get_text(strip=True)
						meaning = columns[2].get_text(strip=True)
						if vocab.find("Từ vựng")!=-1 or vocab.strip()=="":
							continue
						print(f"{topic} - {vocab} - {transcript} - {meaning}")
						ok = vocab.split("(")
						if (len(ok)==1):
							cursor.execute("INSERT INTO TuVung (tiengAnh, phienAm, tiengViet, idChuDe, grouptv) VALUES (?, ?, ?, ?, ?)", (vocab.strip(), transcript, meaning, index, ""))

						else:
							print(ok)
							tiengAnh = ok[0].strip()
							grouptv = "("+ok[1].strip()
							
							cursor.execute("INSERT INTO TuVung (tiengAnh, phienAm, tiengViet, idChuDe, grouptv) VALUES (?, ?, ?, ?, ?)", (tiengAnh, transcript, meaning, index, grouptv))

conn.commit()
conn.close()