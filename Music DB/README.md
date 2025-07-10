# 📁 Music Database Management Project

This project simulates the schema and the functionality of a music database system using PostgreSQL. The project includes ER modeling, schema creation, CSV data import, and complex SQL queries.

---

## 📌 Project Overview

The database models a music store with customers, invoices, invoice lines, tracks, albums, and artists. It follows relational principles and uses real-world-style CSV datasets to populate the tables.

---

## 🛠️ Technologies Used

- PostgreSQL
- SQL
- CSV 
- pgAdmin (for executing SQL and managing tables)

---

## 🗃️ Database Schema

### 👥 Entities & Relationships

- **Customer ↔ Invoice**: One-to-many (a customer has one or more invoices; invoices are paid by one customer).
- **Invoice ↔ InvoiceLine**: One-to-many (an invoice has multiple lines).
- **InvoiceLine ↔ Track**: One-to-one (each invoice line refers to a specific track).
- **Track ↔ Album**: Many-to-one (a track belongs to a single album).
- **Album ↔ Artist**: Many-to-many (an album can be created by one or more artists; an artist can have many albums).