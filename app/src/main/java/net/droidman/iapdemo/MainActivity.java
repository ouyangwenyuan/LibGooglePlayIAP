package net.droidman.iapdemo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      findViewById(R.id.bt_friendlist).setOnClickListener(this);
      findViewById(R.id.bt_invited).setOnClickListener(this);
      findViewById(R.id.bt_shared).setOnClickListener(this);
      findViewById(R.id.bt_select_friend).setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
  }
}
