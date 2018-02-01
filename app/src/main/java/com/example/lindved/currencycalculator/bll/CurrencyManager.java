package com.example.lindved.currencycalculator.bll;

import android.util.Log;

import com.example.lindved.currencycalculator.gui.IGUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lindved on 01-02-2018.
 */

public class CurrencyManager implements ICurrencyManager {

    private static final String TAG = "TEST";
    private static final String CURRENCY_API_URL = "https://api.fixer.io/latest?base=";

    private IGUI mContext;

    private String mToCurrency;
    private double mAmount;

    public CurrencyManager(IGUI context){
        mContext = context;
    }

    public void getCurrency(String fromCurrency, String toCurrency, double amount){
        mToCurrency = toCurrency;
        mAmount = amount;
        getCurrencies(fromCurrency);
    }

    private void getCurrencies(String fromCurrency){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(CURRENCY_API_URL + fromCurrency).build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //TODO RKL: Alert user about failure.
                Log.d(TAG, "Response Failed: " + e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Response Success");
                String jsonData = response.body().string();
                Log.d(TAG, jsonData);
                try {
                    double currency = extractCurrency(jsonData);
                    double newValue = calculateNewValue(currency);
                    mContext.hereIsResult(newValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO RKL: Notify user
                }
            }
        });
    }

    private double extractCurrency(String jsonData) throws JSONException {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject rates = jsonObject.getJSONObject("rates");
            double rate = rates.getDouble(mToCurrency);
            Log.d(TAG, "This is the rate: " + rate);
            return rate;
    }

    private double calculateNewValue(double currency){
        return mAmount * currency;
    }
}
