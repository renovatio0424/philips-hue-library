# PhillipsHue Android Library

Feature
======
1. connect bridge
2. get list of lights 
3. turn on or off the lights
4. change hue & color of lights

Requirement
======
1. you should add this properties at application manifests

```xml
//manifests
<application
    android:usesCleartextTraffic="true"/>
```

2. if you want to use remote hue api, you should set this property in Android WebView

```kotlin
// HueLoginActivity.kt
hue_login_web_view.settings.userAgentString = System.getProperty("http.agent")
```
License
=======

    Copyright 2019 Reno.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
