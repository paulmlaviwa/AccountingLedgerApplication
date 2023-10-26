package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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

        loadLedgerFromCSV(ledger);

        if (ledger.isEmpty() || !hasHeader(ledger)) {
            String[] header = {"Date", "Time", "Description", "Vendor", "Amount"};
            ledger.add(0, header);
        }

        //Displaying the menu to user until user exits from the app
        while (true) {
            System.out.println("\nYour Financial Transaction Dashboard");
            System.out.println("\nHome Screen");
            System.out.println("\tD) Add Deposit");
            System.out.println("\tP) Make Payment");
            System.out.println("\tL) Ledger");
            System.out.println("\tX) Exit");
            System.out.print("What would you like to do?: ");

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

    private static void loadLedgerFromCSV(List<String[]> ledger) {
        try (BufferedReader csvReader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"))) {
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

    private static void displayLedgerOptions(Scanner scanner, List<String[]> ledger) {
        while (true) {
            System.out.println("\nLedger Options");
            System.out.println("\tA) All Transactions");
            System.out.println("\tD) Deposits");
            System.out.println("\tP) Payments");
            System.out.println("\tR) Reports");
            System.out.println("\tH) Home");
            System.out.print("Choose your ledger option: ");

            String ledgerOption = scanner.next().toUpperCase().trim();
            scanner.nextLine(); // Consume the newline character

            switch (ledgerOption) {
                case "A":
                    System.out.println("All Transactions:");
                    printLedger(ledger);
                    break;
                case "D":
                    System.out.println("Below is a comprehensive record of your deposit transactions: \n");
                    printLedgerByType(ledger, true);
                    break;
                case "P":
                    System.out.println("Below is a comprehensive record of your payment transactions: \n");
                    printLedgerByType(ledger, false);
                    break;
                case "R":
                    displayReportsMenu(scanner, ledger);
                    break;
                case "H":
                    return; // Return to the main menu
                default:
                    System.out.println("Please enter a valid ledger option.");
            }
        }
    }

    private static void displayReportsMenu(Scanner scanner, List<String[]> ledger) {
        while (true) {
            System.out.println("\nReports Menu");
            System.out.println("\t1) Month to Date");
            System.out.println("\t2) Previous Month");
            System.out.println("\t3) Year to Date");
            System.out.println("\t4) Previous Year");
            System.out.println("\t5) Search by Vendor");
            System.out.println("\t0) Back to Ledger");
            System.out.print("Choose a report option: ");

            String reportOption = scanner.next().toUpperCase().trim();
            scanner.nextLine(); // Consume the newline character

            switch (reportOption) {
                case "1":
                    generateMonthToDateReport(ledger);
                    break;
                case "2":
                    generatePreviousMonthReport(ledger);
                    break;
                case "3":
                    generateYearToDateReport(ledger);
                    break;
                case "4":
                    generatePreviousYearReport(ledger);
                    break;
                case "5":
                    generateVendorSearchReport(scanner, ledger);
                    break;
                case "0":
                    return; // Return to the Ledger menu
                default:
                    System.out.println("Please enter a valid report option.");
            }
        }
    }

    private static void generateMonthToDateReport(List<String[]> ledger) {
        LocalDate currentDate = getCurrentLocalDate();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);

        System.out.println("Month to Date Transactions:");
        filterAndPrintTransactions(ledger, firstDayOfMonth, currentDate);
    }

    private static void generatePreviousMonthReport(List<String[]> ledger) {
        LocalDate currentDate = getCurrentLocalDate();
        LocalDate firstDayOfPreviousMonth = currentDate.minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfPreviousMonth = currentDate.withDayOfMonth(1).minusDays(1);

        System.out.println("Previous Month Transactions:");
        filterAndPrintTransactions(ledger, firstDayOfPreviousMonth, lastDayOfPreviousMonth);
    }

    private static void generateYearToDateReport(List<String[]> ledger) {
        LocalDate currentDate = getCurrentLocalDate();
        LocalDate firstDayOfYear = currentDate.withDayOfYear(1);

        System.out.println("Year to Date Transactions:");
        filterAndPrintTransactions(ledger, firstDayOfYear, currentDate);
    }

    private static void generatePreviousYearReport(List<String[]> ledger) {
        LocalDate currentDate = getCurrentLocalDate();
        LocalDate firstDayOfPreviousYear = currentDate.minusYears(1).withDayOfYear(1);
        LocalDate lastDayOfPreviousYear = currentDate.minusYears(1).withDayOfYear(currentDate.getDayOfYear());

        System.out.println("Previous Year Transactions:");
        filterAndPrintTransactions(ledger, firstDayOfPreviousYear, lastDayOfPreviousYear);
    }

    private static void generateVendorSearchReport(Scanner scanner, List<String[]> ledger) {
        System.out.print("Enter the vendor name to search for: ");
        String vendorName = scanner.nextLine().trim();

        List<String[]> searchResults = new ArrayList<>();

        for (String[] entry : ledger) {
            if (entry[3].toLowerCase().contains(vendorName.toLowerCase())) {
                searchResults.add(entry);
            }
        }

        if (searchResults.isEmpty()) {
            System.out.println("No transactions found for the vendor: " + vendorName);
        } else {
            System.out.println("Transactions for Vendor: " + vendorName);
            printLedger(searchResults);
        }
    }

    private static LocalDate getCurrentLocalDate() {
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime currentTime = ZonedDateTime.now(zoneId);
        return currentTime.toLocalDate();
    }

    private static void filterAndPrintTransactions(List<String[]> ledger, LocalDate startDate, LocalDate endDate) {
        // To skip the header row when iterating through the ledger
        boolean firstRow = true;

        for (String[] entry : ledger) {
            if (firstRow) {
                firstRow = false;
                continue; // Skip the header row
            }

            LocalDate transactionDate = LocalDate.parse(entry[0]);

            if (!transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate)) {
                printEntry(entry);
            }
        }
    }

    private static void printLedger(List<String[]> ledger) {
        if (ledger.isEmpty()) {
            System.out.println("The ledger is empty.");
        } else {
            // Skip the first header and reverse the rest of the ledger list
            List<String[]> reversedLedger = new ArrayList<>(ledger.subList(1, ledger.size()));
            Collections.reverse(reversedLedger);

            // Print the header
            printEntry(ledger.get(0));

            for (String[] entry : reversedLedger) {
                printEntry(entry);
            }
        }
    }

    private static void printLedgerByType(List<String[]> ledger, boolean deposits) {
        if (ledger.isEmpty()) {
            System.out.println("The ledger is empty.");
            return;
        }

        // Skip the first header and reverse the rest of the ledger list
        List<String[]> reversedLedger = new ArrayList<>(ledger.subList(1, ledger.size()));
        Collections.reverse(reversedLedger);

        // Print the header
        printEntry(ledger.get(0));

        for (String[] entry : reversedLedger) {
            boolean isDeposit = entry[4].charAt(0) != '-';
            if ((deposits && isDeposit) || (!deposits && !isDeposit)) {
                printEntry(entry);
            }
        }
    }

    private static void printEntry(String[] entry) {
        for (String field : entry) {
            System.out.print(field);
            System.out.print(" | ");
        }
        System.out.println();
    }
}
