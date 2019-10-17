package com.bove.martin.udemyfinalapp;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Created by Martín Bove on 03/10/2019.
 * E-mail: mbove77@gmail.com
 */
public class App extends Application {

        static {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
}
