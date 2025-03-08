package edu.hrbeu.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private StringBuilder show = new StringBuilder();
    private ArrayList<String> history = new ArrayList<>();
    private int signal = 0;
    private EditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.editTextText2);

        ConstraintLayout AC = findViewById(R.id.AC);
        ConstraintLayout Divide = findViewById(R.id.D);
        ConstraintLayout Multiplication = findViewById(R.id.X);
        ConstraintLayout Sub = findViewById(R.id.sub);
        ConstraintLayout Add = findViewById(R.id.add);
        ConstraintLayout Equal = findViewById(R.id.equal);
        ConstraintLayout Point = findViewById(R.id.point);
        ConstraintLayout Delete = findViewById(R.id.dl);
        ConstraintLayout Percent = findViewById(R.id.percent);
        ConstraintLayout DoubleZero = findViewById(R.id.yu);
        ConstraintLayout Zero = findViewById(R.id.zero);
        ConstraintLayout One = findViewById(R.id.one);
        ConstraintLayout Two = findViewById(R.id.two);
        ConstraintLayout Three = findViewById(R.id.three);
        ConstraintLayout Four = findViewById(R.id.four);
        ConstraintLayout Five = findViewById(R.id.five);
        ConstraintLayout Six = findViewById(R.id.six);
        ConstraintLayout Seven = findViewById(R.id.seven);
        ConstraintLayout Eight = findViewById(R.id.eight);
        ConstraintLayout Nine = findViewById(R.id.nine);
        ImageView exitButton = findViewById(R.id.imageView2);
        ImageView historyButton = findViewById(R.id.historyButton);

        AC.setOnClickListener(v -> clearAll());
        Delete.setOnClickListener(v -> deleteLast());
        Percent.setOnClickListener(v -> applyPercent());
        DoubleZero.setOnClickListener(v -> appendNumber("00"));
        Zero.setOnClickListener(v -> appendNumber("0"));
        One.setOnClickListener(v -> appendNumber("1"));
        Two.setOnClickListener(v -> appendNumber("2"));
        Three.setOnClickListener(v -> appendNumber("3"));
        Four.setOnClickListener(v -> appendNumber("4"));
        Five.setOnClickListener(v -> appendNumber("5"));
        Six.setOnClickListener(v -> appendNumber("6"));
        Seven.setOnClickListener(v -> appendNumber("7"));
        Eight.setOnClickListener(v -> appendNumber("8"));
        Nine.setOnClickListener(v -> appendNumber("9"));
        Point.setOnClickListener(v -> appendPoint());
        Add.setOnClickListener(v -> appendOperator("+"));
        Sub.setOnClickListener(v -> appendOperator("-"));
        Multiplication.setOnClickListener(v -> appendOperator("*"));
        Divide.setOnClickListener(v -> appendOperator("/"));
        Equal.setOnClickListener(v -> calculateResult());
        exitButton.setOnClickListener(v -> finish());
        historyButton.setOnClickListener(v -> showHistoryDialog());
    }

    private void clearAll() {
        show.setLength(0);
        signal = 0;
        updateDisplay();
    }

    private void deleteLast() {
        if (show.length() > 0) {
            show.deleteCharAt(show.length() - 1);
            updateDisplay();
        }
    }

    private void appendNumber(String number) {
        if (signal == 1) {
            show.setLength(0);
            signal = 0;
        }
        if (number.equals("0") && show.toString().equals("0")) {
            return;
        }
        show.append(number);
        updateDisplay();
    }

    private void appendPoint() {
        if (signal == 1) {
            show.setLength(0);
            show.append(".");
            signal = 0;
        } else {
            String current = show.toString();
            if (current.isEmpty() || isLastOperator(current)) {
                show.append("0.");
            } else if (!hasDecimalInLastNumber(current)) {
                show.append(".");
            }
        }
        updateDisplay();
    }

    private void appendOperator(String operator) {
        String current = show.toString();
        if (current.isEmpty() && operator.equals("-")) {
            show.append(operator);
            updateDisplay();
            return;
        }
        if (!current.isEmpty()) {
            char lastChar = current.charAt(current.length() - 1);
            if (isOperator(lastChar)) {
                if (lastChar == '-' && operator.equals("-") && current.length() > 1 && isOperator(current.charAt(current.length() - 2))) {
                    return;
                }
                show.setLength(show.length() - 1);
            }
            show.append(operator);
            signal = 0;
            updateDisplay();
        }
    }

    private void applyPercent() {
        if (!show.toString().isEmpty()) {
            String current = show.toString();
            char lastChar = current.charAt(current.length() - 1);
            if (!isOperator(lastChar) && lastChar != '%') {
                show.append("%");
                updateDisplay();
            }
        }
    }

    private void calculateResult() {
        if (show.toString().isEmpty()) return;
        signal = 1;
        String input = show.toString();
        if (isOperator(input.charAt(input.length() - 1))) {
            show.append("0");
            input = show.toString();
        }
        String resultStr = calculate(input);
        history.add(input + " = " + resultStr);
        show.setLength(0);
        show.append(resultStr);
        updateDisplay();
    }

    private void updateDisplay() {
        result.setText(show);
        result.setSelection(result.getText().length());
    }

    private void showHistoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("计算历史");

        ScrollView scrollView = new ScrollView(this);
        TextView historyTextView = new TextView(this);

        GradientDrawable scrollDrawable = new GradientDrawable();
        scrollDrawable.setShape(GradientDrawable.RECTANGLE);
        scrollDrawable.setCornerRadius(16f);
        scrollDrawable.setColor(Color.parseColor("#F5F5F5"));
        scrollDrawable.setStroke(2, Color.parseColor("#CCCCCC"));
        scrollView.setBackground(scrollDrawable);
        scrollView.setPadding(16, 16, 16, 16);

        historyTextView.setTextSize(18);
        historyTextView.setTextColor(Color.parseColor("#333333"));
        historyTextView.setPadding(16, 16, 16, 16);
        historyTextView.setLineSpacing(8f, 1.0f);

        updateHistoryText(historyTextView);
        scrollView.addView(historyTextView);
        builder.setView(scrollView);

        TextView titleView = new TextView(this);
        titleView.setText("计算历史记录");
        titleView.setTextSize(20);
        titleView.setTextColor(Color.parseColor("#1976D2"));
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setPadding(16, 16, 16, 0);
        builder.setCustomTitle(titleView);

        builder.setPositiveButton("关闭", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("清除", (dialog, which) -> {
            history.clear();
            updateHistoryText(historyTextView);
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1976D2"));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#D32F2F"));
        });
        dialog.getWindow().setBackgroundDrawable(createDialogBackground());
        dialog.show();
    }

    private void updateHistoryText(TextView historyTextView) {
        StringBuilder historyText = new StringBuilder();
        if (history.isEmpty()) {
            historyText.append("No history available.");
        } else {
            for (String entry : history) {
                historyText.append(entry).append("\n");
            }
        }
        historyTextView.setText(historyText.toString());
    }

    private GradientDrawable createDialogBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(20f);
        drawable.setColor(Color.WHITE);
        drawable.setStroke(1, Color.parseColor("#B0BEC5"));
        return drawable;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isLastOperator(String str) {
        if (str.isEmpty()) return false;
        char last = str.charAt(str.length() - 1);
        return isOperator(last);
    }

    private boolean hasDecimalInLastNumber(String str) {
        int lastOperatorIndex = -1;
        for (int i = str.length() - 1; i >= 0; i--) {
            if (isOperator(str.charAt(i)) || str.charAt(i) == '%') {
                lastOperatorIndex = i;
                break;
            }
        }
        String lastNumber = str.substring(lastOperatorIndex + 1);
        return lastNumber.contains(".");
    }

    private boolean operatorPriorityCompare(char operator1, char operator2) {
        int o1 = (operator1 == '+' || operator1 == '-') ? 0 : 1;
        int o2 = (operator2 == '+' || operator2 == '-') ? 0 : 1;
        return o1 > o2;
    }

    public static Double Add(Double d1, Double d2) {
        if (Double.isInfinite(d1) || Double.isInfinite(d2) || Double.isNaN(d1) || Double.isNaN(d2)) {
            return d1 + d2;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.add(b2).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static Double Sub(Double d1, Double d2) {
        if (Double.isInfinite(d1) || Double.isInfinite(d2) || Double.isNaN(d1) || Double.isNaN(d2)) {
            return d1 - d2;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.subtract(b2).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static Double Mul(Double d1, Double d2) {
        if (Double.isInfinite(d1) || Double.isInfinite(d2) || Double.isNaN(d1) || Double.isNaN(d2)) {
            return d1 * d2;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.multiply(b2).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static Double Div(Double d1, Double d2) {
        if (Double.isInfinite(d1) || Double.isInfinite(d2) || Double.isNaN(d1) || Double.isNaN(d2)) {
            return d1 / d2;
        }
        if (d2 == 0.0) {
            return d1 >= 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.divide(b2, 8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private String calculate(String input) {
        List<String> operator = new ArrayList<>();
        List<Double> operand = new ArrayList<>();
        StringBuilder tempNum = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '-' && (i == 0 || isOperator(input.charAt(i - 1)))) {
                tempNum.append(c);
            } else if (Character.isDigit(c) || c == '.') {
                tempNum.append(c);
            } else if (c == '%') {
                if (tempNum.length() > 0) {
                    double value = Double.parseDouble(tempNum.toString()) / 100;
                    operand.add(value);
                    tempNum.setLength(0);
                }
            } else if (isOperator(c)) {
                if (tempNum.length() > 0) {
                    operand.add(Double.parseDouble(tempNum.toString()));
                    tempNum.setLength(0);
                }
                while (!operator.isEmpty() && !operatorPriorityCompare(c, operator.get(operator.size() - 1).charAt(0))) {
                    String op = operator.remove(operator.size() - 1);
                    Double b = operand.remove(operand.size() - 1);
                    Double a = operand.remove(operand.size() - 1);
                    operand.add(performOperation(a, b, op.charAt(0)));
                }
                operator.add(String.valueOf(c));
            }
        }
        if (tempNum.length() > 0) {
            operand.add(Double.parseDouble(tempNum.toString()));
        }

        while (!operator.isEmpty()) {
            String op = operator.remove(operator.size() - 1);
            Double b = operand.remove(operand.size() - 1);
            Double a = operand.remove(operand.size() - 1);
            operand.add(performOperation(a, b, op.charAt(0)));
        }

        Double result = operand.get(0);
        if (Double.isNaN(result)) return "NaN";
        if (result == Double.POSITIVE_INFINITY) return "∞";
        if (result == Double.NEGATIVE_INFINITY) return "-∞";
        if (result % 1 == 0) {
            return String.valueOf(result.longValue());
        }
        return String.valueOf(result);
    }

    private Double performOperation(Double a, Double b, char operator) {
        switch (operator) {
            case '+': return Add(a, b);
            case '-': return Sub(a, b);
            case '*': return Mul(a, b);
            case '/': return Div(a, b);
            default: throw new IllegalArgumentException("错误运算符: " + operator);
        }
    }
}