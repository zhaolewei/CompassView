# CompassView  
  
  指南针自定义控件
  
## Demo
![](https://raw.githubusercontent.com/zhaolewei/CompassView/master/compassView-gif.gif)


## Gradle

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
}

    dependencies {
        compile 'com.github.zhaolewei:CompassView:v1.0'
     }

```

## Attributes

|name|format|description|
|:---:|:---:|:---:|
| cv_isDebug | boolean |是否开启测试模式（可查看文字中点位置）
| cv_lineColor | color |小线条颜色
| cv_keyLineColor | color |关键线条颜色（每30度绘制一个）
| cv_mainLineColor | color |指针颜色
| cv_edgeTextColor | color |边缘上标尺度数的颜色
| cv_orientationTextColor | color |方向标尺的颜色
| cv_angleTextColor | color |正中间当前角度信息的颜色
| cv_divideCount | integer |小线条的数量


## Use
````java
    compassView.setRotate(rotate); //设置顺时针旋转的角度  

````
