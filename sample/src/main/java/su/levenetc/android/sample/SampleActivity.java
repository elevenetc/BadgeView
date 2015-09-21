package su.levenetc.android.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import su.levenetc.android.badgeview.BadgeView;
import su.levenetc.android.badgeview.interfaces.IAnimationListener;

public class SampleActivity extends AppCompatActivity implements IAnimationListener {

	private View btnsContainer;
	private BadgeView badgeView;
	private int value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		badgeView = (BadgeView) findViewById(R.id.badge_view);
		btnsContainer = findViewById(R.id.btns_container);

		findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				badgeView.setValue(++value);
			}
		});

		findViewById(R.id.btn_sub).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				badgeView.setValue(--value);
			}
		});

		final int duration = 750;

		badgeView.postDelayed(new Runnable() {
			@Override public void run() {

				new BadgeView.AnimationSet(badgeView)
						.add("2", duration)
						.add("1", duration)
						.add("0", duration)
						.add("Text sample!", duration)
						.play(SampleActivity.this);
			}
		}, 1000);
	}

	@Override public void onAnimationEnd() {
		btnsContainer.setVisibility(View.VISIBLE);
	}
}
