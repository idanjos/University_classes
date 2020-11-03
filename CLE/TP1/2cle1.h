/**
 *  \file probConst.h (interface file)
 *
 *  \brief Problem name: Multithreading counter.
 *
 *  Problem simulation parameters.
 *
 *  \author Borys Chystov, Isaac dos Anjos
 */

#ifndef CLE1_H_
#define CLE1_H_


/**
 *  \brief Function formatChar.
 *
 *  Its role is to convert special symbols of the portuguese language into uppercased ASCII characters.
 *
 *  \param c, unsigned char, which represents the first byte of the symbol.
 *  \param f, FILE pointer, help reads the following bytes of the special symbol.
 *
 *  \return ASCII character.
 */
extern unsigned char formatChar(unsigned char c, FILE * f);

/**
 *  \brief Function isWhitespaceOrSeparationOrPounctuationSymbol.
 *
 *  Its role is to check if the given character is a whitespace, separation or punctuation symbol.
 *
 *  \param x, unsigned char, which represents the given character.
 *
 *  \return 1 if its a whitespace, separation or punctuation symbol. Otherwise 0.
 */
extern int isWhitespaceOrSeparationOrPunctuationSymbol(unsigned char x);

/**
 *  \brief Function isAlphanumericOrUnderscoreOrApostrophe.
 *
 *  Its role is to check if the given character is a alphanumeric, underscore or apostrophe symbol.
 *
 *  \param x, unsigned char, which represents the given character.
 *
 *  \return 1 if its a alphanumeric, underscore or apostrophe symbol. Otherwise 0.
 */
extern int isAlphanumericOrUnderscoreOrApostrophe(unsigned char x);

/**
 *  \brief Function isVowel.
 *
 *  Its role is to check if the given character is a vowel.
 *
 *  \param x, unsigned char, which represents the given character.
 *
 *  \return 1 if its a vowel. Otherwise 0.
 */
extern int isVowel(unsigned char x);

/**
 *  \brief Function processChunk.
 *
 *  Its role is process the given chunk of up to B bytes.
 *
 *  \param chunk, char pointer, which represents the given chunk.
 *  \param chunkCounter corresponde to the length of the given chunk.
 *  \param count, the Worker's count reference, represents the amount of words encountered in the chunk.
 *  \param V_counters, the Worker's Vowel counter matrix, represents the amount of vowels encountered in each word length in the chunk.
 *  \param counters, the Worker's counters, represents the amount of words encountered in each word length in the chunk.
 *  \param largestWord, the Worker's largestWord reference, represents the largestWord encountered in the chunk.  
 */
extern void processChunk(char * chunk, int chunkCounter, int * count, int V_counters[50][50], int counters[50], int * largestWord);

#endif /* CLE1_H_ */
