# Padel Club Website

Padel Club Website is a full-stack web application developed with both backend and frontend components. In this project, we implemented the backend API (Kotlin, http4k, PostgreSQL) and also developed the website’s frontend, creating a functional platform for padel club management. The web interface allows users to view and interact with all stored data (clubs, courts, rentals, and users) in real time. The frontend communicates directly with backend web APIs for all operations, providing dynamic, up-to-date information and seamless user interaction.

## Project Description

This platform allows club administrators to create users, define courts, and manage court rentals according to real-world club and court logic. Registered users can view available courts and make reservations (rentals) for specific dates and times. All stored data is accessible and manageable through user-friendly web pages, which are powered by backend APIs. The backend architecture enforces proper club, court, and rental relationships, ensuring that bookings are made only when courts are truly available. This project simulates how a real-world padel club might manage memberships, court scheduling, and rental operations in a scalable, maintainable, and API-driven way.

## Features

- **Club Management:** Create, update, delete, and list padel clubs.
- **Court Management:** Manage courts for each club.
- **Rental System:** Book, update, and manage court rentals with availability checks.
- **User Management:** Register and manage users.
- **Frontend Website:** User-friendly web interface for club, court, and rental management.
- **RESTful API:** Full-featured API for frontend or integration.
- **PostgreSQL Integration:** All data is stored securely in a PostgreSQL database.
- **Backend Testing:** Includes automated API/service tests (Java, JavaScript, and Postman).
- **Developed with:** Kotlin, http4k, Gradle.

## Technology Stack

- **Backend:** Kotlin, http4k
- **Frontend:** HTML, CSS, JavaScript
- **Database:** PostgreSQL
- **Build:** Gradle
- **Testing:** Java, JavaScript, Postman
- **Other:** Object-oriented design, modern service architecture

## Getting Started

### Prerequisites

- JDK 17+
- Gradle (wrapper included)
- PostgreSQL server

### Setup

1. **Clone the repository:**
    ```bash
    git clone https://github.com/yusufgoksu/Padel-Club-Website.git
    cd Padel-Club-Website
    ```

2. **Configure database connection:**  
   Edit the database configuration in `src/main/resources` or as environment variables as needed.

3. **Build and run the project:**
    ```bash
    ./gradlew build
    ./gradlew run
    ```

### Running Tests

- Use Postman collections or included Java/JavaScript files for API and backend testing.
- Automated tests can be run via Gradle:
    ```bash
    ./gradlew test
    ```

## Project Structure

- `src/main/kotlin/api/` — REST API endpoints
- `src/main/kotlin/models/` — Data models (User, Club, Court, Rental, etc.)
- `src/main/kotlin/services/` — Business logic and services
- `src/main/kotlin/DataBase/` — Database access logic
- `src/main/kotlin/server/` — Application entry points
- `src/main/resources/static/` — Frontend website files (HTML, CSS, JS)
- `README.md` — Project documentation

## License

This project is for educational purposes and is shared publicly for reference and learning.

---

*Developed as part of a university software laboratory project.*
