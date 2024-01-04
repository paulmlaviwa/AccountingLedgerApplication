# Accounting Ledger Application

The **Accounting Ledger Application** is a Java-based financial management tool that simplifies financial tracking and reporting. It allows users to record deposits and payments, generate reports, and analyze their financial data conveniently.        

![Screenshot (396)](https://github.com/paulmlaviwa/AccountingLedgerApplication/assets/14105717/22a6641b-05b6-4932-8b86-ce1e355a4de5)

## Features.

- **User-Friendly Interface**: The application offers a simple and text-based menu for interacting with your financial data.

- **Data Persistence**: Financial transaction records are stored in a CSV file for easy access.

- **Data Validation**: The application ensures that only valid numeric amounts are accepted, preventing data entry errors.
  ![Screenshot (398)](https://github.com/paulmlaviwa/AccountingLedgerApplication/assets/14105717/8d916643-5358-4fc7-a4c3-eb3a94ec2f6e)


- **Report Generation**: Users can generate various types of reports, including Month to Date, Previous Month, Year to Date, Previous Year, and Vendor-specific reports.
  
![Screenshot (399)](https://github.com/paulmlaviwa/AccountingLedgerApplication/assets/14105717/96b838f2-74dd-480c-b3d3-896c34776947)

- **Data Filtering**: You can filter transactions by type (deposits or payments) and search for specific transactions by vendor.
![Screenshot (400)](https://github.com/paulmlaviwa/AccountingLedgerApplication/assets/14105717/e33fb16a-a321-4b71-9926-b51a6909a961)

### Prerequisites

To run the Accounting Ledger Application, you'll need the following software installed on your system:

- Java Development Kit (JDK)
- A compatible Java Integrated Development Environment (IDE)

### Installation

1. Clone the repository here https://github.com/paulmlaviwa/AccountingLedgerApplication

2. Open the project in your preferred Java IDE.

3. Build and run the `AccountingLedger` class to start the application.

## Usage

1. Launch the application.

2. The main menu will be displayed with the following options as shown earlier:

   - D: Add a Deposit
   - P: Make a Payment
   - L: Ledger
   - X: Exit

3. Select an option to perform the desired task, such as adding a deposit, making a payment or generating reports.

4. Follow the on-screen instructions to complete your chosen action.

## Handling Positive Deposits and Negative Payments

In the Accounting Ledger Application, it's important to note that the convention used is to represent **deposits as positive values** and **payments as negative values**. This choice simplifies financial data entry and reporting for several reasons:

![Screenshot (402)](https://github.com/paulmlaviwa/AccountingLedgerApplication/assets/14105717/2116e0ee-8c86-45e2-baf4-19b9f7901d65)  ![Screenshot (403)](https://github.com/paulmlaviwa/AccountingLedgerApplication/assets/14105717/3bad771a-717e-4bd9-9ecb-34a0fc888948)

1. **Clarity**: Representing deposits as positive values reflects the increase in your account balance, which is the typical expectation when you make a deposit. Similarly, payments are represented as negative values to show a reduction in your account balance.

2. **Consistency**: This approach aligns with common accounting practices and conventions. It ensures that financial transactions in the ledger are intuitive and straightforward to understand.

3. **Simplified Calculations**: When you generate reports or calculate balances, having deposits and payments as positive and negative values respectively simplifies arithmetic operations.

One thing that is interesting in this project is that I have implemented a feature in the code that allows the app to fetch the current date and time in Coordinated Universal Time using Java's ZonedDateTime and DateTimeFormatter classes. UTC is a uniform and stable time reference, irrespective of geographical location or time zones.

![Screenshot (406)](https://github.com/paulmlaviwa/AccountingLedgerApplication/assets/14105717/e4af570c-e661-4b45-b303-b6f8cdb5bb06)
