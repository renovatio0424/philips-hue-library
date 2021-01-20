# PhillipsHue Android Library
======

## How to use it

### 1. get bridge list in your network
```kotlin
private fun initBridgeList() {
    CoroutineScope(Dispatchers.Main).launch {
        // you can get bridge list in your network
        val bridgeList = BridgeManager().getBridgeList()
    }
}
```

### 2. get token for controlling philips hue bulbs
```kotlin
private fun fetchLightList() {
    val bridgeIp = Bridge.internalIpAddress
    val lightController = BridgeController(bridgeIp)
    
    CoroutineScope(Dispatchers.Main).launch {
        try {
            // if you click the link button, you can get a token!
            token: String = lightController.getToken()
            ...
        } catch (exception: Exception) {
            // it will throw UnClickBridgeLinkButtonException
            // if you have not clicked the bridge link button
            // so if the exception is thrown, you should deliver the message like "click the bridge button"
        }
    }
}

```
### 3. get light list 
```kotlin
CoroutineScope(Dispatchers.Main).launch {
    val lightList:List<Light> = lightController.getLights(token)
}
```
### 4. turn on the light
```kotlin
CoroutineScope(Dispatchers.Main).launch {
    //you can get light id from Light.kt
    lightController.turnOn(token, lightId, turnOn)
}
```
### 5. change hue & color of lights
```kotlin
// use ColorInt
CoroutineScope(Dispatchers.Main).launch {
    lightController.changeColor(
        token,
        lightId,
        //ColorInt
        selectColor
    )
}
// use RGB
CoroutineScope(Dispatchers.Main).launch {
    lightController.changeRGBColor(
        token,
        lightId,
        red = Color.red(colorInt)
        green = Color.green(colorInt)
        blue = Color.blue(colorInt)
    )
}
// use HSV
CoroutineScope(Dispatchers.Main).launch {
    val hsv = FloatArray(3)
    
    Color.colorToHSV(Color.rgb(red, green, blue), hsv)
    val brightness = (hsv[2] * 255).toInt()
    val saturation = (hsv[1] * 255).toInt()
    val hue = ((hsv[0] * 65535) / 360).toInt()
    
    lightController.changeHSVColor(
        token,
        lightId,
        hue = hue,
        saturation = saturation,
        brightness = brightness
    )
}

```

# License
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
