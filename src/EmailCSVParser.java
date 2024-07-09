import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EmailCSVParser {
    private int spam; //number of spam emails
    private int ham; //number of ham emails
    private Map<String, Integer> uniqueWords; //stores unique words and their counters
    private int totalSpamWords; //total number of words in spam emails
    private int totalHamWords; //total number of words in ham emails
    private Map<String, Integer> spamWords; //stores words in spam emails and their counts
    private Map<String, Integer> hamWords; //stores words in ham emails and their counts

    public EmailCSVParser() {
        this.spam = 0;
        this.ham = 0;
        this.uniqueWords = new HashMap<>();
        this.totalSpamWords = 0;
        this.totalHamWords = 0;
        this.spamWords = new HashMap<>();
        this.hamWords = new HashMap<>();
    }

    private void counter(boolean isSpam, String[] row) {
        for (String message : row) {
            // Convert the message to lowercase and remove punctuation and digits
            message = message.toLowerCase().replaceAll("[\\p{Punct}\\d]", "");
            // split into words
            String[] words = message.split("\\s+");
            for (String word : words) {
                // update uniqueWords
                uniqueWords.put(word, uniqueWords.getOrDefault(word, 0) + 1);
                if (isSpam) {
                    //update spam count
                    totalSpamWords++;
                    spamWords.put(word, spamWords.getOrDefault(word, 0) + 1);
                } else {
                    //update ham count
                    totalHamWords++;
                    hamWords.put(word, hamWords.getOrDefault(word, 0) + 1);
                }
            }
        }
    }

    public Map<String, Object> parse(String trainingData) {
        try (BufferedReader csvReader = new BufferedReader(new FileReader(trainingData))) {
            String row;
            boolean currentlyReadingSpam = false;

            int i = 0;
            while ((row = csvReader.readLine()) != null) {
                if (i == 0) {
                    // skip the header row
                    i++;
                    continue;
                }
                String[] data = row.split(",");

                // Initialize flags for new email and its spam status
                boolean isNewEmail = false;
                boolean isSpam = false;
                try {
                    //check if email ID
                    Integer.parseInt(data[0]);
                    // check if email is ham/spam
                    isNewEmail = data[1].equals("ham") || data[1].equals("spam");
                    if (isNewEmail) {
                        isSpam = data[1].equals("spam");
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // continue with the existing email
                }

                if (isNewEmail) {
                    currentlyReadingSpam = isSpam;
                    if (isSpam) {
                        spam++;
                    } else {
                        ham++;
                    }
                    // count the words in the new email
                    counter(currentlyReadingSpam, getMessageArray(data, 2));
                } else {
                    // Tally the words in the continuation of the current email
                    counter(currentlyReadingSpam, data);
                }

                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("spam", spam);
        result.put("ham", ham);
        result.put("uniqueWords", uniqueWords);
        result.put("totalSpamWords", totalSpamWords);
        result.put("totalHamWords", totalHamWords);
        result.put("spamWords", spamWords);
        result.put("hamWords", hamWords);

        return result;
    }

    private String[] getMessageArray(String[] data, int start) {
        String[] messageArray = new String[data.length - start];
        System.arraycopy(data, start, messageArray, 0, data.length - start);
        return messageArray;
    }

    public static void main(String[] args) {
        EmailCSVParser parser = new EmailCSVParser();
        Map<String, Object> result = parser.parse("spam_ham_dataset.csv");
        System.out.println(result);
    }
}
