package com.example.tiptime

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.tiptime.databinding.ActivityMainBinding
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    // The lateinit keyword is something new. It's a promise that your code will initialize the variable before using it. If you don't, your app will crash.
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This line initializes the binding object which you'll use to access Views in the activity_main.xml layout.
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Set the content view of the activity. Instead of passing the resource ID of the layout, R.layout.activity_main, this specifies the root of the hierarchy of views in your app, binding.root.
        setContentView(binding.root)
        binding.calculateButton.setOnClickListener{ calculateTip() }
        binding.costOfServiceEditText.setOnKeyListener { view,keyCode, _ -> handleKeyEvent(view, keyCode) }
    }
    private fun calculateTip() {
        //costOfService.text is Editable type -> toString
        val stringInTextField = binding.costOfServiceEditText.text.toString()
        // return Null if stringInTextField is empty
        val cost = stringInTextField.toDoubleOrNull()
        // if cost is null the out
        if (cost == null) {
            // clear previous result
            binding.tipResult.text = ""
            return
        }
        val tipPercentage = when (binding.tipOptions.checkedRadioButtonId) {
            R.id.option_twenty_percent -> 0.20
            R.id.option_eighteen_percent -> 0.18
            else -> 0.15
        }
        var tip = tipPercentage * cost
        if (binding.roundUpSwitch.isChecked) {
            tip = kotlin.math.ceil(tip)
        }
        displayTip(tip)
    }
    private fun displayTip(tip : Double) {
        // Format the tip: currentcy. Choose NumberFormat (java.text)
        // Open strings.xml (app > res > values > strings.xml)
        // Change the tip_amount string from Tip Amount to Tip Amount: %s.
        val formattedTip = NumberFormat.getCurrencyInstance().format(tip)
        binding.tipResult.text = getString(R.string.tip_amount, formattedTip)
        /*Remove the line with the android:text attribute.
        android:text="@string/tip_amount"
        Add a line for the tools:text attribute set to Tip Amount: $10.
        tools:text="Tip Amount: $10"*/
    }

    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}