import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpamClassification {
    private final String trainingData;
    private int spam; //counter for spam emails
    private int notSpam; //counter for non-spam emails
    private Map<String, Integer> uniqueWords; // stores unique words and their counts
    private int totalSpamWords; //total number of words in spam emails
    private int totalHamWords; //total number of words in non-spam emails
    private Map<String, Integer> spamWords; //stores words in spam emails and their count
    private Map<String, Integer> hamWords; //stores words in non-spam emails and their count

    public SpamClassification(String trainingData) {
        this.trainingData = trainingData;
        this.spam = 0;
        this.notSpam = 0;
        this.uniqueWords = new HashMap<>();
        this.totalSpamWords = 0;
        this.totalHamWords = 0;
        this.spamWords = new HashMap<>();
        this.hamWords = new HashMap<>();
        train();
    }

    private void train() {
        EmailCSVParser parser = new EmailCSVParser();
        Map<String, Object> result = parser.parse(trainingData);
        System.out.println("Finished training with data from " + trainingData);
        this.spam = (int) result.get("spam");
        this.notSpam = (int) result.get("ham");
        this.uniqueWords = (Map<String, Integer>) result.get("uniqueWords");
        this.totalSpamWords = (int) result.get("totalSpamWords");
        this.totalHamWords = (int) result.get("totalHamWords");
        this.spamWords = (Map<String, Integer>) result.get("spamWords");
        this.hamWords = (Map<String, Integer>) result.get("hamWords");
    }

    // Calculate the log probability of words given that the email is spam
    private double logPWordsGivenSpam(String[] email) {
        double sum = 0;
        int numUniqueWords = uniqueWords.size();
        for (String word : email) {
            int nominator = spamWords.getOrDefault(word, 0) + 1;
            int denominator = totalSpamWords + numUniqueWords;
            sum += Math.log((double) nominator / denominator);
        }
        return sum;
    }

    // Calculate the log probability of words given that the email is not spam
    private double logPWordsGivenHam(String[] email) {
        double sum = 0;
        int numUniqueWords = uniqueWords.size();
        for (String word : email) {
            int nominator = hamWords.getOrDefault(word, 0) + 1;
            int denominator = totalHamWords + numUniqueWords;
            sum += Math.log((double) nominator / denominator);
        }
        return sum;
    }

    //converts to lower case and removing punctuation and digits
    private String[] clean(String email) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader(email));
        String line;
        StringBuilder allWords = new StringBuilder();

        while ((line = file.readLine()) != null) {
            line = line.toLowerCase();
            line = line.replaceAll("[\\p{Punct}\\d]", "");
            allWords.append(line).append(" ");
        }

        file.close();
        return allWords.toString().split("\\s+");
    }
    //calculate with log because it is more accurate
    //classify the email as spam or not
    public boolean classify(String email) throws IOException {
        String[] cleanedEmail = clean(email);
        //probability an email is spam is no. spam emails / total no. emails
        double logPSpam = Math.log((double) spam / (spam + notSpam));
        //probability an email is ham is no. ham emails / total no. emails
        double logPHam = Math.log((double) notSpam / (spam + notSpam));
        // Calculate the log likelihood of the words in the email given that the email is spam
        double logPWordsGivenSpam = logPWordsGivenSpam(cleanedEmail);
        // Calculate the log likelihood of the words in the email given that the email is ham
        double logPWordsGivenHam = logPWordsGivenHam(cleanedEmail);
        //combined probabilities
        double logProbabilityOfSpam = logPSpam + logPWordsGivenSpam;
        double logProbabilityOfHam = logPHam + logPWordsGivenHam;

        return logProbabilityOfSpam > logProbabilityOfHam;
    }

    public static void main(String[] args) throws IOException {
        SpamClassification classifier = new SpamClassification("/Users/yoavoselka/Desktop/SpamClassification/src/spam_ham_dataset.csv");
        boolean isSpam = classifier.classify("/Users/yoavoselka/Desktop/SpamClassification/src/email");
        System.out.println("Is spam: " + isSpam);
    }
}
