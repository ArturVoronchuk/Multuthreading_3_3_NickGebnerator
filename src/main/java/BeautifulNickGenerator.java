import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class BeautifulNickGenerator {

    public static AtomicInteger[] lettersAtoms = new AtomicInteger[3];

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void checkStrings(String[] texts, Predicate<String> condition) {
        for (String str : texts) {
            if (condition.test(str)) {
                lettersAtoms[str.length() - 3].incrementAndGet();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 3; i++) {
            lettersAtoms[i] = new AtomicInteger();
        }

        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        Runnable palindrome = () -> checkStrings(texts, str -> {
            int forward = 0;
            int backward = str.length() - 1;
            while (backward > forward) {
                if (str.charAt(forward++) != str.charAt(backward--)) {
                    return false;
                }
            }
            return true;
        });

        Runnable sameLetter = () -> checkStrings(texts, str -> {
            for (int i = 0; i < str.length() - 1; i++) {
                if (str.charAt(i) != str.charAt(i + 1)) {
                    return false;
                }
            }
            return true;
        });

        Runnable increaseLetter = () -> checkStrings(texts, str -> {
            for (int i = 0; i < str.length() - 1; i++) {
                if (str.charAt(i) > str.charAt(i + 1)) {
                    return false;
                }
            }
            return true;
        });

        palindrome.run();
        sameLetter.run();
        increaseLetter.run();

        System.out.println("Красивых слов с длиной 3: " + lettersAtoms[0].get());
        System.out.println("Красивых слов с длиной 4: " + lettersAtoms[1].get());
        System.out.println("Красивых слов с длиной 5: " + lettersAtoms[2].get());
    }
}