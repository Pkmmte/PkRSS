PkRSS
=====

A fluent and flexible RSS feed manager for Android

For more information, please see [the website][1]

Download
--------

Download [the latest JAR][2] or grab via Gradle:
```groovy
compile 'com.pkmmte.pkrss:pkrss:1.1.2'
```
or Maven:
```xml
<dependency>
    <groupId>com.pkmmte.pkrss</groupId>
    <artifactId>pkrss</artifactId>
    <version>1.1.2</version>
</dependency>
```

Basic Usage
--------

PkRSS features a fluent API which allows you to create flexible requests - often using only one line of code. For a full application sample, see the [TechDissected Source Code][3]

#####Basic Loading
This code loads the specified URL asynchronously. 
```java
PkRSS.with(this).load(url).async();
```

#####Pagination & Callbacks
The following loads the next page belonging to the specified url and assigns a callback. PkRSS keeps track of which page was last loaded using a PageTracker. You may also manually specify a page number using `page(int)`.
```java
PkRSS.with(this).load(url).nextPage().callback(this).async();
```

#####Search & Synchronous Requests
You are able to query for a specific search term on a specified feed and get the result synchronously. Pagination is also supported for search queries.
```java
PkRSS.with(this).load(url).search(query).page(int).get();
```

There are a lot more APIs available such as custom parsers, mark articles as read/favorited, instance builder, custom article objects, request cancelling, and more! See [the website][1] for more info or [read the Javadoc][4].

ProGuard
--------

If you are using ProGuard make sure you add the following option:

```
-keep class com.pkmmte.pkrss.Callback{ *; }
-dontwarn com.squareup.okhttp.**
```

Developed By
--------

Pkmmte Xeleon - www.pkmmte.com

<a href="https://plus.google.com/102226057091361048952">
  <img alt="Follow me on Google+"
       src="http://data.pkmmte.com/temp/social_google_plus_logo.png" />
</a>
<a href="https://www.linkedin.com/pub/pkmmte-xeleon/7a/409/b4b/">
  <img alt="Follow me on LinkedIn"
       src="http://data.pkmmte.com/temp/social_linkedin_logo.png" />
</a>

License
--------

    Copyright 2014 Pkmmte Xeleon
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [1]: http://pkmmte.github.io/PkRSS/
 [2]: https://github.com/Pkmmte/PkRSS/releases/download/v1.1.2/pkrss-1.1.2.jar
 [3]: https://github.com/Pkmmte/TechDissected
 [4]: http://pkmmte.github.io/PkRSS/javadoc/
