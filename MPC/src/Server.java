import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private int port; // Port on which the server will listen for connections
    private int numClients; // Number of clients expected to connect
    private List<ClientThread> clientThreads = new ArrayList<>(); // List to store threads for each client

    // Constructor to set the server's port
    public Server(int port) {
        this.port = port;
    }

    // Method to start the server
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Enter the number of expected clients: ");
            Scanner scanner = new Scanner(System.in);
            numClients = scanner.nextInt(); // Get the number of clients from the user

            System.out.println("Server waiting for clients on port " + port);
            // Accept connections until all clients are connected
            while (clientThreads.size() < numClients) {
                Socket socket = serverSocket.accept(); // Accept a new client connection
                ClientThread clientThread = new ClientThread(socket, clientThreads.size() + 1); // Create a new thread for the client
                clientThreads.add(clientThread); // Add the thread to the list
                clientThread.start(); // Start the client thread
                System.out.println("Client " + clientThread.getId() + " connected.");
            }

            System.out.println("All clients connected. No longer accepting new connections.");

            // Notify clients of the number of clients
            for (ClientThread ct : clientThreads) {
                ct.sendNumberOfClients(numClients); // Send the number of clients to each client
            }

            // Receive and store salary parts from clients
            List<List<Double>> allParts = new ArrayList<>(); // List to store salary parts from all clients
            for (ClientThread ct : clientThreads) {
                List<Double> parts = ct.receiveParts(); // Receive parts from each client
                allParts.add(parts); // Add parts to the list
            }

            // Print the parts received from all clients
            System.out.println("Received parts from clients:");
            for (int i = 0; i < allParts.size(); i++) {
                System.out.println("Client " + (i + 1) + " parts: " + allParts.get(i));
            }

            // Redistribute parts among clients (excluding the user's own parts)
            // Shuffle the parts for each client before redistribution
            for (int i = 0; i < numClients; i++) {
                Collections.shuffle(allParts.get(i)); // Shuffle the list of parts for each client
            }

            // Now redistribute the parts randomly (without duplication)
            for (int i = 0; i < numClients; i++) {
                List<Double> redistributedParts = new ArrayList<>();

                // Randomly select one part from each other client
                for (int j = 0; j < numClients; j++) {
                    if (i != j) {
                        // Get a random part from client j
                        double randomPart = allParts.get(j).get(0); // Get the first part after shuffle
                        redistributedParts.add(randomPart); // Add the part to the redistributed list

                        // Remove the selected part to prevent duplicates
                        allParts.get(j).remove(0); // Remove the selected part after it's been used
                    }
                }

                // Print and send the redistributed parts to the current client
                System.out.println("Redistributed parts to Client " + (i + 1) + ": " + redistributedParts);
                clientThreads.get(i).sendRedistributedParts(redistributedParts); // Send redistributed parts to the client
            }

            // Collect computations from clients
            List<Double> clientSums = new ArrayList<>(); // List to store the computed sums from each client
            double totalSum = 0; // Variable to store the total sum of all parts
            for (ClientThread ct : clientThreads) {
                double sum = ct.receiveComputation(); // Receive the computed sum from each client
                clientSums.add(sum); // Add the sum to the list
                totalSum += sum; // Add to the total sum
            }

            // Calculate and broadcast the average salary
            double average = totalSum / numClients; // Calculate the average salary
            for (ClientThread ct : clientThreads) {
                ct.sendAverage(average); // Send the average salary to each client
            }

            System.out.println("Average salary calculated and sent to all clients: " + average);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 1500; // Set the server port
        Server server = new Server(port); // Create a new server instance
        server.start(); // Start the server
    }

    // Inner class to handle each client connection
    private class ClientThread extends Thread {
        private Socket socket; // Client socket
        private ObjectInputStream sInput; // Stream to receive data from client
        private ObjectOutputStream sOutput; // Stream to send data to client
        private int clientId; // Client ID

        // Constructor to set the client socket and ID
        public ClientThread(Socket socket, int clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream()); // Create output stream to send data to client
                sInput = new ObjectInputStream(socket.getInputStream()); // Create input stream to receive data from client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Method to send the number of clients to the client
        public void sendNumberOfClients(int numClients) {
            try {
                sOutput.writeObject(numClients); // Write the number of clients to the output stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Method to receive the salary parts from the client
        public List<Double> receiveParts() {
            try {
                return (List<Double>) sInput.readObject(); // Read the list of parts from the input stream
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return new ArrayList<>(); // Return an empty list in case of error
        }

        // Method to send redistributed parts to the client
        public void sendRedistributedParts(List<Double> parts) {
            try {
                sOutput.writeObject(parts); // Write the redistributed parts to the output stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Method to receive the computed sum from the client
        public double receiveComputation() {
            try {
                return (Double) sInput.readObject(); // Read the computed sum from the input stream
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return 0; // Return 0 in case of error
        }

        // Method to send the average salary to the client
        public void sendAverage(double average) {
            try {
                sOutput.writeObject(average); // Write the average salary to the output stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Method to get the client ID
        public long getId() {
            return clientId; // Return the client ID
        }
    }
}
