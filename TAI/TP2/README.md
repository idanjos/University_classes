For this project premake was used. Basically, it generates compile configurations for a chosen compiler,
which means it can be thought as a unifying language for compiling configurations.

The premake's configuration is the premake5.lua file. In order to automate the building and running process
we made 3 scripts for windows, and 3 for unix systems, but they basically do the same thing.

The build script calls internally premake and then compiles the source code. The run script
looks for the right executable in the bin (binary) folder and executes it with the given parameters.
The reset script simply deletes all files generated by the build script.

##Requirements##
apt install libsndfile-dev

## BUILD ##
./build[.sh]

## RUN ##
WAVHIST:
./run[.sh] wavhist [input file] [channel]
- input file: the file's name of the song
- channel (optional): selecting a channel which the file supports

WAVQUANT:
./run[.sh] wavquant [bits] [input file] [output file]
- bits: number of bits per sample (this isn't necessarily the actual bits per sample, rather tells how many possible values a sample can have, for instance 3 bits equals 8 different possible values) of the output file
- input file: the file's name of the song to quantize
- output file: the file's name of the quantized file

WAVCMP:
./run[.sh] wavcmp [audio file] [original audio file]
- audio file: the file's name of the quantized file
- original audio file: the file's name of the 

WAVCB:
./run[.sh] wavcb [vector size] [overlap factor] [cluster size] [input file|directory] [output file|directiory] [max iterations]
- vector size: the size of the vector (number of samples)
- overlap factor: how many samples are shared between vectors
- cluster size: how many clusters are generated
- input (file|directory): path of audio file(s) to generate codebooks about
- output (file|directory): path of file(s) of the generated codebooks
- max iterations (optional): maximum number of iterations of the k-means algorithm

WAVFIND:
./run[.sh] wavfind [vector size] [overlap factor] [input file] [directiory]
- vector size: the size of the vector (number of samples), it should be the same as the one used in wavcb
- overlap factor: how many samples are shared between vectors, it should also be the same as the one used in wavcb
- input file: audio file to classify
- directory: location of codebooks used in the classification

## RESET ##
./reset[.sh]

## How to run a concrete example ##
0. Verify the requirements above!
apt install libsndfile-dev

1. First thing, execute 

	./build.sh

This will build all of the applications.

2. Run WAVHIST

	./run.sh wavhist example/sample.wav 0

This will read the sample.wav and print out all of the data points in channel 0.

3. Run WAVQUANT
	
	./run.sh wavquant 3 sample.wav sample3.wav

WAVQUANT will quantify the music sample.wav down to 3bits. 

Returns the Nº of frames, sample rate and channels of the input file.

4. Run WAVCMP

	./run[.sh] wavcmp example/s3.wav example/sample.wav

This will calculate the SNR and Maximum per sample absolute error of the original with the quantified file. 

Returns the Nº of frames, sample rate, Nº of channels, Signal Noise Ratio and Maximum per sample absolute error.

5. Run WAVCB

	./run.sh wavcb 20 10 10 example/sample.wav example/codebooks 10

(Descriptions)

6. Run WAVFIND
	
	./run.sh wavfind 20 10 sample.wav example/

Descriptions: