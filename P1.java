import java.util.*;

public class P1 {

    private static Map<Integer, Set<String>> inputData = new HashMap<Integer, Set<String>>();

    @SuppressWarnings("unchecked")
    public static void main(String arg[]) {

        // Reads in threshold.
        Scanner input = new Scanner(System.in);
        System.out.println("Please input a minimum support threshold and a list of items: ");
        int threshold = 0;
        try {
            // Skips through empty lines and comments and trims whitespaces.
            while (input.hasNextLine()) {
                String currentLineInputFormatted = processInput(input.nextLine());
                if (currentLineInputFormatted.isEmpty()) {
                    continue;
                }
                threshold = Integer.valueOf(currentLineInputFormatted);
                break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Threshold can only be a positive integer!");
            return;
        }
        if (threshold <= 0) {
            System.out.println("Threshold can only be a positive integer!");
            return;
        }


        Map<String, Integer> singleItemsCount = new HashMap<String, Integer>();
        int inputLine = 1;

        // Reads in items set per line, split them by comma, and then saves them to inputData.
        while (input.hasNextLine()) {
            String lineInput = input.nextLine();
            if (processInput(lineInput).isEmpty()) {
                continue;
            }
            Set<String> inputItemsPerLine = new HashSet<String>();
            String[] splitedItems = lineInput.split(",");
            for (int i = 0; i < splitedItems.length; i++) {
                String itemName = splitedItems[i].trim();
                int count = singleItemsCount.getOrDefault(itemName, 0) + 1;
                singleItemsCount.put(itemName, count);
                inputItemsPerLine.add(itemName);
            }
            inputData.put(inputLine, inputItemsPerLine);
            inputLine++;
        }

        System.out.println("The frequent item sets are: ");

        // Finds out non-single frequent items.
        findFreqItems(threshold, singleItemsCount);
    }

    //skips through empty lines and comments and trims whitespaces.
    @SuppressWarnings("unchecked")
    private static String processInput(String lineInput) {
        if (lineInput.isEmpty() || lineInput.startsWith("#")) {
            return "";
        }
        if (lineInput.indexOf("#") != -1) {
            return lineInput.substring(0, lineInput.indexOf("#")).trim();
        }
        return lineInput.trim();
    }

    // Formats output from given set of string. E.g. {a} => "a"  {a,b} => "a,b".
    private static String formatItems(Set<String> itemSet) {
        String result = "";
        for (String item : itemSet) {
            result = result + item + ",";
        }
        return result.substring(0, result.length() - 1);
    }

    // Finds frequent items based on given threshold and single items count,
    // prints them out and returns the result.
    @SuppressWarnings("unchecked")
    private static Map<Integer, Map<Set<String>, Integer>> findFreqItems(
            int threshold,
            Map<String, Integer> singleItemsCount) {
        Map<Integer, Map<Set<String>, Integer>> result = new HashMap<Integer, Map<Set<String>, Integer>>();

        // Finds out single frequent items.
        HashMap<Set<String>, Integer> singleFreqItems = new HashMap<Set<String>, Integer>();
        for (Map.Entry<String, Integer> singleItemEntry : singleItemsCount.entrySet()) {
            if (singleItemEntry.getValue() >= threshold) {
                Set<String> singleFreqItemSet = new HashSet<String>();
                singleFreqItemSet.add(singleItemEntry.getKey());
                singleFreqItems.put(singleFreqItemSet, singleItemEntry.getValue());
                System.out.println(formatItems(singleFreqItemSet));
            }
        }
        // Saves single frequent items.
        result.put(1, singleFreqItems);

        // Finds out non-single frequent items.
        int currentFreqItemLength = 2;
        while (true) {
            Map<Set<String>, Integer> lastFreqItemSet = result.get(currentFreqItemLength - 1);
            Map<Set<String>, Integer> currentFreqItemsMap = new HashMap<Set<String>, Integer>();
            for (Map.Entry<Set<String>, Integer> lastFreqItem : lastFreqItemSet.entrySet())
                for (Map.Entry<Set<String>, Integer> singleFreqItemEntry : singleFreqItems
                        .entrySet()) {
                    String singleFreqItem = singleFreqItemEntry.getKey().iterator().next();
                    findCurrentFreqItems(lastFreqItem.getKey(), singleFreqItem, threshold,
                            currentFreqItemsMap);
                }
            if (currentFreqItemsMap.isEmpty()) {
                return result;
            }
            result.put(currentFreqItemLength, currentFreqItemsMap);

            // Results output, result item sets length >= 2
            for (Map.Entry<Set<String>, Integer> entry : currentFreqItemsMap.entrySet()) {
                System.out.println(formatItems(entry.getKey()));
            }
            currentFreqItemLength++;
        }
    }

    // Finds currentFreqItemSets from singleFreqItems and lastFreqItemSets
    @SuppressWarnings("unchecked")
    private static void findCurrentFreqItems(
            Set<String> lastFreqItemSet,
            String singleFreqItem,
            int threshold,
            Map<Set<String>, Integer> currentResultMap) {
        if (lastFreqItemSet.contains(singleFreqItem)) {
            return;
        }
        HashSet<String> currentCandidate = new HashSet<String>();
        currentCandidate.addAll(lastFreqItemSet);
        currentCandidate.add(singleFreqItem);
        int counter = 0;
        for (Map.Entry<Integer, Set<String>> entry : inputData.entrySet()) {
            Set<String> inputSet = entry.getValue();
            // Whether or not input sets contain currentCandidate.
            if (inputSet.containsAll(currentCandidate)) {
                counter++;
            }
        }
        if (counter >= threshold) {
            currentResultMap.put(currentCandidate, counter);
        }
    }
}




