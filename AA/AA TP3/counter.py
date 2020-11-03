import math
import random
import time
import count_min_sketch as cms
import sys

class Counter():

	#Constructor
	def __init__(self):
		pass

	#Exact Counter
	def ExactCount(self,dataset):

		#Variables
		startTime = time.time()
		result = [0 for i in range(0,26)]

		#Stream
		for letter in dataset.split(" "):
			result[ord(letter)-97] += 1;
		
		#Format output
		output = [(result[i],chr(97+i)) for i in range(0,len(result))]
		return (output,self.getsizeof(output),(time.time()-startTime))

	#Misra n' Gries Algorithm
	def MisraGries(self,dataset,k):

		#Variables
		startTime = time.time()
		tokens = []
		result = []

		#Stream
		for letter in dataset.split(" "): 

			#This algorithm depends on order
			result = sorted(result,reverse=True)

			#If letter is in tokens, increment its counter 							
			if letter in tokens:
				for i in range(0,len(result)):
					if result[i][1] == letter:
						result[i] = (result[i][0]+1,letter)
						break

			#If the number tokens is below the threshhold, add letter			
			elif len(tokens) < (k - 1):
				tokens += [letter]
				result += [(1,letter)]

			#Reduce all counters by 1, counters with 0 are removed
			else:
				i = -1
				while True:
					i += 1
					if i >= len(result):
						break
					if result[i][0] == 1:
						tokens.remove(result[i][1])
						if i == (len(result) -1): 	# i is the last index
							result = result[0:i]
						else:
							result = result[0:i] + result[i+1:len(result)]
					else:
						result[i] = (result[i][0]-1,result[i][1])

		#Return
		return (result,self.getsizeof(result),(time.time()-startTime))

	#Manku n' Motwani algorithm
	def MankuMotwani(self,dataset,k):

		#Variables
		startTime = time.time()
		iterations = 0
		result = []
		tokens = []
		delta = 0
		n = 0

		#Stream
		for letter in dataset.split(" "): 
			n += 1

			#If letter is in tokens, increment its counter 
			if letter in tokens:
				for i in range(0,len(result)):
					if result[i][1] == letter:
						result[i] = (result[i][0]+1,letter)
						break

			# Add the counter
			else:
				result += [(1+delta,letter)]
				tokens += [letter]

			#Update delta, remove all counters below delta.
			if math.floor(n/k) != delta:
				delta = math.floor(n/k)
				i=-1
				while True:
					i += 1
					if i >= len(result):
						break
					if result[i][0] < delta:
						tokens.remove(result[i][1])
						if i == (len(result) -1): 		# i is the last index
							result = result[0:i]
						else:
							result = result[0:i] + result[i+1:len(result)]

					


		#Return 	
		return (sorted(result,reverse=True),self.getsizeof(result),(time.time()-startTime))


	#Metwally algorithm
	def Metwally(self,dataset,k):

		#Variables
		startTime = time.time()
		iterations = 0
		result = []
		tokens = []
		n = 0

		#Stream
		for letter in dataset.split(" "): 
			n += 1
			
			#Algorithm depends on order
			result = sorted(result,reverse=True)

			#If letter is in tokens, increment its counter 
			if letter in tokens:
				for i in range(0,len(result)):
					if result[i][1] == letter:
						result[i] = (result[i][0]+1,letter)
						break


			#If the number tokens is below the threshhold, add letter
			elif len(tokens) < k:
				tokens += [letter]
				result += [(1, letter)]

			#Replace the lowest letter with the newest letter, the newest counter is igual to removed counter + 1
			else:
				j = result[(len(result)-1)]
				tokens.remove(j[1])
				tokens +=[letter]
				result = result[0:(len(result)-1)]+[(j[0]+1,letter)]



		#Return
		return (result,self.getsizeof(result),(time.time()-startTime))

	#Count-Min Sketch, Hash counting
	#The 2D array is defined by the relative err(default is 1%) and epsilon(default is 0.001)
	def Count_Min_Sketch(self,dataset,delta, epsilon):

		#Variables
		countminsketch = cms.CountMinSketch(delta=delta, epsilon=epsilon)
		startTime = time.time()
		n = 0
		#Stream
		for letter in dataset.split(" "): 
			n += 1
			#Updates positions of letter
			countminsketch.update(letter)

		#Queries all letters and return
		output = sorted([(countminsketch.query(chr(i+97)),chr(i+97)) for i in range(0,26)],reverse=True)
		return (output,self.getsizeof(output),(time.time()-startTime))

	#https://goshippo.com/blog/measure-real-size-any-python-object/
	def getsizeof(self,obj, seen=None):
	    """Recursively finds size of objects"""
	    size = sys.getsizeof(obj)
	    if seen is None:
	        seen = set()
	    obj_id = id(obj)
	    if obj_id in seen:
	        return 0
	    # Important mark as seen *before* entering recursion to gracefully handle
	    # self-referential objects
	    seen.add(obj_id)
	    if isinstance(obj, dict):
	        size += sum([self.getsizeof(v, seen) for v in obj.values()])
	        size += sum([self.getsizeof(k, seen) for k in obj.keys()])
	    elif hasattr(obj, '__dict__'):
	        size += self.getsizeof(obj.__dict__, seen)
	    elif hasattr(obj, '__iter__') and not isinstance(obj, (str, bytes, bytearray)):
	        size += sum([self.getsizeof(i, seen) for i in obj])
	    return size
			