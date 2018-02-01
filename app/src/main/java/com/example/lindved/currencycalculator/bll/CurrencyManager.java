package com.example.lindved.currencycalculator.bll;

import android.content.Context;
import android.util.Log;

import com.example.lindved.currencycalculator.gui.IGUI;

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

    public CurrencyManager(IGUI context){
        mContext = context;
    }

    public void getCurrency(String fromCurrency, String toCurrency){
        mToCurrency = toCurrency;
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

            }
        });
    }


}
