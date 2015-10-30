package midsummer.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * 项目名称：贝塞尔曲线
 * 类描述：
 * 创建人：77.
 * 创建时间：2015/10/12 0012 22:00
 * 修改人：77.
 * 修改时间：2015/10/15 0016 22:30
 * 修改备注：
 */
public class Android extends RelativeLayout
{
	Drawable red, yellow, blue;
	Drawable[] drawables;
	// 线性
	private Interpolator line = new LinearInterpolator();
	// 弹射
	private Interpolator ove = new OvershootInterpolator();
	// 加速
	private Interpolator acc = new AccelerateInterpolator();
	// 减速
	private Interpolator dce = new DecelerateInterpolator();
	// 先加速后减速
	private Interpolator accdec = new AccelerateDecelerateInterpolator();
	private Interpolator[] interpolators;
	private int dHeight, dWidth, mWidth, mHeight;
	private LayoutParams params;
	private Random random = new Random();
	
	public Android(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context)
	{
		interpolators = new Interpolator[5];
		interpolators[0] = line;
		interpolators[1] = ove;
		interpolators[2] = acc;
		interpolators[3] = dce;
		interpolators[4] = accdec;
		
		drawables = new Drawable[3];
		red = ContextCompat.getDrawable(context, R.drawable.ic_action_android_red);
		yellow = ContextCompat.getDrawable(context, R.drawable.ic_action_android_yellow);
		blue = ContextCompat.getDrawable(context, R.drawable.ic_action_android_blue);
		drawables[0] = red;
		drawables[1] = yellow;
		drawables[2] = blue;
		
		// 得到图片的实际宽高
		dHeight = red.getIntrinsicHeight();
		dWidth = red.getIntrinsicWidth();
		
		// 初始化params
		params = new RelativeLayout.LayoutParams(dWidth, dHeight);
		params.addRule(CENTER_HORIZONTAL, TRUE);
		params.addRule(ALIGN_PARENT_BOTTOM, TRUE);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 测绘——得到本Layout的宽高
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}
	
	public void addAndroid()
	{
		final ImageView iv = new ImageView(getContext());
		iv.setImageDrawable(drawables[random.nextInt(3)]);
		iv.setLayoutParams(params);
		addView(iv);
		
		// 属性动画控制目标
		AnimatorSet set = getAnimation(iv);
		// 设置一个监听
		set.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				super.onAnimationEnd(animation);
				// 结束后，将imageView移除
				removeView(iv);
			}
		});
		// 开启动画集合
		set.start();
	}
	
	// 构造3个属性动画
	private AnimatorSet getAnimation(ImageView iv)
	{
		// Alpha动画
		ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.2f, 1f);
		// 缩放动画
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 0.2f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 0.2f, 1f);
		AnimatorSet enter = new AnimatorSet();
		// 缩放时间
		enter.setDuration(1000);
		// 三个动画同时执行
		enter.playTogether(alpha, scaleX, scaleY);
		enter.setTarget(iv);
		
		// 贝塞尔曲线动画（不断修改当前ImageView的坐标——PointF（x,y））
		ValueAnimator bezierValueAnimator = getBezierValueAnimator(iv);
		AnimatorSet bezierSet = new AnimatorSet();
		// 序列执行
		bezierSet.playSequentially(enter, bezierValueAnimator);
		// 加速因子，使用插值器
		bezierSet.setInterpolator(interpolators[random.nextInt(5)]);
		bezierSet.setTarget(iv);
		// bezierSet.setDuration(3000);
		return bezierSet;
	}
	
	private ValueAnimator getBezierValueAnimator(final ImageView iv)
	{
		// 构造一个贝塞尔曲线（不断修改当前ImageView的坐标——PointF（x,y））
		PointF pointF2 = getPointF(2);
		PointF pointF1 = getPointF(1);
		PointF pointF0 = new PointF((mWidth - dWidth) / 2, mHeight - dHeight);
		PointF pointF3 = new PointF(random.nextInt(mWidth), 0);
		
		// 估值器Evaluator，来控制view的行驶路径（不断的修改point.x，point.y）
		BezierEvaluator evaluator = new BezierEvaluator(pointF1, pointF2);
		// 属性动画不仅仅可以改变view的属性，还可以改变自定义的属性
		ValueAnimator animator = ValueAnimator.ofObject(evaluator, pointF0, pointF3);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				PointF pointF = (PointF) animation.getAnimatedValue();
				iv.setX(pointF.x);
				iv.setY(pointF.y);
				// getAnimatedFraction：得到百分比
				iv.setAlpha(1 - animation.getAnimatedFraction());
			}
		});
		animator.setTarget(iv);
		animator.setDuration(8000);
		return animator;
	}
	
	private PointF getPointF(int i)
	{
		PointF pointF = new PointF();
		// 0~当前layout的宽度
		pointF.x = random.nextInt(mWidth);
		// 为了好看，尽量保证pointF2.y>pointF1.y.
		if (i == 2)
		{
			pointF.y = random.nextInt(mHeight / 2);
		} else
		{
			pointF.y = random.nextInt(mHeight / 2) + mHeight / 2;
		}
		return pointF;
	}
}
