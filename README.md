# Onymos
# Stock Order Matching System

This project implements a **Stock Order Matching System** in **Java**, simulating a real-time stock exchange.  
It efficiently processes **Buy and Sell orders** using a **lock-free, non-blocking approach** for high-performance trading.

## 📌 Features
- **Efficient Order Matching**: Matches buy and sell orders in **O(n) time complexity**.
- **Multi-threaded Safe**: Uses **AtomicReference** to prevent race conditions.
- **Lock-Free Data Structures**: Implements linked lists instead of dictionaries/maps.
- **Real-Time Execution**: Orders are dynamically added and matched.
- **Simulated Stock Market**: Supports **1,024 tickers** and processes **1,000+ orders**.

## 🛠️ Installation & Running the Code
### **1️⃣ Clone the Repository**
```sh
git clone https://github.com/RuthvikReddyASU/Onymos.git
cd Onymos
