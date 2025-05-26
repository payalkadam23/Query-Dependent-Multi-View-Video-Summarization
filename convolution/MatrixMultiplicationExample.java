/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConvNuralNetwork.Convolution;

/**
 *
 * @author virendra
 */
public class MatrixMultiplicationExample{  
public static void main(String args[]){  
//creating two matrices    
int a[][]={{1,1,1},{2,2,2},{3,3,3}};    
int b[][]={{1,1,1},{2,2,2},{3,3,3}};  
int e[][]={{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};

    int[][] MaxPooling= new int[2][2]; 
//creating another matrix to store the multiplication of two matrices    
int c[][]=new int[3][3];  //3 rows and 3 columns  
    
//multiplying and printing multiplication of 2 matrices    
for(int i=0;i<3;i++){    
for(int j=0;j<3;j++){    
c[i][j]=0;      
for(int k=0;k<3;k++)      
{      
c[i][j]+=a[i][k]*b[k][j]*e[i][k];      
}//end of k loop  
// int x = c[i][j];
//if(x < 14)
//    c[i][j] = 0;
if(i < 2 && j < 2)
{
    MaxPooling[i][j] = c[i][j];
System.out.print(MaxPooling[i][j]+" ");  //printing matrix element  
}
}//end of j loop  
System.out.println();//new line    
}    
}}  