package com.simutech.landry.stechphone;

/**
 * Created by User on 11/07/2016.
*/
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import javax.sql.DataSource;

public class MailSender extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button send = (Button) this.findViewById(R.id.btnsend);
        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                try {
                    GMailSender sender = new GMailSender("@gmail.com", "");
                    sender.sendMail("This is Subject",
                            "This is Body",
                            "kateul@gmail.com",
                            "kateul@yahoo.fr");
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }

            }
        });

    }

}
