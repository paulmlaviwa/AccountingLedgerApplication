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
    private static final DecimalFormat moneyFormat = new DecimalFormat("#,###.00");
    private static String formattedDate = "";
    private static String formattedTime = "";
    private static String formattedAmount = "";

    public static void main(String[] args) {
        List<String[]> ledger = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        loadLedgerFromCSV("src/main/resources/transactions.csv", ledger);

        if (ledger.isEmpty() || !hasHeader(ledger)) {
            String[] header = {"Date", "Time", "Description", "Vendor", "Amount"};
            ledger.add(0, header);
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

                    scanner.nextLine();

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

                    formattedAmount = userChoice.equals("P") ? "-" + moneyFormat.format(transactionAmount) : moneyFormat.format(transactionAmount);

                    String[] transactionEntry = {formattedDate, formattedTime, transactionDescription, transactionVendor, formattedAmount};

                    ledger.add(transactionEntry);

                    writeTransactionToCSV("src/main/resources/transactions.csv", transactionEntry);

                    System.out.printf("%.2f %s was successful%n", transactionAmount, userChoice.equals("D") ? "deposit" : "payment");
                    break;

                case "L":
                    displayLedgerOptions(scanner, ledger);
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

    private static double getValidAmount(Scanner scanner) {
        double amount = 0.0;
        boolean valid = false;

        while (!valid) {
            System.out.print("Enter the amount: ");
            String input = scanner.nextLine().trim();

            try {
                amount = Double.parseDouble(input.replace(",", ""));
                valid = true;
            } catch (NumberFormatException e) {
                System.err.println("Invalid amount. Please enter a valid number.");
            }
        }

        return amount;
    }

    private static boolean hasHeader(List<String[]> ledger) {
        if (ledger.isEmpty()) {
            return false;
        }

        String[] firstEntry = ledger.get(0);
        return firstEntry.length == 5 &&
                firstEntry[0].equals("Date") &&
                firstEntry[1].equals("Time") &&
                firstEntry[2].equals("Description") &&
                firstEntry[3].equals("Vendor") &&
                firstEntry[4].equals("Amount");
    }

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

    private static void printEntry(String[] entry, int[] columnWidths) {
        for (int i = 0; i < entry.length; i++) {
            String field = entry[i];
            int padding = columnWidths[i] - field.length();
            System.out.print(field);
            for (int j = 0; j < padding; j++) {
                System.out.print(" ");
            }
            System.out.print(" | ");
        }
        System.out.println();
    }

    private static void printLedger(List<String[]> ledger) {
        if (ledger.isEmpty()) {
            System.out.println("The ledger is empty.");
        } else {
            int[] columnWidths = new int[5];

            for (String[] entry : ledger) {
                for (int i = 0; i < entry.length; i++) {
                    columnWidths[i] = Math.max(columnWidths[i], entry[i].length());
                }
            }

            for (String[] entry : ledger) {
                printEntry(entry, columnWidths);
            }
        }
    }

    private static void displayLedgerOptions(Scanner scanner, List<String[]> ledger) {
        while (true) {
            System.out.println("\nLedger Options");
            System.out.println("A) All Transactions");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Choose ledger option: ");

            String ledgerOption = scanner.next().toUpperCase().trim();
            scanner.nextLine(); // Consume the newline character

            switch (ledgerOption) {
                case "A":
                    System.out.println("All Transactions:");
                    printLedger(ledger);
                    break;
                case "D":
                    System.out.println("Deposits:");
                    printLedgerByType(ledger, true);
                    break;
                case "P":
                    System.out.println("Payments:");
                    printLedgerByType(ledger, false);
                    break;
                case "R":
                    System.out.println("Reports:");
                    break;
                case "H":
                    return; // Return to the main menu
                default:
                    System.out.println("Please enter a valid ledger option.");
            }
        }
    }

    private static void printLedgerByType(List<String[]> ledger, boolean deposits) {
        if (ledger.isEmpty()) {
            System.out.println("The ledger is empty.");
            return;
        }

        int[] columnWidths = new int[5];
        for (String[] entry : ledger) {
            if ((deposits && entry[4].charAt(0) != '-') || (!deposits && entry[4].charAt(0) == '-')) {
                for (int i = 0; i < entry.length; i++) {
                    columnWidths[i] = Math.max(columnWidths[i], entry[i].length());
                }
            }
        }

        for (String[] entry : ledger) {
            if ((deposits && entry[4].charAt(0) != '-') || (!deposits && entry[4].charAt(0) == '-')) {
                printEntry(entry, columnWidths);
            }
        }
    }
}
