package payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import payment.utils.MyLog;
import payment.utils.IabHelper;
import payment.utils.IabResult;
import payment.utils.Purchase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fotoable on 2016/11/4.
 */

public class PaymentManager {

    public interface PurchaseFinishedListener {

        void success(IabResult result, Purchase purchase);

        void failure(IabResult result, Purchase purchase);
    }

    private PurchaseFinishedListener purchaseFinishedListener;


    private static IabHelper mHelper;

    // (arbitrary) request code for the purchase flow
    public static final int RC_REQUEST = 10001;

    public static final int PAY_REQUEST = 10002;

    private static PaymentManager mPaymentUtil = null;

    // Does the user have an active subscription to the infinite gas plan?
//    private boolean mSubscribedToInfiniteGas = false;

    // SKU for our subscription (infinite gas)
//    private static String SKU_premium_test_month = "";
    private static String currentPurchaseId = "";

    private String iapKey = "";
    private boolean setupSuccess = false;


    public static PaymentManager getInstance() {
        if (mPaymentUtil == null) {
            mPaymentUtil = new PaymentManager();
        }
        return mPaymentUtil;
    }

    /**
     * 初始化 支付服务
     *
     * @param context      Context
     * @param iapKey       iab Google后台配置的key
     * @param productItems 配置的商品名列表
     */
    public void setupPurchases(final Context context, String iapKey, final List<String> productItems) {
        this.iapKey = iapKey;
        mHelper = new IabHelper(context, iapKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                MyLog.d("iabresult:" + result);
                setupSuccess = result.isSuccess();
                if (setupSuccess) {
                    mHelper.queryInventoryAsync(true, productItems, null);
                }
            }
        });
    }

    public void checkPurchase(String premiumCommodity, IabHelper.QueryInventoryFinishedListener gotInventoryListener) {
        List<String> items = new ArrayList<>();
        items.add(premiumCommodity);
        checkPurchases(items, gotInventoryListener);
    }


    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }


    /**
     * 检查商品购买情况
     *
     * @param productItems         配置的商品名列表
     * @param gotInventoryListener
     */
    public void checkPurchases(List<String> productItems, IabHelper.QueryInventoryFinishedListener gotInventoryListener) {
        try {
            mHelper.queryInventoryAsync(true, productItems, gotInventoryListener);
        } catch (Exception e) {
            e.printStackTrace();
            gotInventoryListener.onQueryInventoryFinished(null, null);
        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */
        return true;
    }

    /**
     * 支付
     *
     * @param mActivity                发起支付的activity
     * @param purchaseId               支付条目字符串
     * @param purchaseFinishedListener 支付回调
     */
    public void purchase(Activity mActivity, String purchaseId, PurchaseFinishedListener purchaseFinishedListener) {
        this.purchaseFinishedListener = purchaseFinishedListener;
        currentPurchaseId = purchaseId;

        if (!setupSuccess || !mHelper.subscriptionsSupported()) {
            Toast.makeText(mActivity.getApplicationContext(), "Your device is not supported subscription.", Toast.LENGTH_SHORT).show();
            purchaseFinishedListener.failure(null, null);
            return;
        }
        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";
        MyLog.d("Launching purchase flow for preminu features subscription.");
        try {
            mHelper.launchPurchaseFlow(mActivity,
                    purchaseId,
                    IabHelper.ITEM_TYPE_INAPP,
                    RC_REQUEST,
                    new MyOnIabPurchaseFinishedListener(purchaseId),
                    payload);
        } catch (Exception e) {
            e.printStackTrace();
            purchaseFinishedListener.failure(null, null);
        }

    }


    final class MyOnIabPurchaseFinishedListener implements IabHelper.OnIabPurchaseFinishedListener {
        private String currentPurchaseId;

        public MyOnIabPurchaseFinishedListener(String currentPurchaseId) {
            this.currentPurchaseId = currentPurchaseId;
        }

        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            MyLog.d("Purchase finished: -->  " + result + ", purchase: " + purchase);
            if (mHelper == null) {
                purchaseFinishedListener.failure(result, purchase);
                return;
            }

            if (result.isFailure()) {
                Log.e("Trivialpurchasing: --> ", " " + result);
                purchaseFinishedListener.failure(result, purchase);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                Log.e("TrivialDrivepurchasing", ". Authenticity verification failed. ");
                purchaseFinishedListener.failure(result, purchase);
                return;
            }

            if (purchase.getSku().equals(currentPurchaseId)) {
                purchaseFinishedListener.success(result, purchase);
            } else {
                purchaseFinishedListener.failure(result, purchase);
            }
        }
    }

}
