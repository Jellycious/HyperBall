# CODE
All code can be found on [Github](https://github.com/Jellycious/HyperBall).

## Downloading Graphs
Graphs can be downloaded from [WebGraph](http://webgraph.di.unimi.it/). 
To generate the .offsets necessary for loading the graphs you can run the generateOffsets function in Graphs.java. 

## Analyzing Graphs
An example of analyzing a graph can be found in GraphAnalyzer.java. It will automatically generate a .dd file and a graph chart. The .dd file is a serialized file of the class DistanceDistribution. This way results are stored for later access.

## Result Analyzer
Some very basic analysis can be done on the .dd files. You can find an example of this in the ResultAnalyzer.java. For the results to be analyzed you first have to generate actual results using BFS or the HyperBall function. All results are stored in the DistanceDistribution class.
