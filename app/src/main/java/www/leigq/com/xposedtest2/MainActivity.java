package www.leigq.com.xposedtest2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import www.leigq.com.xposedtest2.util.SharedPreferencesUtil;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckBox checkBox = findViewById(R.id.autoCB);

        final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance(this.getApplicationContext());

        sharedPreferencesUtil.putSP("auto", "yes");
        checkBox.setChecked(true);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            private static final String TAG = "WeChart";

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferencesUtil.putSP("auto", "yes");
                } else {
                    sharedPreferencesUtil.putSP("auto", "no");
                }

                Log.d(TAG, "onCheckedChanged: " + sharedPreferencesUtil.getSP("auto"));
            }
        });
    }
}
