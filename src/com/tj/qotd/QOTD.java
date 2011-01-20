package com.tj.qotd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class QOTD extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    /** Each time we show the activity, we display the current quote */
    @Override
    public void onStart()
    {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String quote = extras.getString("quote");
        TextView text = (TextView) findViewById(R.id.activity_quote_text);
        text.setText(quote);
        
        super.onStart();
    }
}