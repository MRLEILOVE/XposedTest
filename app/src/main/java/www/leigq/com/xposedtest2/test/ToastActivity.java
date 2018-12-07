package www.leigq.com.xposedtest2.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import www.leigq.com.xposedtest2.R;

public class ToastActivity extends AppCompatActivity {

    private Button button;

    private void initView() {
        button = findViewById(R.id.toastBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);
        initView();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ToastActivity.this, getToastMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getToastMsg() {
        return "Toast未被拦截";
    }
}
