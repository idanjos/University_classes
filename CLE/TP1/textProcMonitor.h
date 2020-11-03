/**
 *  \file fifo.c (implementation file)
 *
 *  \brief Problem name: Producers / Workerss.
 *
 *  Synchronization based on monitors.
 *  Both threads and the monitor are implemented using the pthread library which enables the creation of a
 *  monitor of the Lampson / Redell type.
 *
 *  Data transfer region implemented as a monitor.
 *
 *  Definition of the operations carried out by the producers / Workerss:
 *     \li putVal
 *     \li getVal.
 *
 *  \author Borys Chystov , Isaac dos Anjos
 */

#ifndef CLE1_H_
#define CLE1_H_

/**
 *  \brief Function setFileNames.
 *
 *  Operation carried out by the Main thread.
 *
 *  \param numOfFiles, correspondes the number of input files given by the user
 *  \param filePaths, array of input files.
 *
 */
extern void setFileNames(unsigned int num, char *fName[]);

/** getTextChunk
 *  \brief Read the next chunk, upto B bytes.
 *
 *  Operation carried out by the Workerss.
 *
 *  \param id Workers identification
 *  \param chunk, the Worker's chunk reference
 *  \param chunkCounter, the Worker's chunkCounter reference, represents the size of the chunk.
 *  \param fileId, the Worker's fileId reference, indicates the current file that the chunk belongs to.
 *
 *  \return 1 if there is more work to do. Otherwise 0.
 *
 */
extern int getTextChunk(unsigned int id, char *chunk, int *chunkCounter,int *fileId);

/** processChunk
 *  \brief Process the results of the chunk.
 *
 *  Operation carried out by the Workerss.
 *
 *  \param id Workers identification.
 *  \param chunk, the Worker's chunk reference
 *  \param chunkCounter, the Worker's chunkCounter reference, represents the size of the chunk.
 *	\param count, the Worker's count reference, represents the number of words encountered.
 *  \param V_counters, the Worker's V_counters reference, represents the frequency of vowels within a word size appeared in the chunk.
 *  \param counters, the Worker's counters reference, represents the frequency of word sizes appeared in the chunk.
 *  \param largestWord, the Worker's largestWord reference, indicates the largestWord encountered.
 *
 */
extern void processChunk(char *chunk,int chunkCounter, int *count, int V_counters[50][50], int counters[50], int *largestWord);

/** saveResults
 *  \brief Saves the results of the chunk.
 *
 *  Operation carried out by the Workerss.
 *
 *  \param id Workers identification.
 *  \param temp_count, the Worker's count reference, represents the number of words encountered.
 *  \param temp_V_counters, the Worker's V_counters reference, represents the frequency of vowels within a word size appeared in the chunk.
 *  \param temp_counters, the Worker's counters reference, represents the frequency of word sizes appeared in the chunk.
 *  \param temp_largestWord, the Worker's largestWord reference, indicates the largestWord encountered.
 *  \param fileId, the Worker's fileId reference, indicates the current file that the chunk belongs to.
 *
 *  \return 1 if there is more work to do. Otherwise 0.
 *
 */
extern void saveResults(int id, int *count, int V_counters[50][50], int counters[50], int *largestWord, int *fileId);

/**
 *  \brief Function printResults.
 *
 *  Operation carried out by the Main thread.
 *
 */
extern void printResults();
#endif /* CLE1_H_ */
