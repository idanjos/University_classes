
import random
import graph
import argparse


# Argument parser
def parseArguments():
    # Create argument parser
    parser = argparse.ArgumentParser()
    parser.add_argument("nodes", help="Num. of Solutions.", type=int)
    # Optional arguments
    parser.add_argument("-c", "--cputime", help="CPU limit in seconds", type=float, default=0.0)
    parser.add_argument("-s", "--solutions",  help="Num. of Solutions.", type=int, default=0)
    parser.add_argument("-p", "--probability",  help="Probability of an Edge.", type=float, default=0.5)
    parser.add_argument("-k", "--k",  help="K Combinations.", type=int, default=100)
   
    # Print version
    parser.add_argument("--version", action="version", version='%(prog)s - Version 1.0')

    # Parse arguments
    args = parser.parse_args()

    return args

# Graph generator for N nodes with p probability of creating an edge between 2 nodes
def generateGraph(N,p=0.5):
	output = [["" for i in range(N)] for j in range(N)]
	for i in range(0,N-1):
		for j in range(i+1,N):
			if i != j:
				if output[j][i] == "": 
					output[i][j]=1 if random.random() > p else 0
				else:
					output[i][j] = output[j][i]
	return output



def main():
	args = parseArguments()
	if not (args.probability >= 0 and args.probability <= 1):
		print("Probability must be between 0 and 1\nInput:"+str(args.probability))
		exit(1)
	

	# Current graph	
	matrix = generateGraph(args.nodes,1-args.probability)
	
	graphSolver = graph.Graph(matrix,args.cputime,args.solutions,k=args.k)
	
	graphs = [graphSolver.solveV1(),graphSolver.solveV2(),graphSolver.solveV3(),graphSolver.solveV4()]

	print(graphs)

	


  
if __name__== "__main__":
  main()