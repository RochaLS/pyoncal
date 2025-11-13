# 🐣 PyonCal  
**Import work shifts into Google Calendar using just a screenshot.**  
Currently in closed testing — access available only to pre-approved users.

## ⭐ About PyonCal  
PyonCal is a lightweight web app that turns any screenshot of your work schedule into real events in your Google Calendar.  

Using AI, PyonCal extracts shift details automatically, so you never need to manually type your schedule again.


## 🎯 Why I Built This  
Manually entering shifts into Google Calendar is slow, annoying, and repetitive.  
Most workplaces post schedules as screenshots anyway, so I wanted a simple tool that could:

- Read the screenshot  
- Add them directly to Google Calendar  

PyonCal exists to make scheduling faster and completely frictionless.


## 🖥️ Screenshots  
![UI Screenshot](https://raw.githubusercontent.com/RochaLS/PyonCal/master/pyoncal-prev.png)
![UI Screenshot](https://raw.githubusercontent.com/RochaLS/PyonCal/master/pyoncal-prev2.png)


## 🛠️ Tech Stack  

**Backend & Frontend:**  
- Java Spring Boot  
- Thymeleaf (Server-Side Rendering)  
- Spring Security (Google OAuth2)

**AI Integration:**  
- Gemini API (Screenshot → Shift extraction)

**Calendar Automation:**  
- Google Calendar API  
- Google OAuth2 (Secure account connection)

**Deployment:**  
- Railway (Hosting)


## 🧠 How It Works  
1. Sign in with your Google account  
2. Upload your work schedule screenshot  
3. PyonCal uses the **Gemini API** to identify shifts  
4. Import everything directly into Google Calendar 🎉


## 🔐 Architecture Overview  
- Spring Boot backend serving Thymeleaf pages  
- Gemini API processes schedule screenshots  
- Google OAuth2 handles authentication  
- Google Calendar API creates events  
- Hosted on Railway  


## 🧩 Features  
- Google login (OAuth2)  
- AI-powered shift extraction  
- Automatic Google Calendar import 
- Clean and minimal UI  


## 🚧 Upcoming Features  
- Multi-week screenshot support  
- Better duplicate detection  
- Mobile-responsive UI  


## 🔓 Availability  
PyonCal is currently in **closed testing**.  
Only pre-approved users can access the web app while the import system is refined.


## 📬 Contact  
If you’d like early access or want to share feedback:  
**Email:** ld.rocha@hotmail.com
