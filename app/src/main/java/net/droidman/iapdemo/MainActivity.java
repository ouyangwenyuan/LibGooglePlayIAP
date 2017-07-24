package net.droidman.iapdemo;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;

import payment.PaymentManager;
import payment.utils.IabHelper;
import payment.utils.IabResult;
import payment.utils.Inventory;
import payment.utils.MyLog;
import payment.utils.Purchase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //BOTOKEYBOARD KEY
    //private static String IPA_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0pqucI4TptQPeTKJSNZeg5HWFsh/MRxwpvnVq9rYDL3wkH2yrSr4Lff8tP1RM3ilXmeMgbpAg1Ef2KEVdddFjp0JA8EwlRJCBq0MMle8fYh5mcElbVWY6F3shvL5Kgw9s7iy1mjtGHInfWsqEt2+Zif0+284FmiLR3hfZBheeNZWS2+WNg2kaLQQRZuHELIqcrN+QiU3C2OKcyg2pTAiVzhU6FbpAJuNTSBBAgUxg9N626H2uxNPL+WGM2wGfGameRjQB9g6/fa49cMfwaoGxhBnlgQ1roqfcj4/6xtgSOsumuU18vvZWBd3nNjXIx1fnekKYmhSN4Gyk5U8mIoIyQIDAQAB";
    // enable debug logging (for a production application, you should set this to false).

    private static String IPA_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzBjdRdNB/cSx4C6ZjMbjNyj/gAo4h1uQ7lBEoWEX4CCMae2+xlFMhuJ0wGzuvyiHvMQYEmSSG4QL1nZ7YL0pcpzQwk0OZdNOwwisJd18JWMxGVqn41XvK4M5xuk/fp3oPZKZYjDVIUWO2hSHEDgjXcDA7DXFecxKQvOw6x/vRkOistujY9yeuDBOekTTo5xDjtli6u+FrVpU0szqJgyeTijT73hkJ1oc/BSC0D8p1ghuf2sSIYWD7eKGd58MubijNsezc7ZvhoQOiuWRfW/I1M8hMJlxu9qFRD2Xez4rI3LuXxfv5dgg30rnB+XNEo4nfp5EF3dU5DngUpaK5Rm8owIDAQAB";
    public static String PUCHASE_WEEKLY_ITEM = "com.tbs.piano.servenday.experience"; //"boto_keyboard_remove_ad"; //
    public static String PUCHASE_MONTHLY_ITEM = "com.tbs.piano.monthly.membership";
    public static String PUCHASE_YEARLY_ITEM = "com.tbs.piano.yearly.membership";
    private List<String> purchareItems;

    String purchaseId = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_week_iap).setOnClickListener(this);
        findViewById(R.id.bt_month_iap).setOnClickListener(this);
        findViewById(R.id.bt_year_iap).setOnClickListener(this);
        findViewById(R.id.bt_restore).setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("querying ...");

        purchareItems = new ArrayList<>();
        purchareItems.add(PUCHASE_WEEKLY_ITEM);
        purchareItems.add(PUCHASE_MONTHLY_ITEM);
        purchareItems.add(PUCHASE_YEARLY_ITEM);
        PaymentManager.getInstance().setupPurchases(this, IPA_KEY, purchareItems);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_week_iap:
                purchaseId = PUCHASE_WEEKLY_ITEM;
                break;
            case R.id.bt_month_iap:
                purchaseId = PUCHASE_MONTHLY_ITEM;
                break;
            case R.id.bt_year_iap:
                purchaseId = PUCHASE_YEARLY_ITEM;
                break;
            case R.id.bt_restore:
                purchaseId = "";
                break;
        }

        if (!TextUtils.isEmpty(purchaseId)) {
//            progressDialog.show();
            PaymentManager.getInstance().purchase(this, purchaseId, new PaymentManager.PurchaseFinishedListener() {
                @Override
                public void success(IabResult result, Purchase purchase) {
                    MyLog.i(purchase + ",success " + result);
                    try {
                        showToast("success" + result);
//                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failure(IabResult result, Purchase purchase) {
                    MyLog.i(purchase + ",fail " + result);
                    try {
                        showToast("failure" + result);
//                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            restorePurchase();
        }
    }

    private void restorePurchase() {
        progressDialog.show();
        PaymentManager.getInstance().checkPurchases(purchareItems, new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                try {
                    progressDialog.dismiss();
                    if (result == null) {
                        showToast("query fail, maybe no google play");
                    } else {
                        if (result.isSuccess() && inv != null) {
                            for (String item : purchareItems) {
                                if (inv.hasPurchase(item)) {
                                    MyLog.i("purchase:" + inv.getPurchase(item).toString());
                                }
                                if (inv.hasDetails(item)) {
                                    MyLog.i("skudetail:" + inv.getSkuDetails(item).toString());
                                }
                            }
                        } else {
                            showToast("query fail, result:" + result);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
