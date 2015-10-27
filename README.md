# android.wallet
uCoin Android client application.

##Developpement
Use Android Studio , and Android NDK (Native Developpement Kit) to be able to use TweetNaCl (a compact crypto library).

- [Android Studio](https://developer.android.com/sdk/index.html)
- [NDK (Native Developpement Kit)](https://developer.android.com/ndk/downloads/index.html)


- Install dependencies needed by kalium-jni compilation
```
sudo apt-get install build-essential libpcre3 libpcre3-dev libtool automake
```
- Using the instructions below, clone the source repository from GitHub and generate static libsodium for all Android architectures.

	First export the path of the android NDK previously installed
	```
	export ANDROID_NDK_HOME=/absolutepath/to/android-ndk
	```

	Then

	```
	git clone https://github.com/ucoin-io/android.wallet
	cd android.wallet
	git submodule init
	git submodule sync
	git submodule update
	cd kalium-jni/src/main/jni/libsodium
	./autogen.sh
	./dist-build/android-arm.sh
	./dist-build/android-mips.sh
	./dist-build/android-x86.sh
	cd ~

	```

- Configure the Android Studio project : edit the local.properties file, used by Gradle
    ```
    sdk.dir=/absolutepath/to/android-sdk
    ndk.dir=/absolutepath/to/android-ndk
    ```