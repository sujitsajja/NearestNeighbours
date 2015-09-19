/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nearest.neighbour;
/**
 *
 * @author sujit
 */
import java.io.*;
import java.util.*;

public class NearestNeighbour
{
    public static void main(String[] args) throws FileNotFoundException
    {
        Scanner sc = new Scanner(new File("file1.txt"));
        int kfold = sc.nextInt();
        int m = sc.nextInt();
        int t = sc.nextInt();
        double finalError = 0;
        int permutations[] = new int[m];
        int permutationMatrix[][] = new int[t][m] ;
        ArrayList<Integer> errorArrays = new ArrayList<Integer>();
        FileOutputStream fout = new FileOutputStream("out.txt");
        PrintStream pw = new PrintStream(fout);
        System.setOut(pw);
        for(int i = 0; i < t; i++)
        {
            for(int j = 0; j < m; j++)
                permutationMatrix[i][j] = sc.nextInt();
        }
        Scanner sc3 = new Scanner(new File("file2.txt"));
        int rows = sc3.nextInt();
        int cols = sc3.nextInt();
        int finalMatrix[][] = new int[rows][cols];
        ArrayList<int[][]> grids = new ArrayList<int[][]>();
        for(int knn = 1; knn < 6 ; knn++)
        {
            for(int i = 0; i < t; i++)
            {
                for(int j = 0; j < m; j++)
                    permutations[j] = permutationMatrix[i][j]+1;
                errorArrays.add(start(m,kfold,permutations,knn,finalMatrix));
                grids.add(finalMatrix);
            }
            for(int k = 0; k < errorArrays.size();k++)
                finalError = finalError + (double)(errorArrays.get(k)/(float)m);
            double e = finalError/(double)t;
            double variance = 0;
            for(int k = 0; k < errorArrays.size();k++)
                variance = variance + ((e - ((double)(errorArrays.get(k)/(float)m))) * (e - ((double)(errorArrays.get(k)/(float)m))));
            pw.println("k = "+knn+" e = "+e+" sigma= "+Math.sqrt((double)variance/(float)(t - 1)));
            for(int k = 0; k<rows; k++)
            {
                for(int j = 0;j<cols;j++)
                {
                    if(finalMatrix[k][j] == 1)
                        pw.print("+" + " ");
                    else
                        pw.print("-" + " ");
                }
                pw.println("");
            }
            pw.println("");
            grids.clear();
            errorArrays.clear();
            finalError = 0;
            variance = 0;
        }
        sc.close();
    }
    public static int start(int m,int kfold,int[] permutations, int knn,int[][] finalMatrix) throws FileNotFoundException
    {
        Scanner sc2 = new Scanner(new File("file2.txt"));
        int rows = sc2.nextInt();
        int cols = sc2.nextInt();
        int x1[] = new int[m];
        int x2[] = new int[m];
        int x1ForDot[] = new int[(rows*cols) - m];
        int x2ForDot[] = new int[(rows*cols) - m];
        int y[] = new int[m];
        int count1 = 0;
        int countForDot = 0;
        String[][] givenData = new String[rows][cols];
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                givenData[i][j] = sc2.next();
                if(Character.valueOf(givenData[i][j].charAt(0)).equals('+'))
                {
                    x1[count1] = j;
                    x2[count1] = i;
                    y[count1] = 1;
                    count1++;
                }
                else if(Character.valueOf(givenData[i][j].charAt(0)).equals('-'))
                {
                    x1[count1] = j;
                    x2[count1] = i;
                    y[count1] = 0;
                    count1++;
                }
                else if(Character.valueOf(givenData[i][j].charAt(0)).equals('.'))
                {
                    x1ForDot[countForDot] = j;
                    x2ForDot[countForDot] = i;
                    countForDot++;
                }
            }
        }
        int minNumberInFold = m/kfold;
        int extraNumberInFold = m % kfold;
        int count2 = m-1;
        int count3 = 0;
        boolean flag = true;
        if(extraNumberInFold != 0)
            count3 = minNumberInFold;
        else
        {
            count3 = minNumberInFold-1;
            flag  = false;
        }
        int[][] partitions= new int[kfold][minNumberInFold+1];
        for(int i = kfold-1 ; i >= 0 ; i--)
        {
            for(int j = count3 ; j >= 0 ; j--)
            {
                partitions[i][j] = permutations[count2];
                count2--;
            }
            if(extraNumberInFold != 0)
                extraNumberInFold--;
            if(extraNumberInFold == 0 && flag == true)
            {
                count3--;
                flag = false;
            }
        }
        ArrayList<HashMap<Integer, Float>> distancesOfIndices = new ArrayList<HashMap<Integer, Float>>();
        ArrayList<Integer> sortedKeys2 = new ArrayList<Integer>();
        for(int i = 0 ; i < kfold; i++)
        {
            for(int l = 0 ; l < minNumberInFold + 1 ; l++)
            {
                if(partitions[i][l] != 0)
                {
                    Map<Integer, Float> distancesMap = new HashMap<Integer, Float>();
                    for(int j = 0; j < kfold; j++)
                    {
                        for(int k = 0; k < minNumberInFold + 1 ; k++)
                        {
                            if(i != j && partitions[j][k] != 0)
                                distancesMap.put(partitions[j][k],(float) distanceBtwTwoPoints(x1[partitions[i][l]-1],x2[partitions[i][l]-1],x1[partitions[j][k]-1],x2[partitions[j][k]-1]));
                        }
                    }
                    distancesOfIndices.add((HashMap<Integer, Float>) distancesMap);
                }
            }
        }
        ArrayList<HashMap<Integer, Float>> distancesOfIndicesForDots = new ArrayList<HashMap<Integer, Float>>();
        for(int i = 0; i < x1ForDot.length ; i++)
        {
            Map<Integer, Float> distancesMap2 = new HashMap<Integer, Float>();
            for(int j = 0; j < x1.length ; j++)
                distancesMap2.put(j+1 , (float) distanceBtwTwoPoints(x1ForDot[i],x2ForDot[i],x1[j],x2[j]));
            distancesOfIndicesForDots.add((HashMap<Integer, Float>) distancesMap2);
        }
        int error = 0;
        int estimate = 0;
        int countForError = 0;
        int counter = 0;
        ArrayList<Integer> neighbours = new ArrayList<Integer>();
        ArrayList<Integer> positives = new ArrayList<Integer>();
        ArrayList<Integer> negatives = new ArrayList<Integer>();
        for(int  i= 0; i<m;i++)
            finalMatrix[x2[i]][x1[i]] = y[i];
        for(int i = 0; i < kfold; i++)
        {
            for(int j = 0; j < minNumberInFold+1; j++)
            {
                if(partitions[i][j] != 0)
                {
                    sortedKeys2 = sortHashMapByValue(distancesOfIndices.get(counter));
                    counter++;
                    for(int k = 0; k < Math.min(knn,sortedKeys2.size());k++ )
                    {
                        if(sortedKeys2.get(k) != null)
                            neighbours.add(sortedKeys2.get(k));
                    }
                    for(int l = 0; l < neighbours.size() ; l++)
                    {
                        if(y[neighbours.get(l)-1] == 0)
                            negatives.add(neighbours.get(l));
                        else
                            positives.add(neighbours.get(l));
                    }
                    if(positives.size() > negatives.size())
                        estimate = 1;
                    else
                        estimate = 0;
                    if(y[partitions[i][j] - 1] != estimate)
                        error++;
                    neighbours.clear();
                    positives.clear();
                    negatives.clear();
                }
            }
        }
        for(int j = 0; j < distancesOfIndicesForDots.size() ; j++)
        {
            sortedKeys2 = sortHashMapByValue(distancesOfIndicesForDots.get(j));
            for(int k = 0; k < Math.min(knn,sortedKeys2.size());k++ )
            {
                if(sortedKeys2.get(k) != null)
                    neighbours.add(sortedKeys2.get(k));
            }
            for(int l = 0; l < neighbours.size() ; l++)
            {
                if(y[neighbours.get(l)-1] == 0)
                    negatives.add(neighbours.get(l));
                else
                    positives.add(neighbours.get(l));
            }
            if(positives.size() > negatives.size())
                estimate = 1;
            else
                estimate = 0;
            finalMatrix[x2ForDot[j]][x1ForDot[j]] = estimate;
            neighbours.clear();
            positives.clear();
            negatives.clear();
        }
        sc2.close();
        return error;
    }
    public static ArrayList<Integer> sortHashMapByValue(final HashMap<Integer, Float> map)
    {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.addAll(map.keySet());
        ArrayList<Integer> sortedKeys = new ArrayList<Integer>();
        Collections.sort(keys, new Comparator<Integer>()
        {
            @Override
            public int compare(Integer arg0, Integer arg1)
            {
                Float f1 = map.get(arg0);
                Float f2 = map.get(arg1);
                if (f1 == null)
                    return (f2 != null) ? 1 : 0;
                else if ((f1 != null) && (f2 != null))
                    return  f1.compareTo(f2);
                else
                    return 0;
            }
        });
        for (Integer key : keys)
            sortedKeys.add(key);
        return sortedKeys;
    }
    private static double distanceBtwTwoPoints(int x11, int x12, int x21, int x22)
    {
        return Math.sqrt(Math.pow((x11 - x21),2) + Math.pow((x12 - x22),2) );
    }
}