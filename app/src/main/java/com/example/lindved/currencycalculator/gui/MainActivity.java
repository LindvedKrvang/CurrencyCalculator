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
    private TextView mResultText;
    private TextView mExchangeRate;
    private TextView mConvertText;

    private ICurrencyManager mCurrencyManager;
    private DecimalFormat mFormatter;

    private double mRate;
    private double mValue;
    private double mResult;
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

        mCurrencyManager = new CurrencyManager(this);
        mFormatter = new DecimalFormat(getString(R.string.two_decimals));

        initializeSpinners();
        initializeViews();

        loadSavedInstanceState(savedInstanceState);

        updateUIElements();
    }

    private void loadSavedInstanceState(Bundle state){
        if(state == null){
            mFromCountry = mFromCurrencySpinner.getSelectedItem().toString();
            mToCountry = mToCurrencySpinner.getSelectedItem().toString();
            return;
        }
        mRate = state.getDouble(getString(R.string.Rate));
        mValue = state.getDouble(getString(R.string.value));
        mResult = state.getDouble(getString(R.string.result));
        mFromCountry = state.getString(getString(R.string.from_country));
        mToCountry = state.getString(getString(R.string.to_country));
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
        mResultText = findViewById(R.id.txtResult);
    }

    public void OnCalculateButtonClick(View view){
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
            createToast(getString(R.string.no_network));
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
        mResult = result;
        mRate = exchangeRate;
        updateUIElements();
    }

    /**
     * Updates all the views in the GUI with their respective information.
     */
    private void updateUIElements(){
        String resultText = mFormatter.format(mResult) + " " + mToCountry;
        mResultText.setText(resultText);
        if(isLandscape()) {
            String exchangeText = getString(R.string.exchange_rate_is)+ " " + mFormatter.format(mRate);
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

    /**
     * Saves all the necessary information for the activity to restart without loss of  valuable information.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(getString(R.string.Rate), mRate);
        outState.putDouble(getString(R.string.value), mValue);
        outState.putDouble(getString(R.string.result), mResult);
        outState.putString(getString(R.string.from_country), mFromCountry);
        outState.putString(getString(R.string.to_country), mToCountry);
    }
}
