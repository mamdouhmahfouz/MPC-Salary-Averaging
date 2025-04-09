# Secure Multiparty Computation for Average Salary Calculation

## Overview
This project implements a Secure Multiparty Computation (MPC) application using Java, designed to preserve the privacy of individuals' salary information. The goal is to compute the average salary of all employees without revealing any individual's salary to anyone else. The solution uses a client-server architecture based on Java socket programming to facilitate secure communication between participants.

The assignment was a part of the ICS 505 Cryptography course, which aimed to give hands-on experience in implementing cryptographic protocols that ensure data privacy even when multiple parties are involved in the computation.

## Problem Statement
The task involves calculating the average salary of `n` users in a way that preserves the privacy of each individual user. Each user decomposes their salary into `n` unequal parts and distributes these parts among other users. Each user then receives parts of other users' salaries, along with a part of their own salary, and performs a specific computation. The results of these computations are then forwarded to the server, which ultimately calculates the average salary.

The server in this setup acts solely as a gateway for communication, ensuring that it does not collect or have access to any salary information.

## Features Implemented
### 1. Client-Server Architecture
- Implemented a basic client-server setup using Java sockets.
- The server acts as a facilitator, ensuring communication between users, but it does not store or process any salary information on its own.
- Multiple clients can connect to the server, representing different users.

### 2. Salary Decomposition
- Each user decomposes their salary into `n` unequal parts.
- These parts are then shared with other users through the server.

### 3. Secure Multiparty Computation
- Each user receives parts of other users' salaries and keeps one part of their own.
- A secure computation is carried out by each user on the received parts.
- The users then send their computed values back to the server.

### 4. Average Calculation
- The server collects the computed values from all users and calculates the average salary without learning any individual's salary.

## Code Structure
- **Client.java**: This file implements the client-side logic. Each client represents a user in the MPC system.
  - Connects to the server and decomposes the user's salary into parts.
  - Receives parts from other users and computes a partial result.
  - Sends the computed result back to the server.

- **Server.java**: This file implements the server-side logic, which is responsible for handling multiple client connections.
  - Facilitates the distribution of salary parts among clients.
  - Collects computed results from all clients and calculates the average salary.
  - Acts only as a mediator, ensuring that no private salary information is exposed.

## How to Run the Code
1. **Clone the Repository**: Clone the repository containing the solution from GitHub:
   ```
   git clone https://github.com/YourUsername/YourRepository.git
   ```
2. **Compile the Java Files**: Use the following commands to compile the Java files:
   ```
   javac Server.java
   javac Client.java
   ```
3. **Run the Server**: Start the server first:
   ```
   java Server
   ```
4. **Run the Clients**: Start multiple clients (each in a different terminal or console window):
   ```
   java Client
   ```

## Technologies Used
- **Java**: The solution was implemented in Java, using socket programming for client-server communication.
- **Cryptography Concepts**: Secure multiparty computation to ensure privacy during salary averaging.

## Challenges Faced
- **Privacy Preservation**: Ensuring that individual salary data remains private while still allowing for a meaningful computation was challenging. This was achieved through careful decomposition of salaries and secure message passing.
- **Client-Server Synchronization**: Managing communication between multiple clients and ensuring that each user received the correct parts of salary data without deadlocks or delays.

## Future Improvements
- **Encryption**: Adding encryption to the communication between clients and the server to provide an additional layer of security.
- **Dynamic Number of Users**: Allowing dynamic joining or leaving of users during the computation process.
- **Improved Fault Tolerance**: Handling client disconnections gracefully to ensure the computation can still proceed with available data.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Contact
For any questions or suggestions, feel free to contact:
- Dr. Muhammad Hataba (Instructor): [muhammad.hataba@giu-uni.de](mailto:muhammad.hataba@giu-uni.de)
- TA John Ehab: [john.ehab@giu-uni.de](mailto:john.ehab@giu-uni.de)
