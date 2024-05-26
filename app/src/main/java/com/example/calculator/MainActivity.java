package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    // TextView for displaying the result
    private TextView resultTextView;
    // TextView for displaying the solution
    private TextView solutionTextView;
    // ArrayList for storing the history of calculations
    private ArrayList<String> history;
    // String for storing the current number being entered
    private String currentNumber;
    // Boolean indicating if an operator has been pressed
    private boolean hasOperator;
    // Integer for storing the current result
    private int currentResult;
    // Boolean indicating if there is a current result
    private boolean hasCurrentResult;
    // Boolean indicating if decimal mode is active
    private boolean isDecimalMode;
    // Boolean indicating if hexadecimal mode is active
    private boolean isHexadecimalMode;
    // Boolean indicating if octal mode is active
    private boolean isOctalMode;
    // Boolean indicating if binary mode is active
    private boolean isBinaryMode;
    // Boolean indicating if decimal has been pressed
    private boolean isDecimalPressed;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the TextViews, history ArrayList, and other variables
        resultTextView = findViewById(R.id.resultTextView);
        solutionTextView = findViewById(R.id.solutionTextView);
        history = new ArrayList<>();
        currentNumber = "";
        hasCurrentResult = false;
        hasOperator = false;
        isDecimalMode = true;
        isBinaryMode = false;
        isHexadecimalMode = false;
        isOctalMode = false;
        isDecimalPressed = false;

        // Initialize the history button and set its OnClickListener
        Button historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> showHistoryDialog());
    }

    /**
     * Handles number button clicks
     * 
     * @param view The View that was clicked
     */
    public void onNumberClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        if (hasCurrentResult) {
            currentNumber = buttonText;
            hasCurrentResult = false;
        } else {
            currentNumber += buttonText;
        }
        hasOperator = false;
        updateResultTextView();
    }

    /**
     * Handles operator button clicks
     * 
     * @param view The View that was clicked
     */
    public void onOperatorClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        if (!hasOperator) {
            if (!currentNumber.isEmpty()) {
                currentNumber += buttonText;
            } else {
                currentNumber = "0" + buttonText;
            }
            hasOperator = true;
        } else {
            currentNumber = currentNumber.substring(0, currentNumber.length() - 1) + buttonText;
        }
        updateResultTextView();
    }

    /**
     * Handles power button clicks
     * 
     * @param view The View that was clicked
     */
    public void onPowerClick(View view) {
        if (!hasOperator) {
            if (!currentNumber.isEmpty()) {
                currentNumber += "^";
            } else {
                currentNumber = "0^";
            }
        } else {
            currentNumber = currentNumber.substring(0, currentNumber.length() - 1) + "^";
        }
        hasOperator = true;
        hasCurrentResult = false;
        updateResultTextView();
    }

    /**
     * Handles equals button clicks
     * 
     * @param view The View that was clicked
     */
    public void onEqualsClick(View view) {
        if (!currentNumber.isEmpty()) {
            try {
                String result = evaluateExpression(currentNumber);
                currentResult = Integer.parseInt(result);
                hasCurrentResult = true;
                hasOperator = false;

                history.add(currentNumber + " = " + currentResult + "\n");
                solutionTextView.setText(String.format("%s%s", currentNumber, getString(R.string.equals)));
                currentNumber = "";
                updateResultTextView();
            } catch (Exception e) {
                resultTextView.setText(getString(R.string.error));
                currentNumber = "";
                hasCurrentResult = false;
                hasOperator = false;
            }
        }
    }

    /**
     * Evaluates a mathematical expression
     * 
     * @param expression The expression to evaluate
     * @return The result of the expression as a String
     */
    private String evaluateExpression(String expression) throws ArithmeticException {
        Stack<Integer> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c)) {
                int num = c - '0';
                while (i + 1 < expression.length() && Character.isDigit(expression.charAt(i + 1))) {
                    num = num * 10 + (expression.charAt(i + 1) - '0');
                    i++;
                }
                operands.push(num);
            } else if (c == '+' || c == '-' || c == 'x' || c == '÷' || c == '^') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    int result = performOperation(operands.pop(), operands.pop(), operators.pop());
                    operands.push(result);
                }
                operators.push(c);
            }
        }
        while (!operators.isEmpty()) {
            int result = performOperation(operands.pop(), operands.pop(), operators.pop());
            operands.push(result);
        }
        return String.valueOf(operands.pop());
    }

    /**
     * Determines if the first operator has precedence over the second
     * 
     * @param op1 The first operator
     * @param op2 The second operator
     * @return True if op1 has precedence over op2, otherwise false
     */
    private boolean hasPrecedence(char op1, char op2) {
        return (op1 != '^' && op1 != 'x' && op1 != '÷') || (op2 != '+' && op2 != '-');
    }

    /**
     * Performs a mathematical operation
     * 
     * @param operand2 The second operand
     * @param operand1 The first operand
     * @param operator The operator
     * @return The result of the operation
     */
    private int performOperation(int operand2, int operand1, char operator) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case 'x':
                return operand1 * operand2;
            case '÷':
                if (operand2 == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero");
                return operand1 / operand2;
            case '^':
                return (int) Math.pow(operand1, operand2);
            default:
                throw new UnsupportedOperationException("Unknown operator: " + operator);
        }
    }

    /**
     * Handles binary button clicks
     * @param view The View that was clicked
     */
    public void onBinaryClick(View view) {
        if (isDecimalMode) {
            // Convert decimal to binary
            decimalToBinary(view);
            isDecimalMode = !isDecimalMode;
        } else if (isHexadecimalMode) {
            // Convert hexadecimal to binary
            hexadecimalToBinary(view);
            isHexadecimalMode = !isHexadecimalMode;
        } else if (isOctalMode) {
            // Convert octal to binary
            octalToBinary(view);
            isOctalMode = !isOctalMode;
        } else if (isBinaryMode) {
            // Convert binary to decimal
            binaryToDecimal(view);
            isBinaryMode = !isBinaryMode;
        }
        isBinaryMode = !isBinaryMode;
    }

    /**
     * Handles hexadecimal button clicks
     * @param view The View that was clicked
     */
    public void onHexadecimalClick(View view) {
        if (isDecimalMode) {
            // Convert decimal to hexadecimal
            decimalToHexadecimal(view);
            isDecimalMode = !isDecimalMode;
        } else if (isHexadecimalMode) {
            // Go to Decimal
            hexadecimalToDecimal(view);
            isDecimalMode = !isDecimalMode;
        } else if (isOctalMode) {
            // Convert octal to hexadecimal
            octalToHexadecimal(view);
            isOctalMode = !isOctalMode;
        } else if (isBinaryMode) {
            // Convert binary to hexadecimal
            binaryToHexadecimal(view);
            isBinaryMode = !isBinaryMode;
        }
        isHexadecimalMode = !isHexadecimalMode;
    }

    /**
     * Handles octal button clicks
     * @param view The View that was clicked
     */
    public void onOctalClick(View view) {
        if (isDecimalMode) {
            // Convert decimal to octal
            decimalToOctal(view);
            isDecimalMode = false;
            isOctalMode = true;
        } else if (isHexadecimalMode) {
            // Convert hexadecimal to octal
            hexadecimalToOctal(view);
            isHexadecimalMode = false;
            isOctalMode = true;
        } else if (isOctalMode) {
            // Do nothing
            octalToDecimal(view);
            isDecimalMode = true;
            isOctalMode = false;
        } else if (isBinaryMode) {
            // Convert binary to octal
            binaryToOctal(view);
            isBinaryMode = false;
            isOctalMode = true;
        }
    }

    // Conversion methods to different methods ....
    public void decimalToBinary(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int number = Integer.parseInt(currentNumber);
            currentNumber = Integer.toBinaryString(number);
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void decimalToOctal(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int number = Integer.parseInt(currentNumber);
            currentNumber = Integer.toOctalString(number);
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void decimalToHexadecimal(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int number = Integer.parseInt(currentNumber);
            String hexString = Integer.toHexString(number);
            currentNumber = hexString.toUpperCase();
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void binaryToDecimal(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int decimalNumber = Integer.parseInt(currentNumber, 2);
            currentNumber = String.valueOf(decimalNumber);
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void binaryToOctal(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int decimalNumber = Integer.parseInt(currentNumber, 2);
            currentNumber = Integer.toOctalString(decimalNumber);
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void binaryToHexadecimal(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int decimalNumber = Integer.parseInt(currentNumber, 2);
            String hexString = Integer.toHexString(decimalNumber);
            currentNumber = hexString.toUpperCase();
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void hexadecimalToDecimal(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int decimalNumber = Integer.parseInt(currentNumber, 16);
            currentNumber = String.valueOf(decimalNumber);
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void hexadecimalToBinary(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int decimalNumber = Integer.parseInt(currentNumber, 16);
            currentNumber = Integer.toBinaryString(decimalNumber);
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void hexadecimalToOctal(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int decimalNumber = Integer.parseInt(currentNumber, 16);
            currentNumber = Integer.toOctalString(decimalNumber);
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void octalToHexadecimal(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int decimalNumber = Integer.parseInt(currentNumber, 8);
            String hexString = Integer.toHexString(decimalNumber);
            currentNumber = hexString.toUpperCase();
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void octalToBinary(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int decimalNumber = Integer.parseInt(currentNumber, 8);
            currentNumber = Integer.toBinaryString(decimalNumber);
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    public void octalToDecimal(View view) {
        try {
            if (hasCurrentResult) {
                clearCurrentResult();
                if (Objects.equals(currentNumber, "")) {
                    currentNumber = String.valueOf(currentResult);
                }
            }
            int decimalNumber = Integer.parseInt(currentNumber, 8);
            currentNumber = String.valueOf(decimalNumber);
            updateResultTextView();
        } catch (NumberFormatException e) {
            handleConversionException(e);
        }
    }

    /**
     * Handles decimal button clicks
     *
     * @param view The View that was clicked
     */
    public void onDecimalClick(View view) {
        if (!isDecimalPressed) {
            isDecimalPressed = true;

            if (hasCurrentResult) {
                currentNumber = "0.";
                hasCurrentResult = false;
            } else {
                currentNumber += ".";
            }

            updateResultTextView();
        }
    }

    /**
     * Handles delete button clicks
     * 
     * @param view The View that was clicked
     */
    public void onDeleteClick(View view) {
        if (currentNumber.isEmpty()) {
            String screen = resultTextView.getText().toString();
            if (!screen.isEmpty()) {
                currentNumber = screen.substring(0, screen.length() - 1);
            }
        } else {
            currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
        }
        updateResultTextView();
    }

    /**
     * Clears the current result
     */
    public void clearCurrentResult() {
        currentNumber = "";
        hasOperator = false;
        hasCurrentResult = false;
        solutionTextView.setText("0");
        updateResultTextView();
    }

    /**
     * Handles clear button clicks
     * 
     * @param view The View that was clicked
     */
    public void onClearClick(View view) {
        clearCurrentResult();
    }

    /**
     * Handles reset button clicks
     * 
     * @param view The View that was clicked
     */
    public void onResetClick(View view) {
        currentNumber = "";
        hasOperator = false;
        hasCurrentResult = false;
        history.clear();
        solutionTextView.setText("0");
        isDecimalMode = true;
        isBinaryMode = false;
        isHexadecimalMode = false;
        isOctalMode = false;
        isDecimalPressed = false;
        updateResultTextView();
    }

    /**
     * Updates the result TextView
     */
    private void updateResultTextView() {
        String text;
        if (!currentNumber.isEmpty()) {
            text = currentNumber;
        } else if (hasCurrentResult) {
            text = String.valueOf(currentResult);
        } else {
            text = "0";
        }
        resultTextView.setText(text);
    }

    /**
     * Shows the history dialog
     */
    private void showHistoryDialog() {
        // Creating a new dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.history_dialog);

        // Setting the dialog title
        dialog.setTitle("History");

        // Getting the TextView in the dialog
        TextView historyTextView = dialog.findViewById(R.id.historyTextView);

        // Setting the text of the TextView to the history list
        StringBuilder sb = new StringBuilder();
        for (String item : history) {
            sb.append(item).append("\n");
        }
        historyTextView.setText(sb.toString());

        // Showing the dialog
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        // Get the current dialog
        Dialog dialog = new Dialog(this);
        if (dialog.isShowing()) {
            dialog.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    private void handleConversionException(Throwable e) {
        resultTextView.setText(getString(R.string.error));
        currentNumber = "";
        hasCurrentResult = false;
        hasOperator = false;
        System.out.println(e);
    }
}