package com.example.lindved.currencycalculator.gui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.lindved.currencycalculator.R;
import com.example.lindved.currencycalculator.bll.CurrencyManager;
import com.example.lindved.currencycalculator.bll.ICurrencyManager;

public class MainActivity extends AppCompatActivity implements IGUI {

    private static final String TAG = "TEST";

    private Spinner mFromCurrencySpinner;
    private Spinner mToCurrencySpinner;

    private ICurrencyManager mCurrencyManager;

    private String mFromCountry;
    private String mToCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSpinners();
        mCurrencyManager = new CurrencyManager(this);
        getCurrencies();
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

    public void OnClickTest(View view){
        Log.d(TAG, "Button Clicked");

        //This is only for testing purpose. Must be removed!!
        mFromCountry = mFromCurrencySpinner.getSelectedItem().toString();
        mToCountry = mToCurrencySpinner.getSelectedItem().toString();
        mCurrencyManager.getCurrency(mFromCountry, mToCountry);
    }

    private void getCurrencies(){
        if(isNetworkAvailable()){
            mFromCountry = mFromCurrencySpinner.getSelectedItem().toString();
            mToCountry = mToCurrencySpinner.getSelectedItem().toString();


        }else{
            Log.d(TAG, getString(R.string.no_network));
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

    @Override
    public void hereIsResult(String result) {
        // TODO RKL: Show result.
        Log.d(TAG, result);
    }

//    private class CurrencyGetter extends AsyncTask{
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            mCurrencyManager.getCurrency(mFromCountry, mToCountry);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//        }
//    }
}
