# BadgeView
Badge view with animated effect which shows a bitmap or a text.
### Usage 
```XML
<su.levenetc.android.badgeview.BadgeView
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  app:badgeText="Hello!" />
```
```Java
BadgeView badgeView = new BadgeView(this);
badgeView.setValue(R.string.hello);
```
### XML attributes
```XML
<attr name="badgeText" format="string"/>
<attr name="badgeBitmap" format="reference"/>
<attr name="badgeBackgroundColor" format="color"/>
<attr name="badgeTextColor" format="color"/>
<attr name="badgeTextSize" format="dimension"/>
<attr name="badgePadding" format="dimension"/>
<attr name="badgeAnimationDuration" format="integer"/>
```
### Animations
To show values sequentially use `setValues` method:
```Java
Bitmap bitmapX;
badgeView.setValues(0, 1, bitmapX, 3, "How are you?");
```
To defaine partucular delay for each value use helper class `BadgeView.AnimationSet`:
```Java
new BadgeView.AnimationSet(badgeView)
  .add("Hi!", 1000)
  .add("How are you?", 1200)
  .add("Im fine!", 1500)
  .play();
```
