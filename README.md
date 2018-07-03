# LogToMail Features
 
  - Create a ```.txt``` file that contains all ```Android``` app log data and send that file via email
  - Supports required permissions state check for 
  ```java 
  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
  ```


## Required Manifest Permissions
 
  - ```<uses-permission android:name="android.permission.READ_LOGS" />```
  - ```<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />```
  - ```<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />```
 
 
## Required Manifes Provider
  ```java
  <provider
      android:name="android.support.v4.content.FileProvider"
      android:authorities="${applicationId}.provider"
      android:exported="false"
      android:grantUriPermissions="true">
      <meta-data
          android:name="android.support.FILE_PROVIDER_PATHS"
          android:resource="@xml/provider_paths" />
  </provider>
  ``` 
 
 
## Required xml file (res > xml > provider_paths.xml)
  ```java
  <?xml version="1.0" encoding="utf-8"?>
  <paths>
      <external-path
      name="external_files"
      path="." />
  </paths>^
``` 
 
 
## Fixme

  - MainActivity line 135:
  ```java 
  String to[] = {"YOUR_EMAIL_HERE_@gmail.com"};
  ```
  - MainActivity line 142 
  ```java 
  emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Subject Here...");
  ```
