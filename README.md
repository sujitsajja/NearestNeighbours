# NearestNeighbours
Machine Learning Algorithm

The input consists of two files. The first file contains cross-validation information, and the second file contains the data.

The first file
The numbers in each row are separated by a single space.
The first number is the k of k-fold, to be used in the k-fold cross validation scheme.
The second number is m, the number of examples.
The third number is t, the number of random permutations.

The second file
The numbers and characters in each row are separated by a single space.
The first line has two numbers: rows cols.
This is followed by a grid of size rows by cols.
Each entry in the grid is on eof {+;-;.}, where + indicates a positive example, - indicates a negative example, and . indicates that the location is not an example.

Output
The output file contains evaluation of k-nearest neighbors for k = 1; 2; 3; 4; 5.
In each case the following is produced:
1. The estimate e of for the error.
2. The estimate sigma for the error standard deviation.
3. The labeling of the entire grid according to k-nearest neighbors.
