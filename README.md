# Zoom Ionic SDK for Ionic Angular <span align="center"><img src="https://s3-us-west-1.amazonaws.com/sdk.zoom.us/Community-Project.png" width="200px" max-height="100px" style="margin:auto;"/></span>

**Note: This is a community project initiated by Zoom. Zoom does not actively enhance the interfaces in the Ionic plugin. If you would like to contribute, please contact us or submit a pull request. Thanks! :)**

<div align="center">
<img src="https://s3.amazonaws.com/user-content.stoplight.io/8987/1541013063688" width="400px" max-height="400px" style="margin:auto;"/>
</div>

## Table of Contents
- [:rotating_light: Announcement :rotating_light:](#rotating_light-announcement-rotating_light)   
- [Latest SDK Notifications](#latest-sdk-notifications)   
- [Full Documentation && Community Support](#full-documentation-community-support)   
- [What is Zoom Ionic SDK?](#what-is-zoom-ionic-sdk)   
- [Disclaimer](#disclaimer)   
- [Getting Started](#getting-started)   
  - [Prerequisites](#prerequisites)   
  - [Installing](#installing)   
  - [Usage](#usage)   
  - [Build Note](#build-note)   
- [Simulator Support](#simulator-support)   
- [Documentation](#documentation)   
- [Versioning](#versioning)   
- [Change log](#change-log)
- [Frequently Asked Questions (FAQ)](#frequently-asked-questions-faq)     
- [Support](#support)   
- [License](#license)   
- [Acknowledgments](#acknowledgments)   


## :rotating_light: Announcement :rotating_light:
To align with Zoom’s [recent announcement](https://blog.zoom.us/wordpress/2020/04/22/zoom-hits-milestone-on-90-day-security-plan-releases-zoom-5-0/) pertaining to our security initiative, Zoom Client SDKs have added **AES 256-bit GCM encryption** support, which provides more protection for meeting data and greater resistance to tampering. **The system-wide account enablement of AES 256-bit GCM encryption will take place on June 01, 2020.** You are **strongly recommended** to start the required upgrade to this latest version 4.6.21666.0512 at your earliest convenience. Please note that any Client SDK versions below 4.6.21666.0512 will **no longer be operational** from June 01.

> If you would like to test the latest SDK with AES 256-bit GCM encryption meeting before 05/30, you may:
> 1. Download the latest version of Zoom client: https://zoom.us/download
> 2. Visit https://zoom.us/testgcm and launch a GCM enabled meeting with your Zoom client, you will see a Green Shield icon that indicates the GCM encryption is enabled
> 3. Use SDK to join this meeting

## Latest SDK Notifications
1. Please note that our Ionic SDK only support Ionic Angular.

## Full Documentation && Community Support
You can find the full Zoom Ionic SDK documentation and the community support forum here:
<div align="center">
   <a target="_blank" href="https://marketplace.zoom.us/docs/sdk/hybrid-frameworks/ionic" style="text-decoration:none">
   <img src="https://s3-us-west-1.amazonaws.com/sdk.zoom.us/Doc-button.png" width="350px" max-height="350px" style="margin:1vh 1vw;"/>
   </a>
   <a target="_blank" href="https://devforum.zoom.us/c/mobile-sdk" style="text-decoration:none">
   <img src="https://s3-us-west-1.amazonaws.com/sdk.zoom.us/Forum-button.png" width="350px" max-height="350px" style="margin:1vh 1vw;"/>
   </a>
</div>

## What is Zoom Ionic SDK?
Zoom SDK makes it easy to integrate Zoom with your mobile applications, and boosts up your applications with the power of Zoom.

* **Easy to use**: Our SDK is built to be easy to use. Just import the libraries, call a few functions, and we will take care all video conferencing related stuffs for you.
* **Localizable**: Our SDK naturally supports 7 major languages, so you can grow your applications internationally.

## Disclaimer

**Please be aware that all hard-coded variables and constants shown in the documentation and in the demo, such as Zoom Token, Zoom Access, Token, etc., are ONLY FOR DEMO AND TESTING PURPOSES. We STRONGLY DISCOURAGE the way of HARDCODING any Zoom Credentials (username, password, API Keys & secrets, SDK keys & secrets, etc.) or any Personal Identifiable Information (PII) inside your application. WE DON’T MAKE ANY COMMITMENTS ABOUT ANY LOSS CAUSED BY HARD-CODING CREDENTIALS OR SENSITIVE INFORMATION INSIDE YOUR APP WHEN DEVELOPING WITH OUR SDK**.

## Getting Started

The following instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
* For detailed instructions, please refer to our documentation website: [[https://marketplace.zoom.us/docs/sdk/hybrid-frameworks/ionic](https://marketplace.zoom.us/docs/sdk/hybrid-frameworks/ionic)];
* If you need support or assistance, please visit our [Zoom Developer Community Forum](https://devforum.zoom.us/);

### Prerequisites

Before you try out our SDK, you would need the following to get started:

* **A Zoom Account**: If you do not have one, you can sign up at [https://zoom.us/signup](https://zoom.us/signup).
  * Once you have your Zoom Account, sign up for a 60-days free trial at [https://marketplace.zoom.us/](https://marketplace.zoom.us/)
* **A mobile device**
  * Android
    * Android 5.0 (API Level 21) or later.
    * CPU: armeabi-v7a, x86, armeabi, arm64-v8a, x86_64
    * **compileSdkVersion**: 29+
    * **buildToolsVersion**: 29+
    * **minSdkVersion**: 21
    * **Required dependencies**
    ```
    implementation 'androidx.multidex:multidex:2.0.0'
    implementation 'androidx.recyclerview:recyclerview:1.0.0'
    implementation 'androidx.appcompat:appcompat:1.0.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'com.google.android.material:material:1.0.0-rc01'
    ```
  * iOS
    * iPhone or iPad
    * **npm@6.13.4+**
    * **ionic-cli@6.9.0+**
    * **ionic/angular@5.0.0+**
    * **ionic-native/core@5.25.0+**
    * **ionic-native/zoom@5.25.0+**



 If you are developing on Android, you will need to install the latest version of cordova-android(Since the current released version of cordova-android does not support Android SDK API Level 29, please install the latest version from their Github repo, which supports Android SDK API Level 29)
    ```
    ionic cordova platform add https://github.com/apache/cordova-android.git
    ```

 and install the following 2 plugins before you can use the Ionic SDK:
 ```
 ionic cordova plugin add cordova-plugin-androidx
 ionic cordova plugin add cordova-plugin-androidx-adapter
 ```

### Installing

Clone or download a copy of our SDK files from GitHub. After you unzipped the file, you should have the following folders:

```
.
├── README.md
├── libs
├── package.json
├── plugin.xml
├── src
└── www
```
In your Ionic application directory, run the following to install the plugin:
```
ionic cordova plugin add cordova.plugin.zoom
```


### Usage
0.  Import the plugin

```
import { Zoom } from '@ionic-native/zoom';

constructor(private zoomService: Zoom) { }
```
1. Initialize Zoom SDK
Initialize Zoom SDK, need to be called when app fired up.
```
this.zoomService.initialize(API_KEY, API_SECRET)
  .then((success: any) => console.log(success))
  .catch((error: any) => console.log(error));
```

2. Login
Log user in with Zoom username and password.
```
this.zoomService.login(userName, password)
  .then((success: any) => console.log(success))
  .catch((error: any) => console.log(error));
```
3. Logout
Log user out.
```
this.zoomService.logout()
  .then((success: boolean) => console.log(success))
  .catch((error: any) => console.log(error));
```

4. isLoggedIn
Check whether a user is logged in. Return true if the user is logged in. False if the user is not logged in.
```
this.zoomService.isLoggedIn()
  .then((success: boolean) => console.log(success))
  .catch((error: any) => console.log(error));
```

5. MeetingOptions
Meeting options. Configure the default meeting room. Some of the options are only available on Android.
```
let options = {
  custom_meeting_id: "Customized Title",
  no_share: false,
  no_audio: false,
  no_video: false,
  no_driving_mode: true,
  no_invite: true,
  no_meeting_end_message: true,
  no_dial_in_via_phone: false,
  no_dial_out_to_phone: false,
  no_disconnect_audio: true,
  no_meeting_error_message: true,
  no_unmute_confirm_dialog: true,    // Android only
  no_webinar_register_dialog: false, // Android only
  no_titlebar: false,
  no_bottom_toolbar: false,
  no_button_video: false,
  no_button_audio: false,
  no_button_share: false,
  no_button_participants: false,
  no_button_more: false,
  no_text_password: true,
  no_text_meeting_id: false,
  no_button_leave: false
 };
 ```

6. Join Meeting
Join meeting 
```
this.zoomService.joinMeeting(meetingNumber, meetingPassword, displayName, options)
  .then((success: any) => console.log(success))
  .catch((error: any) => console.log(error));
```

7. Start an existing meeting for non-login user
Start an existing meeting for non-login user.
```
this.zoomService.startMeetingWithZAK(meetingNumber, vanityId, displayName, zoomToken, zoomAccessToken, userId, options)
  .then((success: any) => console.log(success))
  .catch((error: any) => console.log(error));
```

8. Start an existing meeting for logged in user
Start an existing meeting for logged in user.
```
this.zoomService.startMeeting(meetingNumber, vanityId, options)
  .then((success: any) => console.log(success))
  .catch((error: any) => console.log(error));
```

9. Start an instant meeting for logged in user
Start an instant meeting for logged in user.
```
this.zoomService.startInstantMeeting()
  .then((success: any) => console.log(success))
  .catch((error: any) => console.log(error));
```
10. Set language
Change in-meeting language.
```
this.zoomService.setLanguage("en-US")
  .then((success: any) => console.log(success))
  .catch((error: any) => console.log(error));
```
Supported Languages and Parameters
The parameters are unified using IETF BCP 47 (https://tools.ietf.org/html/bcp47).

| Language            | Language Tag / Parameter |
|---------------------|--------------------------|
| English             | en-US                    |
| Simplified Chinese  | zh-CN                    |
| Japanese            | ja-JP                    |
| German              | de-DE                    |
| French              | fr-FR                    |
| Traditional Chinese | zh-TW                    |
| Spanish             | es-419                   |
| Russian             | ru-RU                    |
| Portuguese          | pt-PT                    |


### Build Note
In order to use the camera and microphone on iOS, please add the following in your config.xml file. Otherwise, your app will not be able to initialize a meeting.
```
<edit-config file="*-Info.plist" mode="merge" target="NSCameraUsageDescription">
    <string>Need camera for video conferencing</string>
</edit-config>
<edit-config file="*-Info.plist" mode="merge" target="NSMicrophoneUsageDescription">
    <string>Need microphone for video conferencing</string>
</edit-config>
```

## Simulator Support
By default, this plugin only support running on real devices. Since [4.4.55130.0712](), we have introduced the simulator support. Here are the steps to add simulator support:

1. Install the Zoom Ionic SDK plugin
```
ionic cordova plugin add cordova.plugin.zoom
```
2. Navigate to `appRoot/plugins/cordova.plugin.zoom`, and back up the whole `libs` folder.
3. Go to [https://github.com/zoom/zoom-sdk-ionic/tags](https://github.com/zoom/zoom-sdk-ionic/tags) and download the `ionic-zoomsdk-***.zip` file; Unzipp it.
4. Replace the `libs` folder in `appRoot/plugins/cordova.plugin.zoom` with the one in the `ionic-zoomsdk-***` folder.
5. Run `ionic cordova platfrom rm android && ionic cordova platform add android` or `ionic cordova platform rm ios && ionic cordova platform add ios` to make sure the new libraries are added to the project.
6. If you are going to publish your app to the Google Play Store or Apple App Store, please use the original `libs` since a library that contians simulator architecture will not pass the app upload check.

## Documentation

Please visit [[https://marketplace.zoom.us/docs/sdk/hybrid-frameworks/ionic](https://marketplace.zoom.us/docs/sdk/hybrid-frameworks/ionic)] for details of each features and functions.

## Versioning

For the versions available, see the [tags on this repository](https://github.com/zoom/zoom-sdk-ionic/tags).

## Change log

Please refer to our [CHANGELOG](CHANGELOG.md) for all changes.

## Frequently Asked Questions (FAQ)
* :one: I am using Ionic SDK on Android and getting `AAPT: error: attribute android:foregroundServiceType not found`:

    * Please have a try with the following:
      1. Make sure you have installed the `cordova-plugin-androidx` and `cordova-plugin-androidx-adapter`
      2. Add the following in your config.xml
```
<preference name="android-minSdkVersion" value="22" />
        <preference name="android-compileSdkVersion" value="29" />
        <preference name="android-build-tool" value="29.0.2" />
        <preference name="android-targetSdkVersion" value="29" />
```
      3. [Important] When adding the Android platform support, please add using the following:
```
ionic cordova platform add https://github.com/apache/cordova-android.git
```
Since the current released version of cordova-android does not support Android SDK API Level 29, please install the latest version from their Github repo, which supports Android SDK API Level 29. Please see the following links:
        * https://github.com/apache/cordova-android/issues/846
        * https://github.com/apache/cordova-android/issues/830

## Support

For any issues regarding our SDK, please visit our new Community Support Forum at https://devforum.zoom.us/.

## License

Please refer to [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* :star: If you like our SDK, please give us a "Star". Your support is what keeps us moving forward and delivering happiness to you! Thanks a million! :smiley:
* If you need any support or assistance, we are here to help you: [Zoom Developer Community Forum](https://devforum.zoom.us/);

---
Copyright ©2020 Zoom Video Communications, Inc. All rights reserved.
