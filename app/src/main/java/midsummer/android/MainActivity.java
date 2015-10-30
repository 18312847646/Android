package midsummer.android;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
{
	@ViewById
	Toolbar toolbar;
	@ViewById
	Android android;
	
	@AfterViews
	public void mainActivity()
	{
		setSupportActionBar(toolbar);
	}
	
	@Click(R.id.fab)
	public void click(View v)
	{
		// 出现图片并开始动画
		android.addAndroid();
	}
}
