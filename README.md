对google play 的 内购 iap的一个封装，方便集成和使用

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
