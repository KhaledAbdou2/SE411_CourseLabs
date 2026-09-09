package edu.psu.se411.lab05;

import java.math.BigDecimal;
import edu.psu.se411.lab05.exceptions.InvalidAgeException;
import edu.psu.se411.lab05.exceptions.InsufficientFundsException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Exercise 1: Age validation");
        for (int age : new int[] {17, 18, 25}) {
            try {
                System.out.print("Age " + age + ": ");
                validateAge(age);
            } catch (InvalidAgeException exception) {
                System.out.println("Rejected: " + exception.getMessage());
            }
        }

        System.out.println("\nExercise 2: Online wallet");
        Wallet wallet = new Wallet(new BigDecimal("100.00"));
        BigDecimal bankBalance = new BigDecimal("0.00");
        System.out.println("Opening wallet balance: " + wallet.getBalance());
        for (String requested : new String[] {"40.00", "80.00"}) {
            try {
                BigDecimal transferred = wallet.withdraw(new BigDecimal(requested));
                bankBalance = bankBalance.add(transferred);
                System.out.println("Transferred to simulated bank account: " + transferred);
            } catch (InsufficientFundsException exception) {
                System.out.println("Withdrawal rejected: " + exception.getMessage());
            }
            System.out.println("Wallet balance: " + wallet.getBalance());
            System.out.println("Bank balance: " + bankBalance);
        }
    }

    public static void validateAge(int age) throws InvalidAgeException {
        if (age < 18) {
            throw new InvalidAgeException("Age must be 18 or older; received " + age + ".");
        }
        System.out.println("Age valid message.");
    }
}
