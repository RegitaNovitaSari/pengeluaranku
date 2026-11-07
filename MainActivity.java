package com.umko.pengeluaranku;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private PrefsManager prefs;
    private ExpenseDbHelper db;

    private TextView tvGreeting, tvBudget, tvTotal, tvSisa;
    private SwitchCompat swDark;
    private EditText etUsername, etBudget, etTitle, etAmount;
    private ListView lvExpenses;

    private CompoundButton.OnCheckedChangeListener darkListener;
    private List<ExpenseDbHelper.Expense> expenses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new PrefsManager(this);
        db = new ExpenseDbHelper(this);

        AppCompatDelegate.setDefaultNightMode(prefs.isDarkMode()
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvBudget = findViewById(R.id.tvBudget);
        tvTotal = findViewById(R.id.tvTotal);
        tvSisa = findViewById(R.id.tvSisa);
        swDark = findViewById(R.id.swDark);
        etUsername = findViewById(R.id.etUsername);
        etBudget = findViewById(R.id.etBudget);
        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        lvExpenses = findViewById(R.id.lvExpenses);
        Button btnSaveName = findViewById(R.id.btnSaveName);
        Button btnSaveBudget = findViewById(R.id.btnSaveBudget);
        Button btnAdd = findViewById(R.id.btnAdd);

        darkListener = (button, checked) -> {
            prefs.setDarkMode(checked);
            AppCompatDelegate.setDefaultNightMode(checked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
        };
        swDark.setOnCheckedChangeListener(darkListener);

        btnSaveName.setOnClickListener(v -> {
            prefs.setUsername(etUsername.getText().toString().trim());
            loadSettings();
            Toast.makeText(this, "Nama tersimpan", Toast.LENGTH_SHORT).show();
        });

        btnSaveBudget.setOnClickListener(v -> {
            String s = etBudget.getText().toString().trim();
            if (s.isEmpty()) {
                Toast.makeText(this, "Isi budget dulu", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.setMonthlyBudget(Long.parseLong(s));
            loadSettings();
            refreshSummary();
            Toast.makeText(this, "Budget tersimpan", Toast.LENGTH_SHORT).show();
        });

        btnAdd.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            if (title.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Judul dan nominal wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            String date = new SimpleDateFormat("dd MMM yyyy", new Locale("in", "ID")).format(new Date());
            db.addExpense(title, Long.parseLong(amountStr), date);
            etTitle.setText("");
            etAmount.setText("");
            loadExpenses();
        });

        lvExpenses.setOnItemLongClickListener((parent, view, position, id) -> {
            db.deleteExpense(expenses.get(position).id);
            loadExpenses();
            Toast.makeText(this, "Pengeluaran dihapus", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
        loadExpenses();
    }

    private void loadSettings() {
        swDark.setOnCheckedChangeListener(null);
        swDark.setChecked(prefs.isDarkMode());
        swDark.setOnCheckedChangeListener(darkListener);

        String name = prefs.getUsername();
        etUsername.setText(name);
        tvGreeting.setText(name.isEmpty() ? "Halo!" : "Halo, " + name + "!");

        long budget = prefs.getMonthlyBudget();
        etBudget.setText(budget == 0 ? "" : String.valueOf(budget));
    }

    private void loadExpenses() {
        expenses = db.getAllExpenses();
        List<String> rows = new ArrayList<>();
        for (ExpenseDbHelper.Expense e : expenses) {
            rows.add(e.title + "\n" + rupiah(e.amount) + "  •  " + e.date);
        }
        lvExpenses.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, rows));
        refreshSummary();
    }

    private void refreshSummary() {
        long budget = prefs.getMonthlyBudget();
        long total = db.getTotal();
        long sisa = budget - total;

        tvBudget.setText("Budget bulanan: " + rupiah(budget));
        tvTotal.setText("Total pengeluaran: " + rupiah(total));
        tvSisa.setText(sisa >= 0
                ? "Sisa budget: " + rupiah(sisa)
                : "Melebihi budget: " + rupiah(-sisa));
    }

    private String rupiah(long value) {
        return "Rp " + NumberFormat.getNumberInstance(new Locale("in", "ID")).format(value);
    }
}