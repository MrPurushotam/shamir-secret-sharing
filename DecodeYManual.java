import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class DecodeYManual {
    static BigInteger lagrangeInterpolation(TreeMap<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            int xi = (int) points.keySet().toArray()[i];
            BigInteger yi = points.get(xi);

            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j)
                    continue;
                int xj = (int) points.keySet().toArray()[j];
                num = num.multiply(BigInteger.valueOf(-xj));
                den = den.multiply(BigInteger.valueOf(xi - xj));
            }

            BigInteger li = num.multiply(yi).divide(den);
            result = result.add(li);
        }

        return result;
    }


    static void generateCombinations(List<Map.Entry<Integer, BigInteger>> arr, int k, int index,
            List<Map.Entry<Integer, BigInteger>> current,
            List<List<Map.Entry<Integer, BigInteger>>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        if (index == arr.size())
            return;

        current.add(arr.get(index));
        generateCombinations(arr, k, index + 1, current, result);
        current.remove(current.size() - 1);
        generateCombinations(arr, k, index + 1, current, result);
    }

    static BigInteger findBestSecret(TreeMap<Integer, BigInteger> allPoints, int k) {
        List<Map.Entry<Integer, BigInteger>> entries = new ArrayList<>(allPoints.entrySet());
        Map<BigInteger, Integer> frequency = new HashMap<>();

        List<List<Map.Entry<Integer, BigInteger>>> combinations = new ArrayList<>();
        generateCombinations(entries, k, 0, new ArrayList<>(), combinations);

        for (List<Map.Entry<Integer, BigInteger>> combo : combinations) {
            TreeMap<Integer, BigInteger> subset = new TreeMap<>();
            for (Map.Entry<Integer, BigInteger> entry : combo) {
                subset.put(entry.getKey(), entry.getValue());
            }
            BigInteger secret = lagrangeInterpolation(subset, k);
            frequency.put(secret, frequency.getOrDefault(secret, 0) + 1);
        }

        // Find the most frequent secret
        return frequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
    }

    public static void main(String[] args) {
        try {
            String json = readFile("data.json");

            // Remove whitespace for easier parsing
            json = json.replaceAll("\\s+", "");

            // Extract n and k
            int n = Integer.parseInt(extractValue(json, "\"n\":", ","));
            int k = Integer.parseInt(extractValue(json, "\"k\":", "}"));

            // Prepare map
            TreeMap<Integer, String> encodedMap = new TreeMap<>();
            TreeMap<Integer, BigInteger> decodedMap = new TreeMap<>();

            String pattern = "\"(\\d+)\":\\{";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);

            while (m.find()) {
                int x = Integer.parseInt(m.group(1));

                // Find the object content for this key
                int start = m.end();
                int braceCount = 1;
                int end = start;

                while (braceCount > 0 && end < json.length()) {
                    char c = json.charAt(end);
                    if (c == '{')
                        braceCount++;
                    else if (c == '}')
                        braceCount--;
                    end++;
                }

                String chunk = json.substring(start, end - 1);

                String baseStr = extractValue(chunk, "\"base\":\"", "\"");
                String valStr = extractValue(chunk, "\"value\":\"", "\"");
                int base = Integer.parseInt(baseStr);
                BigInteger decoded = new BigInteger(valStr, base);

                encodedMap.put(x, valStr);
                decodedMap.put(x, decoded);
            }

            // Output
            System.out.println("n = " + n + ", k = " + k);
            System.out.println("Decoded values (x, y coordinates):");
            for (int x : decodedMap.keySet()) {
                System.out.println("x = " + x + ", y = " + decodedMap.get(x) +
                        " (encoded: " + encodedMap.get(x) + ")");
            }

            BigInteger secret = findBestSecret(decodedMap, k);
            System.out.println("\nMost likely secret (f(0)) = " + secret);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);
        br.close();
        return sb.toString();
    }

    static String extractValue(String json, String key, String endChar) {
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf(endChar, start);
        return json.substring(start, end);
    }
}