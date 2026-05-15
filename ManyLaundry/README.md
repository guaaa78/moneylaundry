# 🧺 Many Laundry — Management System

A Java Swing POS-style laundry shop management application.

## 📁 Project Structure

```
ManyLaundry/
├── src/
│   ├── Main.java          ← Entry point
│   ├── Machine.java       ← Machine model (AVAILABLE / OCCUPIED)
│   ├── Order.java         ← Order model + pricing + validation logic
│   ├── LaundrySystem.java ← Core business logic controller
│   ├── Receipt.java       ← Kiosk-style receipt generator
│   └── DashboardUI.java   ← Full Java Swing GUI
├── compile_and_run.sh     ← Quick build script (Linux/Mac)
└── README.md
```

## ▶️ How to Run

### Requirements
- Java JDK 11 or higher (JDK, not just JRE)

### Steps

**Linux / macOS:**
```bash
cd ManyLaundry
chmod +x compile_and_run.sh
./compile_and_run.sh
```

**Windows:**
```cmd
cd ManyLaundry
mkdir out
javac -d out src\*.java
java -cp out Main
```

**From an IDE (IntelliJ / Eclipse / VS Code):**
1. Open the `src/` folder as source root
2. Run `Main.java`

## 💡 Features

| Feature | Description |
|---|---|
| 🏠 Dashboard | Live stats: pending/completed/cancelled orders, free machines |
| ➕ New Order | Customer name + service type + weight → auto-assigns machine |
| 📋 All Orders | Table view with complete/cancel/receipt actions |
| 🌀 Machines | Visual card-based status of all 5 washing machines |
| 🔍 Search | Find any order by ID, then act on it |
| 🧾 Receipt | Kiosk-style digital receipt popup |

## 💰 Service Pricing

| Service | Price |
|---|---|
| DIY Wash – Lite (4kg, 22min) | ₱55 |
| DIY Wash – Regular (8kg, 36min) | ₱75 |
| DIY Dry – Lite (4kg, 25min) | ₱45 |
| DIY Dry – Regular (8kg, 40min) | ₱65 |
| Full Service – Lite (<4kg) | ₱130 |
| Full Service – Regular (5–8kg) | ₱170 |
| Comforter – Small | ₱175 |
| Comforter – Medium | ₱375 |
| Comforter – Large | ₱600 |
| Barong / Blazer | ₱400 |
| Gown | ₱1,350 |
| Sneakers | ₱475 |

## 📌 Business Rules
- Weight must match the selected service tier (enforced)
- Maximum 5 washing machines; machine auto-assigned
- Special items (comforters, gowns, etc.) don't use machines
- Cannot complete a cancelled order, cannot cancel a completed order

## 🏗️ OOP Structure
- **Encapsulation**: All fields private, accessed via getters/setters
- **Separation of concerns**: UI (DashboardUI) is fully separate from logic (LaundrySystem)
- **String constants**: Used throughout instead of enums
- **ArrayList**: Orders and machines stored in ArrayLists
