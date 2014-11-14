package mayday.statistics.rankproduct;

import java.util.Random;

import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.matrix.PermutableSubMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.ConstantIndexVector;
import mayday.core.structures.linalg.vector.DoubleVector;

/**
 * A class that provides all methods required to compute the rank product
 * (see Breitling et al: Rank products: a simple, yet powerful, new method 
 * to detect differentially regulated genes in replicated microarray experiments.
 * FEBS Lett. 2004 Aug 27;573(1-3):83-92.)
 * @author owen
 *
 */
public class RPTools {

	private Random random = new Random();

	/**
	 * based on Bioconductor source code: http://search.bioconductor.jp/codes/1258
	 * RankProd1.R
	 * @param data1	expression data 
	 * @param data2 expression data (in case of one-sample: null)
	 * @param num_classes number of classes
	 * @param logged TRUE if values are logarithmized, FALSE if they aren't
	 * @param reverse TRUE if roles are reversed. If FALSE, function will always try to find up-/downregulated genes in class 2 in comparison to class 1
	 * @return
	 */
	public DoubleVector computeRankProduct(PermutableMatrix data1, PermutableMatrix data2 , int num_classes, boolean logged, boolean reverse, boolean paired){

		int num_genes 	= data1.getColumn(0).size();
		boolean largeData;
		int num_col;

		DoubleVector rankProduct = DoubleVector.rep( 1.0, num_genes );//the vector which will hold the RP for each gene

		if(num_classes==2){
			
			PermutableMatrix data1_wk = reverse? data2 : data1;
			PermutableMatrix data2_wk = reverse? data1 : data2;
			
			int num_gene = data1.nrow();

			int k1, k2;

			if(!paired){
			k1 = data1_wk.ncol();
			k2 = data2_wk.ncol();
			//compute all pairwise comparisons between members of each class
			num_col 	= k1*k2;
			largeData 	= (num_col > 50 && num_gene > 2000 | num_col > 100);
			
			}
			else{
				
				k1 = data1_wk.ncol();
				num_col = k1;
				k2 = 1;
				largeData 	= (num_col > 50 && num_gene > 2000 | num_col > 100);
			}
			
			//compute fold changes
			for (int i1=0; i1!=k1; ++i1) {
				for (int i2=0; i2!=k2; ++i2) {
					
					DoubleVector rank_prod_temp = data1_wk.getColumn(i1).clone();

					//compute fold changes by subtraction (if values are log) or division(if values aren't log)
					if(!paired)
					computeFoldChange(rank_prod_temp, data2_wk.getColumn(i2).clone(), logged);
					else
					computeFoldChange(rank_prod_temp, data2_wk.getColumn(i1).clone(), logged);

					rank_prod_temp = rank_prod_temp.rank(); //rank fold changes

					if (largeData) { // apply root right now 
						rank_prod_temp.raise(1.0/num_col);
					} 
					rankProduct.multiply(rank_prod_temp);  //iteratively compute the rank product whenever the ranks become available
				}
			}	

			if (!largeData) { // do the rooting at the end
				rankProduct.raise(1.0/num_col);

			}

		}else{

			num_col 	= data1.getRow(0).size();
			largeData 	= (num_col > 50 && num_genes> 2000) | num_col > 100;	//if there is a lot of data, this may lead to problems with overflow

			PermutableMatrix rankedMatrix = new PermutableSubMatrix(data1, new int[num_genes], new int[num_col]);

			// rank each column in descending order, NAs are assigned the lowest rank. 
			for(int i = 0; i < num_col; i++){

				DoubleVector dataColumn = data1.getColumn(i).clone();
				DoubleVector rankedCol = dataColumn.rank();	

				// reverse = FALSE
				// desired ranking: NAs are ranked lowest, all non-NA values are ranked increasing from small values to large values
				// e.g. (5,3,NA,1,2) -> (4,3,5,1,2)
				// rank function will do just this
				if(!reverse){

					rankedMatrix.setColumn(i, rankedCol);

					// reverse = TRUE
					// desired ranking: NAs are ranked lowest, all non-NA values are ranked increasing from large to small values
					// e.g. (5,3,NA,1,2) -> (1,2,5,4,3)
					// rank function will rank NAs lowest, this should remain unchanged but the ranks of the non-NA values need to be inverted
					// -> for each non-NA value compute reverse rank as (num_genes+1)-rank
				}else{

					for(int j = 0; j < rankedCol.size(); j++){

						double reversedRank = num_genes+1 - rankedCol.get(j);
						rankedCol.set(j, reversedRank);

					}
				}

				rankedMatrix.setColumn(i, rankedCol);

				if (largeData) { // apply root right now 
					rankProduct.raise(1.0/num_col);
				}
				rankProduct.multiply(rankedCol);  //iteratively compute the rank product whenever the ranks become available
			}

			if (!largeData) { // do the rooting at the end
				rankProduct.raise(1.0/num_col);
			}
		}
		return rankProduct;
	}

