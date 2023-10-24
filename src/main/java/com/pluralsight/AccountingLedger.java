package com.pluralsight;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AccountingLedger {
    // Format for displaying money amounts with commas and two decimal places
    private static final DecimalFormat moneyFormat = new DecimalFormat("#,###.00");

    // Variables for storing formatted date, time, and amount
    private static String formattedDate = "";
    private static String formattedTime = "";
    private static String formattedAmount = "";

    public static void main(String[] args) {
        List<String[]> ledger = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        // Load existing ledger data from the CSV file
        loadLedgerFromCSV("src/main/resources/transactions.csv", ledger);

        if (ledger.isEmpty()) {
            // The ledger is empty, so add the header
            String[] header = {"Date", "Time", "Description", "Vendor", "Amount"};
            ledger.add(header);
        }

        while (true) {
            System.out.println("\nHome Screen");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Choose your option: ");

            String userChoice = scanner.next().toUpperCase().trim();

            switch (userChoice) {
                case "D":
                case "P":
                    String transactionDescription;
                    String transactionVendor;
                    double transactionAmount;

                    scanner.nextLine(); // Consume the newline character

                    System.out.print("Enter the transaction description: ");
                    transactionDescription = scanner.nextLine().trim();

                    System.out.print("Enter the name of the Vendor: ");
                    transactionVendor = scanner.nextLine().trim();

                    transactionAmount = getValidAmount(scanner);

                    ZoneId zoneId = ZoneId.of("UTC");
                    ZonedDateTime currentTime = ZonedDateTime.now(zoneId);

                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                    formattedDate = currentTime.format(dateFormatter);
                    formattedTime = currentTime.format(timeFormatter);

                    // Prefix the transaction amount with '-' for payments
                    formattedAmount = userChoice.equals("P") ? "-" + moneyFormat.format(transactionAmount) : moneyFormat.format(transactionAmount);

                    String[] transactionEntry = {formattedDate, formattedTime, transactionDescription, transactionVendor, formattedAmount};

                    ledger.add(transactionEntry);

                    // Write the transaction to the CSV file
                    writeTransactionToCSV("src/main/resources/transactions.csv", transactionEntry);

                    System.out.printf("%.2f %s was successful%n", transactionAmount, userChoice.equals("D") ? "deposit" : "payment");
                    break;

                case "L":
                    System.out.println("Here is your Ledger Information:");
                    for (String[] entry : ledger) {
                        formattedDate = entry[0];
                        formattedTime = entry[1];
                        String description = entry[2];
                        String vendor = entry[3];
                        formattedAmount = entry[4];

                        System.out.println(formattedDate + " | " + formattedTime + " | " + description + " | " + vendor + " | " + formattedAmount);
                    }
                    break;

                case "X":
                    System.out.println("Are you sure you want to exit? Y/N");
                    String exitConfirmation = scanner.next().toUpperCase().trim();
                    if (exitConfirmation.equals("Y")) {
                        System.out.println("Exiting... Thank you.");
                        scanner.close();
                        System.exit(0);
                    }
                    break;

                default:
                    System.out.println("Please enter a valid choice.");
            }
        }
    }

    // Method to validate and get a valid amount from the user
    private static double getValidAmount(Scanner scanner) {
        double amount = 0.0;
        boolean valid = false;

        while (!valid) {
            System.out.print("Enter the amount: ");
            String input = scanner.nextLine().trim();

            try {
                // Replace commas with an empty string to support user input with commas
                amount = Double.parseDouble(input.replace(",", ""));
                valid = true;
            } catch (NumberFormatException e) {
                System.err.println("Invalid amount. Please enter a valid number.");
            }
        }

        return amount;
    }

    // Method to load ledger data from a CSV file
    private static void loadLedgerFromCSV(String filePath, List<String[]> ledger) {
        try (BufferedReader csvReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = csvReader.readLine()) != null) {
                String[] entry = line.split("\\|");
                ledger.add(entry);
            }
        } catch (IOException e) {
            System.err.println("Error loading ledger data from the CSV file: " + e.getMessage());
        }
    }

    // Method to write a transaction to the CSV file
    private static void writeTransactionToCSV(String filePath, String[] transaction) {
        try (FileWriter csvWriter = new FileWriter(filePath, true)) {
            String formattedTransaction = String.join("|", transaction);
            csvWriter.append(formattedTransaction);
            csvWriter.append("\n");
            csvWriter.flush();
        } catch (IOException e) {
            System.err.println("Error writing the transaction to the CSV file: " + e.getMessage());
        }
    }
}
