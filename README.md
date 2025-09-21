# 📱 Lost & Found App – PHCET College(MES Users)

> 🔍 A campus-exclusive Android app for reporting and finding lost or found items.  
> 🔒 Only accessible to MES students and faculty using institutional email.

---

## 🖼️ Overview

This Android application allows verified MES College users to report **lost** or **found** items and browse through reports submitted by others. The app integrates with **Firebase Realtime Database** for real-time updates and uses **Firebase Authentication** for secure access.

---

## ✅ Features Implemented

- 🔐 **User Authentication** – Only MES emails (e.g., `xyz@student.mes.ac.in`) can register and log in.
- 🖼️ **Splash Screen** – Clean intro screen while the app loads.
- 📝 **Register & Login** – Connected to Firebase Auth.
- 🏠 **Homepage** – Toolbar with Search, Notifications, and Profile icons.
- ⚙️ **Settings Page** – Allow user control over app behavior (dark mode, notifications, etc.)
- ☰ **Navigation Drawer** – Includes Home, Lost Items, Found Items, Settings, and Logout.
- ➕ **Floating Action Button** – Users can report items (Lost or Found) via dialog selection.
- 📤 **Report Forms** – Users can fill and submit details of lost/found items.
- 🔗 **Firebase Realtime Database** – For storing and retrieving reports.
- 🧾 **Lost & Found Segregation** – Improve UI to clearly separate lost vs found items.

---

## 💻 Tech Stack

- **Frontend**: Java + Android XML (Android Studio)
- **Backend**: Firebase (Auth + Realtime DB + Storage)
- **Design**: Material Components
