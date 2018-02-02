package com.example.lindved.currencycalculator.gui;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lindved.currencycalculator.R;
import com.example.lindved.currencycalculator.bll.CurrencyManager;
import com.example.lindved.currencycalculator.bll.ICurrencyManager;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements IGUI {

    private static final String TAG_TEST = "TEST";
    private static final String TAG_ERR = "ERROR";

    private Spinner mFromCurrencySpinner;
    private Spinner mToCurrencySpinner;
    private EditText mValueText;
    private TextView mResult;
    private TextView mExchangeRate;
    private TextView mConvertText;

    private ICurrencyManager mCurrencyManager;
    private double mRate;
    private double mValue;
    private DecimalFormat formatter;
    private String mFromCountry;
    private String mToCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isLandscape()){
            setContentView(R.layout.activity_main_landscape);
            initializeLandscapeViews();
        }
        else
            setContentView(R.layout.activity_main);

        initializeSpinners();
        initializeViews();

        mCurrencyManager = new CurrencyManager(this);
        formatter = new DecimalFormat(getString(R.string.two_decimals));
    }

    private void initializeLandscapeViews(){
        mExchangeRate = findViewById(R.id.txtExchangeRate);
        mConvertText = findViewById(R.id.txtConvert);
    }

    private void initializeSpinners(){
        mFromCurrencySpinner = findViewById(R.id.spnFromCurrency);
        mToCurrencySpinner = findViewById(R.id.spnToCurrency);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countries_currency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mFromCurrencySpinner.setAdapter(adapter);
        mToCurrencySpinner.setAdapter(adapter);
    }

    private void initializeViews(){
        mValueText = findViewById(R.id.etxtCurrency);
        mResult = findViewById(R.id.txtResult);
    }

    public void OnClickTest(View view){
        getCurrencies();
    }

    /**
     * Calls the CurrencyManager to get the currency requested.
     */
    private void getCurrencies(){
        if(isNetworkAvailable()){
            mFromCountry = mFromCurrencySpinner.getSelectedItem().toString();
            mToCountry = mToCurrencySpinner.getSelectedItem().toString();
            if(!validateEntries(mFromCountry, mToCountry))
                return;
            try{
                mValue = Double.valueOf(mValueText.getText().toString());
                mCurrencyManager.getCurrency(mFromCountry, mToCountry, mValue);
            }catch (NumberFormatException nfe){
                createToast(getString(R.string.enter_valid_number));
                Log.e(TAG_ERR, nfe.getMessage());
            }
        }else{
            Log.d(TAG_TEST, getString(R.string.no_network));
        }
    }

    /**
     * Returns true if the app has access to the network.
     * @return
     */
    private boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * Returns true if the two countries select is not the same.
     * @param fromCountry
     * @param toCountry
     * @return
     */
    private boolean validateEntries(String fromCountry, String toCountry){
        if(fromCountry.equals(toCountry)){
            createToast(getString(R.string.select_different_currencies));
            return false;
        }

        return true;
    }

    /**
     * Updates the GUI to show the result.
     * @param result
     * @param exchangeRate
     */
    @Override
    public void hereIsResult(double result, double exchangeRate) {
        Log.d(TAG_TEST, "This is the new amount: " + result);
        mRate = exchangeRate;

        String resultText = formatter.format(result) + " " + mToCountry;
        mResult.setText(resultText);
        if(isLandscape()){
            String exchangeText = "The exchange rate is: " + formatter.format(mRate);
            mExchangeRate.setText(exchangeText);
            String convertText = mValue + " " + mFromCountry + " is:";
            mConvertText.setText(convertText);
        }
    }

    /**
     * Creates a toast with the parsed message.
     * @param message
     */
    private void createToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Returns true if app is in landscape mode.
     * @return
     */
    private boolean isLandscape(){
        Configuration config = getResources().getConfiguration();
        return config.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
