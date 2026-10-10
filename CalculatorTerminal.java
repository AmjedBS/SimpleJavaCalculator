import java.util.Scanner; // This library is used to take input from the user

class Calculator {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in); // Create a Scanner object to read input

        // 1. Ask for the first number
        System.out.print("Enter the first number: ");
        double num1 = input.nextDouble(); // Read the first number

        // 2. Ask for the operator 
        System.out.print("Enter an operator (+, -, *, /): ");
        char operator = input.next().charAt(0); // Read the operator

        // 3. Ask for the secound number
        System.out.print("Enter the second number: ");
        double num2 = input.nextDouble(); // Read the second number

        // 4. Calculate and display the result
        double result;
        switch (operator){
            case '+':
                result = num1 + num2;
                System.out.println(num1 + " + " + num2 + " = " + result);
                break;
            case '-':
                result = num1 - num2;
                System.out.println(num1 + " - " + num2 + " = " + result);
                break;
            case '*':
                result = num1 * num2;
                System.out.println(num1 + " * " + num2 + " = " + result);
                break;
            case '/':
                result = num1 / num2;
                System.out.println(num1 + " / " + num2 + " = " + result);
                break;
            default:
                System.out.println("Invalid operator!");
        }
    }
}