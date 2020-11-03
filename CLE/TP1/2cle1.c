/**
 *  \file 2cle1.c (implementation file)
 *
 *  \brief Problem name: General Problem 1, Multithreading
 *
 *  Synchronization based on monitors.
 *  Both threads and the monitor are implemented using the pthread library which enables the creation of a
 *  monitor of the Lampson / Redell type.
 *
 *  Generator thread of the intervening entities.
 *
 *  \author Borys Chystov, Isaac dos Anjos
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>


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
unsigned char formatChar(unsigned char c, FILE *f) {
    unsigned char x = 0;
    if (c >= 0xF0 && c < 0xF8) {
        x = 0;
    } else if (c >= 0xE0) {

        for (int i = 1; i < 3; i++) {
            if ((fscanf(f, "%c", & x)) == EOF) {
                perror("Error in reading special character");
                exit(EXIT_FAILURE);
            }

        }
        switch (x) {
            case 0x93: // dash
                x = 0x2D;
                break;
            case 0x98: //single quotation marks
            case 0x99: //single quotation marks
                x = 0x27;
                break;
            case 0x9C: //double quotation marks
            case 0x9D: //double quotation marks
                x = 0x22;
                break;
            case 0xA6: //ellipsis
                x = 0x2E;
                break;
        }

    } else if (c == 0xC3) {

        if ((fscanf(f, "%c", & x)) == EOF) {
            perror("Error in reading special character");
            exit(EXIT_FAILURE);
        }
        switch (x) {
            case 0x81: // Á 		
            case 0x80: // À
            case 0x82: // Â
            case 0x83: // Ã
            case 0xA1: // á
            case 0xA0: // à
            case 0xA2: // â
            case 0xA3: // ã
                x = 0x41;
                break;
            case 0x89: // É
            case 0x88: // È
            case 0x8A: // Ê
            case 0xA9: // é
            case 0xA8: // è
            case 0xAA: // ê
                x = 0x45;
                break;
            case 0x8D: // Í
            case 0x8C: // Ì
            case 0xAD: // í
            case 0xAC: // ì
                x = 0x49;
                break;
            case 0x93: // Ó
            case 0x92: // Ò
            case 0x94: // Ô
            case 0x95: // Õ
            case 0xB3: // ó
            case 0xB2: // ò
            case 0xB4: // ô
            case 0xB5: // õ
                x = 0x4F;
                break;
            case 0x9A: // Ú
            case 0x99: // Ù
            case 0xBA: // ú
            case 0xB9: // ù
                x = 0x55;
                break;
            case 0x87: // Ç;
            case 0xA7: // ç
                x = 0x43;
                break;
        }

    } else if (c < 0x80) {
        x = c;
    }
    return x;
}

/**
 *  \brief Function isWhitespaceOrSeparationOrPounctuationSymbol.
 *
 *  Its role is to check if the given character is a whitespace, separation or punctuation symbol.
 *
 *  \param x, unsigned char, which represents the given character.
 *
 *  \return 1 if its a whitespace, separation or punctuation symbol. Otherwise 0.
 */
int isWhitespaceOrSeparationOrPunctuationSymbol(unsigned char x) {
    switch (x) {
        case 0x9: // \t
        case 0xA: // \n
        case 0xD: // \r
        case 0x20: // Space
        case 0x21: // !
        case 0x22: // "
        case 0x28: // (
        case 0x29: // )
        case 0x2C: // ,
        case 0x2D: // -
        case 0x2E: // .
        case 0x3A: // :
        case 0x3B: // ;
        case 0x3F: // ?
        case 0x5B: // [
        case 0x5D: // ]
            return 1;
    }
    return 0;
}

/**
 *  \brief Function isAlphanumericOrUnderscoreOrApostrophe.
 *
 *  Its role is to check if the given character is a alphanumeric, underscore or apostrophe symbol.
 *
 *  \param x, unsigned char, which represents the given character.
 *
 *  \return 1 if its a alphanumeric, underscore or apostrophe symbol. Otherwise 0.
 */
