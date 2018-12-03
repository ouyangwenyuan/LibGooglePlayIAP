对google play 的 内购 iap的一个封装，方便集成和使用
PaymentManager API：
```java
 	/**
     * 初始化 支付服务
     *
     * @param context      Context
     * @param iapKey       iab Google后台配置的key
     * @param productItems 配置的商品名列表
     */
    public void setupPurchases(final Context context, String iapKey, final List<String> productItems)；
    
        
   /**
     * 检查商品购买情况
     *
     * @param productItems         配置的商品名列表
     * @param gotInventoryListener
     */
    public void checkPurchases(List<String> productItems, IabHelper.QueryInventoryFinishedListener gotInventoryListener)

   /**
     * 支付
     *
     * @param mActivity                发起支付的activity
     * @param purchaseId               支付条目字符串
     * @param purchaseFinishedListener 支付回调
     */
    public void purchase(Activity mActivity, String purchaseId, PurchaseFinishedListener purchaseFinishedListener)；
    
 ```
使用方法

#### Step 1. Add the JitPack repository to your build file , Add it in your root build.gradle at the end of repositories:

```gradle
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
#### Step 2. Add the dependency
 
 ```gradle
  dependencies {
	        implementation 'com.github.ouyangwenyuan:LibGooglePlayIAP:Tag'
	}
```
#### Step3. 在App启动的时候初始化IAP库
```java
 	List purchareItems = new ArrayList<>();
        purchareItems.add(PUCHASE_WEEKLY_ITEM);
        purchareItems.add(PUCHASE_MONTHLY_ITEM);
        purchareItems.add(PUCHASE_YEARLY_ITEM);
        PaymentManager.getInstance().setupPurchases(this, IPA_KEY, purchareItems);
```
#### Step4. 在支付的时候调用purchase 方法
```java
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
```
 
