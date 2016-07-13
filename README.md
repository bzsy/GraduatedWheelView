# GraduatedWheelView

### Screenshot
<img src="https://raw.githubusercontent.com/bzsy/GraduatedWheelView/master/screenshot/screenshot.jpeg" width="30%" height="30%">

### How to use

Gradle:
```
dependencies {
    compile 'com.github.bzsy:GraduatedWheelView:1.0'
}
```

### Configure
```java
setMinVal();            //min value
setMaxVal();            //max value
setCurValue();          //current value
setValueType();         //GraduatedWheelView.TYPE_DECIMAL or GraduatedWheelView.TYPE_INTEGER
setDivLineColor();      //divide line color
setStrokeColor();       //stroke line color
setCenterLineColor();   //center line color
setTextColor();         //text color
setOnValueChangedListener(new GraduatedWheelView.OnValueChangedListener() {
    @Override
    public void onChanged(float oldValue, float newValue) {
        //newValue is current selected value
      }
});
```