int isAlphanumericOrUnderscoreOrApostrophe(unsigned char x) {
    switch (x) {
        case 0x27: // '
        case 0x30: // 0
        case 0x31: // 1
        case 0x32: // 2
        case 0x33: // 3
        case 0x34: // 4
        case 0x35: // 5
        case 0x36: // 6
        case 0x37: // 7
        case 0x38: // 8
        case 0x39: // 9
        case 0x41: // A
        case 0x42: // B
        case 0x43: // C
        case 0x44: // D
        case 0x45: // E
        case 0x46: // F
        case 0x47: // G
        case 0x48: // H
        case 0x49: // I
        case 0x4A: // J
        case 0x4B: // K
        case 0x4C: // L
        case 0x4D: // M
        case 0x4E: // N
        case 0x4F: // O
        case 0x50: // P
        case 0x51: // Q
        case 0x52: // R
        case 0x53: // S
        case 0x54: // T
        case 0x55: // U
        case 0x56: // V
        case 0x57: // W
        case 0x58: // X
        case 0x59: // Y
        case 0x5A: // Z
        case 0x61: // a
        case 0x62: // b
        case 0x63: // c
        case 0x64: // d
        case 0x65: // e
        case 0x66: // f
        case 0x67: // g
        case 0x68: // h
        case 0x69: // i
        case 0x6A: // j
        case 0x6B: // k
        case 0x6C: // l
        case 0x6D: // m
        case 0x6E: // n
        case 0x6F: // o
        case 0x70: // p
        case 0x71: // q
        case 0x72: // r
        case 0x73: // s
        case 0x74: // t
        case 0x75: // u
        case 0x76: // v
        case 0x77: // w
        case 0x78: // x
        case 0x79: // y
        case 0x7A: // z
        case 0x5F: // _
            return 1;
    }
    return 0;
}

/**
 *  \brief Function isVowel.
 *
 *  Its role is to check if the given character is a vowel.
 *
 *  \param x, unsigned char, which represents the given character.
 *
 *  \return 1 if its a vowel. Otherwise 0.
 */
int isVowel(unsigned char x) {
    switch (x) {
        case 0x41: // A
        case 0x61: // a
        case 0x45: // E
        case 0x65: // e
        case 0x49: // I
        case 0x69: // i
        case 0x4F: // O
        case 0x6F: // o
        case 0x55: // U
        case 0x75: // u
            return 1;
    }
    return 0;
}

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
void processChunk(char *chunk, int chunkCounter, int *count, int V_counters[50][50], int counters[50], int *largestWord) {
    int charCount = 0;
    int vowelCount = 0;
    int tempCount;
    tempCount = * count;
    int tempLargestWord;
    tempLargestWord = * largestWord;
    const unsigned char apostrophe = 0x27;
    for (int i = 0; i < 50; i++) {
        counters[i] = 0;
        for (int j = 0; j < 50; j++)
            V_counters[i][j] = 0;
    }
    int index = 0;
    while (index < chunkCounter) {
        unsigned char normalizedChar = chunk[index];
        index++;
        if (isWhitespaceOrSeparationOrPunctuationSymbol(normalizedChar)) {
            if (charCount > 0) {
                V_counters[vowelCount][charCount]++;
                counters[charCount]++;
                tempCount++;
            } else {
                continue;
            }
            if (charCount > tempLargestWord) {
                tempLargestWord = charCount;
            }
            vowelCount = 0;
            charCount = 0;
        } else if (isAlphanumericOrUnderscoreOrApostrophe(normalizedChar) && normalizedChar != apostrophe) {
            if (isVowel(normalizedChar))
                vowelCount++;
            charCount++;
        }
    }

    if (charCount) {
        V_counters[vowelCount][charCount]++;
        counters[charCount]++;
        tempCount++;
        if (charCount > tempLargestWord) {
            tempLargestWord = charCount;
        }
    }
    * largestWord = tempLargestWord;
    * count = tempCount;
}
