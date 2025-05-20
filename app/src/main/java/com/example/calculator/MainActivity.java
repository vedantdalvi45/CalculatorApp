package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private StringBuilder expression = new StringBuilder();
    DecimalFormat format = new DecimalFormat("0.######");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDisplay = findViewById(R.id.tvDisplay);

        setNumericOnClickListener();
        setOperatorOnClickListener();
        setUtilityButtons();
    }

    private void setNumericOnClickListener() {
        int[] numericButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDecimal
        };

        for (int id : numericButtons) {
            Button b = findViewById(id);
            b.setOnClickListener(v -> {
                expression.append(b.getText().toString());
                tvDisplay.setText(expression.toString());
            });
        }
    }

    private void setOperatorOnClickListener() {
        int[] operatorButtons = {
                R.id.btnAdd, R.id.btnSubtract,
                R.id.btnMultiply, R.id.btnDivide
        };

        for (int id : operatorButtons) {
            Button b = findViewById(id);
            b.setOnClickListener(v -> {
                String op = b.getText().toString();
                switch (op) {
                    case "×": op = "*"; break;
                    case "÷": op = "/"; break;
                }
                expression.append(op);
                tvDisplay.setText(expression.toString());
            });
        }

        findViewById(R.id.btnEquals).setOnClickListener(v -> {
            try {
                String expr = expression.toString();
                double result = evaluateExpression(expr);
                tvDisplay.setText(format.format(result));
                expression.setLength(0);
                expression.append(format.format(result));
            } catch (Exception e) {
                tvDisplay.setText("Error");
                expression.setLength(0);
            }
        });
    }

    private void setUtilityButtons() {
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            expression.setLength(0);
            tvDisplay.setText("0");
        });

        findViewById(R.id.btnPlusMinus).setOnClickListener(v -> {
            // Not implemented for full expression mode
        });

        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            expression.append("/100");
            tvDisplay.setText(expression.toString());
        });
    }

    // Simple expression evaluator (no parentheses)
    private double evaluateExpression(String expr) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int i = 0;
        int len = expr.length();

        while (i < len) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < len && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    num.append(expr.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(num.toString()));
                continue;
            }

            if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    char op = operators.pop();
                    numbers.push(applyOp(a, b, op));
                }
                operators.push(c);
            }
            i++;
        }

        while (!operators.isEmpty()) {
            double b = numbers.pop();
            double a = numbers.pop();
            char op = operators.pop();
            numbers.push(applyOp(a, b, op));
        }

        return numbers.pop();
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private double applyOp(double a, double b, char op) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return b == 0 ? Double.NaN : a / b;
        }
        return 0;
    }
}
