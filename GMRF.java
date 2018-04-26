package bayesianOptimization;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.distribution.*;
import java.util.*;

public class GMRF {
	
	class Feedback {
		double count;
		double sum;

		public Feedback() {
			count = 0;
			sum = 0;
		}
		
		public Feedback(double count, double sum) {
			this.count = count;
			this.sum = sum;
		}
		
		public void addfeedback(double fb) {
			count++;
			sum += fb;
		}
	}
	
	int SIZE = 11;
	int DIM = 3;
	double[][] Laplacian;
	double ALPHA = 1.0;
	
	HashSet<Integer> ValidIndex;
	HashSet<Integer> InvalidIndex;
	
	Feedback[] fb_list;
	double[] performance_list;

	public void initialperformance() {
		int len = (int)Math.pow(SIZE,DIM);
		performance_list = new double[len];
		int index = 0;
		for (int s1=0; s1<SIZE; s1++) {
			for (int s2=0; s2<SIZE; s2++) {
				for (int s3=0; s3<SIZE; s3++) {
					double e = (-(s1-3.0)*(s1-3.0)-(s2-5.0)*(s2-5.0)-(s3-6.0)*(s3-6.0))/20;
					double value = Math.exp(e);
					performance_list[index] = value;
					index++;
				}
			}
		}
	}
	
	public double[][] construct(double[][] base) {
		int len = base.length;
		int len_new = len*SIZE;
		double[][] base_new = new double[len_new][len_new];
		
		for (int s1=0; s1<SIZE; s1++) {
			int x1 = s1*len;
			for (int s2=0; s2<SIZE; s2++) {
				int x2 = s2*len;
				if (s1==s2) {
					// copy base to sub matrix from (x1,x2)
					for (int l1=0; l1<len; l1++) {
						for (int l2=0; l2<len; l2++) {
							base_new[x1+l1][x2+l2] = base[l1][l2];
						}
					}
				} else if (Math.abs(s1-s2)==1) {
					// set negative identity matrix
					for (int l=0; l<len; l++) {
						base_new[x1+l][x2+l] = -1;
					}
				}
			}
		}
		
		return base_new;
	}
	
	public void construct() {
		//int len = SIZE^DIM;
		//Laplacian = new double[len][len];
		
		Laplacian = new double[1][1];
		//printmatrix(Laplacian);
		for (int d=0; d<DIM; d++) {
			Laplacian = construct(Laplacian);
			//printmatrix(Laplacian);
		}
		
		int len = Laplacian.length;
		for (int l1=0; l1<len; l1++) {
			double sum = 0;
			for (int l2=0; l2<len; l2++) {
				sum = sum - Laplacian[l1][l2];
			}
			Laplacian[l1][l1] = sum;
			//System.out.print(" " + (int)Math.round(sum));
		}
	}
	
	public void printmatrix(double[][] m) {
		int len = m.length;
		
		for (int x1=0;x1<len; x1++) {
			for (int x2=0;x2<len; x2++) {
				double value_d = m[x1][x2];
				int value_i = (int)Math.round(value_d);
				System.out.print(" " + value_i);
			}
			System.out.println();
		}
	}
	
	public void printmatrix(RealMatrix m) {
		for (int r = 0; r < m.getRowDimension(); r++) {
	        for (int c = 0; c < m.getColumnDimension(); c++) {
	        	double value = Math.round(m.getEntry(r, c));
	        	int value_i = (int)value;
	        	System.out.print(" " + value_i);
	        }
	        System.out.println();
	    }
	}
	
	public void printdiagonal(RealMatrix m) {
		for (int r = 0; r < m.getRowDimension(); r++) {
        	System.out.print(" " + m.getEntry(r, r));
	    }
        System.out.println();
	}
	
	public void eigendecomposition(double[][] vecMatrix) {
		RealMatrix m = new Array2DRowRealMatrix(vecMatrix);
		EigenDecomposition dec = new EigenDecomposition(m);
		RealMatrix D = dec.getD();
		printdiagonal(D);
		RealMatrix V = dec.getV();
		//printmatrix(V);
		
		RealMatrix VT = V.transpose();
		RealMatrix VDVT = V.multiply(D).multiply(VT);
		//printmatrix(VDVT);
	}
	
	public RealMatrix matrixinverse(double[][] vecMatrix) {
		RealMatrix m = new Array2DRowRealMatrix(vecMatrix);
		EigenDecomposition dec = new EigenDecomposition(m);
		RealMatrix D = dec.getD();
		RealMatrix V = dec.getV();
		
		int len = vecMatrix.length;
		for (int l=0; l<len; l++) {
			double value = D.getEntry(l, l);
			D.setEntry(l, l, 1.0/value);
		}
		RealMatrix VT = V.transpose();
		RealMatrix INV = V.multiply(D).multiply(VT);
		
		return INV;
	}
	
	public void getvalidindex() {
		ValidIndex = new HashSet<Integer>();
		InvalidIndex = new HashSet<Integer>();
		
		int len = Laplacian.length;
		fb_list = new Feedback[len];
		for (int l=0; l<len; l++) {
			double value = Laplacian[l][l];
			
			if (value > DIM*2-0.01) {
				ValidIndex.add(l);
				fb_list[l] = new Feedback();
			} else {
				InvalidIndex.add(l);
				fb_list[l] = new Feedback(1, 0.5);
			}
		}
	}
	
	public int UCB() {
		int len = Laplacian.length;
		double[][] matrixA = new double[len][len];
		for (int l1=0; l1<len; l1++) {
			for (int l2=0; l2<len; l2++) {
				matrixA[l1][l2] = Laplacian[l1][l2];
			}
		}
		double[] vectorb = new double[len];
		
		for (int l=0; l<len; l++) {
			double count = fb_list[l].count;
			double sum = fb_list[l].sum;
			matrixA[l][l] += count;
			vectorb[l] = sum;
		}
		
		RealMatrix INV = matrixinverse(matrixA);
		double[] vectorb_new = INV.operate(vectorb);
		
		int trial = -1;
		double ucb_largest = -10000;
		boolean b = false;
		for (Iterator<Integer> it=ValidIndex.iterator(); it.hasNext(); ) {
			int index = it.next();
			double ave = vectorb_new[index];
			double std = Math.sqrt(INV.getEntry(index, index));
			double ucb = ave + std*ALPHA;
			if (b)
				System.out.println("" + index + ": " + ave + ", " + std);
			if (ucb>ucb_largest) {
				trial = index;
				ucb_largest = ucb;
			}
		}
		
		return trial;
	}
	
	public double getfeedback(int trial) {
		double performance = performance_list[trial];
		NormalDistribution nd = new NormalDistribution();
		double s = nd.sample()*0.05;
		double fb = performance + s;
		System.out.println("" + trial + ": " + performance + ", " + fb);
		return fb;
	}
	
	public void run() {
		System.out.println("Starting...");
		initialperformance();
		System.out.println("Constructing...");
		construct();
		//printmatrix(Laplacian);
		System.out.println("Eigen decomposition...");
		eigendecomposition(Laplacian);
		getvalidindex();
		
		System.out.println(ValidIndex);
		System.out.println(ValidIndex.size());
		System.out.println(InvalidIndex);
		System.out.println(InvalidIndex.size());
		
		for (int n=0; n<1000; n++) {
			if (n==600) {
				System.out.println(n);
			}
			int trial = UCB();
			double fb = getfeedback(trial);
			fb_list[trial].addfeedback(fb);
		}
	}
	
	public static void main(String[] args) {
		GMRF g = new GMRF();
		g.run();
	}

}
