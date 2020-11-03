/**
 *  \file fifo.c (implementation file)
 *
 *  \brief Problem name: Producers / Consumers.
 *
 *  Synchronization based on monitors.
 *  Both threads and the monitor are implemented using the pthread library which enables the creation of a
 *  monitor of the Lampson / Redell type.
 *
 *  Data transfer region implemented as a monitor.
 *
 *  Definition of the operations carried out by the producers / consumers:
 *     \li putVal
 *     \li getVal.
 *
 *  \author Borys Chystov , Isaac dos Anjos
 */
#ifndef CLE1_H_
#define CLE1_H_

/** processData
 *  \brief Process the data.
 *
 *  Operation carried out by the Workers.
 *
 *  \param a, the Worker's "a" reference.
 *  \param b, the Worker's "b" reference.
 *  \param result, the Worker's fileId reference, indicates the current file that the chunk belongs to.
 *
 *  \return 1 if there is more work to do. Otherwise 0.
 *
 */
extern void processData(double *a, double *b, double *result);

/** getData
 *  \brief Read the next data.
 *
 *  Operation carried out by the Workers.
 *
 *  \param id consumer identification
 *  \param index, the Worker's index reference
 *  \param a, the Worker's "a" reference.
 *  \param b, the Worker's "b" reference.
 *  \param fileId, the Worker's fileId reference, indicates the current file that the chunk belongs to.
 *
 *  \return 1 if there is more work to do. Otherwise 0.
 *
 */
extern int getData (unsigned int id, int *index, double *a, double *b, int *fileId);

/**
 *  \brief Function setFileNames.
 *
 *  Operation carried out by the Main thread.
 *
 *  \param numOfFiles, correspondes the number of input files given by the user
 *  \param filePaths, array of input files.
 *
 */
extern void setFileNames(unsigned int numOfFiles, char *filePaths[]);

/** saveResults
 *  \brief Saves the results of the chunk.
 *
 *  Operation carried out by the Workers.
 *
 *  \param id consumer identification.
 *  \param index, the Worker's index reference
 *  \param result, the Worker's result reference, the result of the operation.
 *  \param fileId, the Worker's fileId reference, indicates the current file that the chunk belongs to.
 *
 *  \return 1 if there is more work to do. Otherwise 0.
 *
 */
extern void saveResults(int id,int *index, double *result, int *fileId);

/**
 *  \brief Function printResults.
 *
 *  Operation carried out by the Main thread.
 *
 */
extern void printResults();

#endif /* CLE1_H_ */