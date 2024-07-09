# Spam Email Classification

This project implements a Naive Bayes classifier to detect spam emails based on the content of the emails. It consists of two main components:

1. **EmailCSVParser**: Parses a CSV file containing emails labeled as spam or ham (not spam), and extracts word counts for both categories.
2. **SpamClassification**: Uses the parsed data to train a Naive Bayes classifier and classify new emails as spam or ham.

## Files

- 'EmailCSVParser.java': reads and parses the CSV dataset, counting the occurrence of each word in spam and ham emails.
- 'SpamClassification.java': trains the Naive Bayes classifier and classifies new emails.
- 'spam_ham_dataset.csv': The dataset used for training the classifier, containing labeled emails.
