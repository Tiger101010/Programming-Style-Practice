import sys, string
import numpy as np
from collections import Counter


# Example input: "Hello  World!" 
characters = np.array([' ']+list(open(sys.argv[1]).read())+[' '])
# Result: array([' ', 'H', 'e', 'l', 'l', 'o', ' ', ' ', 
#           'W', 'o', 'r', 'l', 'd', '!', ' '], dtype='<U1')


# Normalize
characters[~np.char.isalpha(characters)] = ' '
characters = np.char.lower(characters)
# Result: array([' ', 'h', 'e', 'l', 'l', 'o', ' ', ' ', 
#           'w', 'o', 'r', 'l', 'd', ' ', ' '], dtype='<U1')

# Leetify
leet = {'a' : '4', 'e' : '3', 'i' : '1', 'o' : '0', 'u' : '_'}
# https://stackoverflow.com/questions/67770868/replace-string-character-in-np-array
characters = np.vectorize(lambda x: leet.get(x, x))(characters)


### Split the words by finding the indices of spaces
sp = np.where(characters == ' ')
# Result: (array([ 0, 6, 7, 13, 14], dtype=int64),)
# A little trick: let's double each index, and then take pairs
sp2 = np.repeat(sp, 2)
# Result: array([ 0, 0, 6, 6, 7, 7, 13, 13, 14, 14], dtype=int64)
# Get the pairs as a 2D matrix, skip the first and the last
w_ranges = np.reshape(sp2[1:-1], (-1, 2))
# Result: array([[ 0,  6],
#                [ 6,  7],
#                [ 7, 13],
#                [13, 14]], dtype=int64)
# Remove the indexing to the spaces themselves
w_ranges = w_ranges[np.where(w_ranges[:, 1] - w_ranges[:, 0] > 2)]
# Result: array([[ 0,  6],
#                [ 7, 13]], dtype=int64)

# Voila! Words are in between spaces, given as pairs of indices
words = list(map(lambda r: characters[r[0]:r[1]], w_ranges))
# Result: [array([' ', 'h', 'e', 'l', 'l', 'o'], dtype='<U1'), 
#          array([' ', 'w', 'o', 'r', 'l', 'd'], dtype='<U1')]
# Let's recode the characters as strings
swords = np.array(list(map(lambda w: ''.join(w).strip(), words)))
# Result: array(['hello', 'world'], dtype='<U5')


# Next, let's remove stop words
stop_words = list(open('../stop_words.txt').read())
# Leetify
stop_words = list(map(lambda x: leet.get(x, x), stop_words))
stop_words = np.array(list(set(''.join(stop_words).split(','))))

ns_words = swords[~np.isin(swords, stop_words)]

# Generate 2-grams
two_grams = zip(ns_words, ns_words[1:])
count = Counter(two_grams)

# Print 5 most common 2-grams
for w, c in count.most_common(5):
    print(w[0], w[1], end='')
    print(' - ', end='')
    print(c)