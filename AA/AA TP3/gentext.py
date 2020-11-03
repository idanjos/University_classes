import random

class GenText():
	def __init__(self):
		#Class attributes
		self.alphabet = [chr(x+97) for x in range(0,26)]

	#Text generator, returns a String
	def generate(self,n,weights=None):
		return " ".join(random.choices(population=self.alphabet,weights=weights,k=n))

