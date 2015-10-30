package midsummer.android;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * 项目名称：贝塞尔曲线
 * 类描述：
 * 创建人：77.
 * 创建时间：2015/10/16 0016 21:13
 * 修改人：77.
 * 修改时间：2015/10/16 0016 21:13
 * 修改备注：
 */
public class BezierEvaluator implements TypeEvaluator<PointF>
{
	PointF point1;
	PointF point2;
	
	public BezierEvaluator(PointF pPoint1, PointF pPoint2)
	{
		point1 = pPoint1;
		point2 = pPoint2;
	}
	
	@Override
	public PointF evaluate(float t, PointF point0, PointF point3)
	{
		// t：百分比 0~1
		PointF point = new PointF();
		point.x = point0.x * (1 - t) * (1 - t) * (1 - t)
				+ 3 * point1.x * t * (1 - t) * (1 - t)
				+ 3 * point2.x * t * t * (1 - t)
				+ point3.x * t * t * t;
		point.y = point0.y * (1 - t) * (1 - t) * (1 - t)
				+ 3 * point1.y * t * (1 - t) * (1 - t)
				+ 3 * point2.y * t * t * (1 - t)
				+ point3.y * t * t * t;
		
		return point;
	}
}
