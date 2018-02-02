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

    private ICurrencyManager mCurrencyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isLandscape())
            setContentView(R.layout.activity_main_landscape);
        else
            setContentView(R.layout.activity_main);

        initializeSpinners();
        initializeViews();

        mCurrencyManager = new CurrencyManager(this);
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

    private void getCurrencies(){
        if(isNetworkAvailable()){
            String fromCountry = mFromCurrencySpinner.getSelectedItem().toString();
            String toCountry = mToCurrencySpinner.getSelectedItem().toString();
            if(!validateEntries(fromCountry, toCountry))
                return;
            try{
                double value = Double.valueOf(mValueText.getText().toString());
                mCurrencyManager.getCurrency(fromCountry, toCountry, value);
            }catch (NumberFormatException nfe){
                createToast(getString(R.string.enter_valid_number));
                Log.e(TAG_ERR, nfe.getMessage());
            }
        }else{
            Log.d(TAG_TEST, getString(R.string.no_network));
        }
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private boolean validateEntries(String fromCountry, String toCountry){
        if(fromCountry.equals(toCountry)){
            createToast(getString(R.string.select_different_currencies));
            return false;
        }

        return true;
    }

    @Override
    public void hereIsResult(double result) {
        Log.d(TAG_TEST, "This is the new amount: " + result);
        DecimalFormat formatter = new DecimalFormat(getString(R.string.two_decimals));
        mResult.setText(formatter.format(result));
    }

    private void createToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean isLandscape(){
        Configuration config = getResources().getConfiguration();
        return config.orientation == config.ORIENTATION_LANDSCAPE;
    }
}
