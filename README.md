# Compass
This is just a compass. Nothing more, nothing less. No adds.

## Developer Info
This is my first Android app and I learned a few things.

### Junit4
JUnit5 did not work in the end.

### Angles in radians vs. 360°
All angles on the sensor side are in radians, while rotating an image with RotateAnimation takes degrees as parameters.

### Low-pass filter
A simple low-pass-filter reduces noise in the sensor values.
```
azimut = 0.97f * azimut + (1 - 0.97f) * newAzimut;
```

### Public constructor not being public
android.hardware.SensorEvent shows a public constructor in Android Studio. But using results in a compiler error:

'SensorEvent()' is not public in 'android.hardware.SensorEvent'. Cannot be accessed from outside package

Maybe it has something to do with SensorEvent being written in Kotlin and Android Studio showing decompiled Java code.

## Links
Fix Markdown plugin in Android studio:

https://joachimschuster.de/posts/android-studio-fix-markdown-plugin/

Low-pass filter:

https://developer.android.com/guide/topics/sensors/sensors_motion#sensors-motion-accel
https://github.com/iutinvg/compass/commit/1bd90d7cd0c292b5911cb775400670aef1a86c51#diff-4c11faf4ab931e7176732c7e08fe8a15cc8dc09b85ad5d5fce8f1ff52bba1dfa


