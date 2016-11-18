package particles;

import sim.util.Bag;
import sim.util.Double2D;

public class PearsonCorrelation {
	double sXY = 0;
	double sX = 0;
	double sY =0;
	double sX2 = 0;
	double sY2 = 0;
	double n = 0;

	public void getData(double x, double y){
		sXY += x*y;
		sX += x;
		sY += y;
		sX2 += x*x;
		sY2 += y*y;
		n++;
	}

	public Double2D means(){
		return new Double2D(sX/n, sY/n);
	}

	public double correlation(){
		double r = (sXY - (sX*sY)/n)/Math.sqrt((sX2-(sX*sX)/n)*(sY2-(sY*sY)/n));
		return r;
	}

	public static double mean(Bag array){
		double x =0;

		for(int i=0; i< array.numObjs;i++){
			x += (Double)array.get(i);
		}

		return x/(double)array.numObjs;
	}

	public static double ssq(Bag array, double mean){
		double x=0;
		for(int i=0; i< array.numObjs;i++){
			double y = (Double)array.get(i)-mean;
			x += y*y;
		}

		return x;
	}

	public static double sumXY(Bag arrayX, Bag arrayY, double meanX, double meanY){
		double xy = 0;
		for(int i =0; i< arrayX.numObjs;i++){
			double x = (Double)arrayX.get(i)-meanX;
			double y = (Double)arrayY.get(i)-meanY;
			xy += x*y;
		}
		return xy;
	}

	public static double correlation(Bag arrayX, Bag arrayY){
		if(arrayX.numObjs != arrayY.numObjs){
			System.err.println("Arrays are not equal!");
			return -100;
		}
		double r = 0;
		double meanX = mean(arrayX);
		double meanY = mean(arrayY);
		double ssqX = ssq(arrayX, meanX);
		double ssqY = ssq(arrayY, meanY);
		double sXY = sumXY(arrayX,arrayY,meanX,meanY);

		r = sXY/Math.sqrt(ssqX * ssqY);
		return r;
	}

}
