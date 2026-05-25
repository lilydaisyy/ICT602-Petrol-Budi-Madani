package com.example.budimadani;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class Home extends Fragment {

    private static final double BUDI_SUBSIDY_RATE = 1.99;

    private MaterialButtonToggleGroup togglePetrolType;
    private MaterialButtonToggleGroup toggleInputMode;
    private TextInputLayout tilMainInput;
    private TextInputEditText etMainInput, etPetrolPrice;
    private MaterialSwitch switchBudi;
    private Button btnCalculate;

    private TextView tvTotalCost, tvTotalCostLabel;
    private View cardSummary;
    private TextView tvSummaryLiters, tvSummaryPumpPrice, tvSummaryBudiPrice;

    // References to your individual fuel buttons for custom background tints
    private MaterialButton btnRON95, btnRON97, btnDiesel;

    public Home() {
        // Required empty constructor
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home, container, false);

        togglePetrolType = view.findViewById(R.id.togglePetrolType);
        toggleInputMode = view.findViewById(R.id.toggleInputMode);
        tilMainInput = view.findViewById(R.id.tilMainInput);
        etMainInput = view.findViewById(R.id.etMainInput);
        etPetrolPrice = view.findViewById(R.id.etPetrolPrice);
        switchBudi = view.findViewById(R.id.switchBudi);
        btnCalculate = view.findViewById(R.id.btnCalculate);

        tvTotalCostLabel = view.findViewById(R.id.tvTotalCostLabel);
        tvTotalCost = view.findViewById(R.id.tvTotalCost);

        cardSummary = view.findViewById(R.id.cardSummary);
        tvSummaryLiters = view.findViewById(R.id.tvSummaryLiters);
        tvSummaryPumpPrice = view.findViewById(R.id.tvSummaryPumpPrice);
        tvSummaryBudiPrice = view.findViewById(R.id.tvSummaryBudiPrice);

        // Link button views
        btnRON95 = view.findViewById(R.id.btnRON95);
        btnRON97 = view.findViewById(R.id.btnRON97);
        btnDiesel = view.findViewById(R.id.btnDiesel);

        // --- Handle Petrol Type Selection and Background Coloring ---
        updateButtonColors(togglePetrolType.getCheckedButtonId()); // Initial state on load

        togglePetrolType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateButtonColors(checkedId);

                // Safe-check: Turn off BUDI switch if user picks anything other than RON95
                if (checkedId != R.id.btnRON95 && switchBudi.isChecked()) {
                    Toast.makeText(requireContext(), "BUDI MADANI is strictly for RON95 users.", Toast.LENGTH_SHORT).show();
                    switchBudi.setChecked(false);
                }
            }
        });

        toggleInputMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnModeLiters) {
                    tilMainInput.setHint("Total Fuel (Liters)");
                    tvTotalCostLabel.setText("Estimated Final Cost");
                } else {
                    tilMainInput.setHint("Total Spend (RM)");
                    tvTotalCostLabel.setText("Total Spend");
                }

                etMainInput.setText("");
                cardSummary.setVisibility(View.GONE);
                tvTotalCost.setText("RM 0.00");
            }
        });

        btnCalculate.setOnClickListener(v -> calculatePetrolCost());

        switchBudi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && togglePetrolType.getCheckedButtonId() != R.id.btnRON95) {
                Toast.makeText(requireContext(), "BUDI MADANI is strictly for RON95 users.", Toast.LENGTH_SHORT).show();
                switchBudi.setChecked(false);
                return;
            }

            if (!TextUtils.isEmpty(etMainInput.getText()) && !TextUtils.isEmpty(etPetrolPrice.getText())) {
                calculatePetrolCost();
            }
        });

        return view;
    }

    /**
     * Updates selected toggle backgrounds: Yellow for RON95, Green for RON97, Gray for Diesel
     */
    private void updateButtonColors(int checkedId) {
        ColorStateList transparent = ColorStateList.valueOf(Color.TRANSPARENT);
        ColorStateList defaultTextColor = ColorStateList.valueOf(Color.parseColor("#333333")); // standard unselected dark text

        // Reset backgrounds and text to default state
        btnRON95.setBackgroundTintList(transparent);
        btnRON97.setBackgroundTintList(transparent);
        btnDiesel.setBackgroundTintList(transparent);

        btnRON95.setTextColor(defaultTextColor);
        btnRON97.setTextColor(defaultTextColor);
        btnDiesel.setTextColor(defaultTextColor);

        // Apply dynamic styling based on what's currently active
        if (checkedId == R.id.btnRON95) {
            btnRON95.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFD54F"))); // Yellow
            btnRON95.setTextColor(Color.BLACK);
        } else if (checkedId == R.id.btnRON97) {
            btnRON97.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
            btnRON97.setTextColor(Color.WHITE);
        } else if (checkedId == R.id.btnDiesel) {
            btnDiesel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#757575"))); // Gray
            btnDiesel.setTextColor(Color.WHITE);
        }
    }

    private void calculatePetrolCost() {
        String inputText = etMainInput.getText().toString().trim();
        String priceText = etPetrolPrice.getText().toString().trim();

        if (TextUtils.isEmpty(inputText)) {
            etMainInput.setError("Required");
            etMainInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceText)) {
            etPetrolPrice.setError("Required");
            etPetrolPrice.requestFocus();
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputText);
            double petrolPrice = Double.parseDouble(priceText);

            if (petrolPrice <= 0 || inputValue < 0) {
                Toast.makeText(requireContext(), "Please enter valid numbers > 0", Toast.LENGTH_SHORT).show();
                return;
            }

            int checkedPetrolId = togglePetrolType.getCheckedButtonId();
            boolean isEligible = switchBudi.isChecked();

            double fuelUsage = 0;
            double totalPetrolCost = 0;

            if (toggleInputMode.getCheckedButtonId() == R.id.btnModeLiters) {
                fuelUsage = inputValue;
                totalPetrolCost = fuelUsage * petrolPrice;
            } else {
                double effectivePricePerLiter = petrolPrice;

                if (checkedPetrolId == R.id.btnRON95 && isEligible) {
                    effectivePricePerLiter = petrolPrice - BUDI_SUBSIDY_RATE;
                    if (effectivePricePerLiter <= 0) effectivePricePerLiter = 0.01;
                }

                fuelUsage = inputValue / effectivePricePerLiter;
                totalPetrolCost = fuelUsage * petrolPrice;
            }

            double budiRebate = 0.00;

            if (checkedPetrolId == R.id.btnRON95 && isEligible) {
                budiRebate = fuelUsage * BUDI_SUBSIDY_RATE;
                tvSummaryBudiPrice.setText(String.format(Locale.getDefault(), "RM %.2f/L", BUDI_SUBSIDY_RATE));
            } else {
                tvSummaryBudiPrice.setText(isEligible ? "Not Eligible" : "Not Applied");
            }

            double finalPayable = totalPetrolCost - budiRebate;
            if (finalPayable < 0) finalPayable = 0;

            tvTotalCost.setText(String.format(Locale.getDefault(), "RM %.2f", finalPayable));
            tvSummaryLiters.setText(String.format(Locale.getDefault(), "%.3f L", fuelUsage));
            tvSummaryPumpPrice.setText(String.format(Locale.getDefault(), "RM %.2f/L", petrolPrice));
            cardSummary.setVisibility(View.VISIBLE);

            if (budiRebate > 0) {
                Toast.makeText(requireContext(),
                        String.format(Locale.getDefault(), "Subsidy Applied! You saved RM %.2f", budiRebate),
                        Toast.LENGTH_LONG).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }
}
