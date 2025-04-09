import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private ObjectInputStream sInput; // Stream to receive data from server
    private ObjectOutputStream sOutput; // Stream to send data to server
    private Socket socket; // Client socket
    private int numClients; // Number of clients connected to the server
    private double ownPart;  // Added to store the client's own part

    // Constructor to connect to server
    public Client(String server, int port) {
        try {
            // Connect to the specified server and port
            socket = new Socket(server, port);
            // Create output stream to send data to server
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            // Create input stream to receive data from server
            sInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create a new client instance and connect to localhost on port 1500
        Client client = new Client("localhost", 1500);
        Scanner scanner = new Scanner(System.in);

        // Wait for the server to send the number of clients
        try {
            client.numClients = (int) client.sInput.readObject(); // Read the number of clients from the server
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Prompt the user to enter their salary
        System.out.print("Enter your salary: ");
        double salary = scanner.nextDouble();

        // Decompose salary into n parts accurately
        List<Double> parts = new ArrayList<>(); // List to store the decomposed parts of the salary
        double sum = 0; // Sum of the parts so far
        Random random = new Random(); // Random number generator

        // Generate n-1 random parts of the salary
        for (int i = 0; i < client.numClients - 1; i++) {
            double part = random.nextDouble() * (salary - sum) / (client.numClients - i); // Calculate a random part
            parts.add(part); // Add the part to the list
            sum += part; // Update the sum
        }

        // Last part is the client's own part, which will not be sent to the server
        client.ownPart = salary - sum; // Calculate the remaining part as the client's own part
        parts.add(client.ownPart); // Add the own part to the list of parts

        // Display all parts (including the last part)
        for (Double part : parts) {
            System.out.println("part: " + part);
        }

        // Do not send the last part (own part) to the server, only the first n-1 parts
        List<Double> partsToSend = new ArrayList<>(parts.subList(0, parts.size() - 1));
        client.sendToServer(partsToSend); // Send the first n-1 parts to the server

        // Receive redistributed parts from the server and compute the sum
        double receivedSum = 0;
        try {
            List<Double> redistributedParts = (List<Double>) client.sInput.readObject(); // Read redistributed parts from the server
            for (Double part : redistributedParts) {
                receivedSum += part; // Add each part to the received sum
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Add the client's own part to the received sum
        System.out.println("Client's own part: " + client.ownPart);
        receivedSum += client.ownPart; // Add own part to the received sum

        // Display the total received sum (including own part)
        System.out.println("Received sum (including own part): " + receivedSum);

        // Send the computed sum back to the server
        client.sendToServer(receivedSum);

        // Receive the average salary from the server
        try {
            double averageSalary = (Double) client.sInput.readObject(); // Read the average salary from the server
            System.out.println("The average salary is: " + averageSalary); // Display the average salary
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Method to send an object to the server
    public void sendToServer(Object obj) {
        try {
            sOutput.writeObject(obj); // Write the object to the output stream
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