	/**
	 * 
	 * @param oldRank_vector vector of ranks of RP values
	 * @param RP_vector the original vector of rank products
	 * @param RP_permutations vector with rank products of randomly permuted values
	 */
	public void updateRank( DoubleVector oldRank_vector, DoubleVector RP_vector, DoubleVector RP_permutations ) {

	
		int[] ord = RP_vector.order(); 		// compute an array reflecting the order of the RP values of the original data 
		RP_vector.setPermutation(ord);		// sort the vector of RP values in decreasing order
		oldRank_vector.setPermutation(ord);	// apply this permutation to the vector of rankings

		RP_permutations.sort();  			// move NAs to the end, sort RP-values from random permutation in descending order

		// this behaves exactly like the R code only that access indices are decremented by 1 (zero-based)

		int i = RP_vector.size(); 
		int j = RP_permutations.size();

		// skip NAs now
		while (Double.isNaN(RP_permutations.get(j-1)))
			--j; 

		while (Double.isNaN(RP_vector.get(i-1)))
			--i;

		int firstNA = j;		//the first NA in the vector: since NAs were sorted to the bottom, this is the index of the first NA-value as seen from the top

		while(i>0 && j>0) { 
			while (j>0 && RP_permutations.get(j-1) >= RP_vector.get(i-1)) //find the first RP value from nextCol that is < the (j-1)st RP value in col1
				j--;

			int k = i;

			while (i>0 && j>0 && RP_permutations.get(j-1) < RP_vector.get(i-1)) 	//find the first RP-value from col1 that is >= the (j-1)st value in nextCol  
				i--;

			oldRank_vector.add( j, i, (k-1) ); 										//add j to indices i through (k-1)  
		}

		// change the rank of NA items by adding the number of non-NA elements
		for (int pos : RP_vector.whichIsNA())
			oldRank_vector.set(pos, oldRank_vector.get(pos)+firstNA);

		oldRank_vector.unpermute(); 
		RP_permutations.unpermute();
		RP_vector.unpermute(); 
	}

	/**
	 * From two input matrices, computes a new matrix by permuting each column randomly.
	 * @param data1 data from condition/class 1
	 * @param data2 data from condition/class 2
	 * @return an expression matrix in which each column represents a column of the original matrix which was randomly permuted
	 */
	public DoubleMatrix[] generateNewData(PermutableMatrix data1, PermutableMatrix data2, int numClasses) {

		int k1 			= data1.ncol();
		int num_gene	= data1.nrow();
		DoubleMatrix new_data1 = new DoubleMatrix( num_gene, k1 ); 

		//permute values of data1, column by column
		for (int k=0; k!=k1; ++k) {
			AbstractVector v = data1.getColumn(k);
			v.permute(random);
			new_data1.setColumn(k, v);
		}

		DoubleMatrix new_data2 = null;

		if(numClasses == 2){
			int k2 = data2.ncol();
			new_data2 = new DoubleMatrix( num_gene, k2 ); 
			//permute values of data1, column by column
			for (int k=0; k!=k2; ++k) {
				AbstractVector v = data2.getColumn(k);
				v.permute(random);
				new_data2.setColumn(k, v);
			}
		}

		DoubleMatrix[] result = new DoubleMatrix[]{new_data1, new_data2};

		return result;
	}

	/**
	 * Computes the percentage of false positives (pfp)
	 * @param count_perm rank vector
	 * @param rank_ori rank vector from RP values of original data
	 * @param num_gene number of genes/rows
	 * @param num_permutations number of permutations 
	 * @return an array of double vectors; position 0 contains vector of p-values, position 1 contains vector of pfp-values
	 */
	// based on http://search.bioconductor.jp/codes/1249, starting at    ##address significance level cat("Computing pfp ..","\n")"
	public DoubleVector[] computePFP( DoubleVector count_perm, DoubleVector rank_ori, int num_gene, int num_permutations ) {

		//		 	wo befindet sich dieser Teil des Codes?
		//			temp1 <- as.vector(cbind(RP.ori.upin2$RP,RP.perm.upin2))
		//		  	temp2 <- rank(temp1)[1:num.gene]  ##the rank of original RP in the increasing order
		//		   	order.temp <- match(temp2,sort(temp2))


		count_perm.sort();
		AbstractVector expected = new ConstantIndexVector(count_perm.size(), 1);
		count_perm.subtract( expected );		//		   count.perm <- (sort(temp2)-c(1:num.gene))[order.temp]
		count_perm.unpermute();



		// avg. expected value for RP
		DoubleVector exp_count = count_perm.clone();
		exp_count.divide( num_permutations );		//		  	exp.count <- count.perm/num.perm

		// pfp-value
		DoubleVector pfp = exp_count;				//			q(g)=E(RPg)/rank(g)
		pfp.divide( rank_ori );						//		   	pfp.upin2 <- exp.count/rank.ori.upin2 

		// p-value
		DoubleVector pval = count_perm.clone();
		pval.divide( num_permutations * num_gene );	//		   	pval.upin2 <- count.perm/(num.perm*num.gene)


		return new DoubleVector[]{pval, pfp};
	}

	/**
	 * Computes the fold change of two vectors.
	 * @param vector1 vector 1
	 * @param vector2 vector 2
	 * @param logged TRUE if values are logarithmized, FALSE if not.
	 */
	private void computeFoldChange(DoubleVector vector1, DoubleVector vector2, boolean logged){

		if (logged){
			vector1.subtract(vector2);
		}else{
			vector1.divide(vector2);
		}
	}
}
