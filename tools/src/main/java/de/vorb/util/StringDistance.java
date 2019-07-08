package de.vorb.util;

/**
 * Distance metrics for strings.
 *
 * @author Paul Vorbach
 */
public class StringDistance {
    private static StringDistance instance = null;

    private StringDistance() {
    }

    /**
     * @return singleton instance of this class.
     */
    public static StringDistance getInstance() {
        if (instance == null)
            instance = new StringDistance();

        return instance;
    }

    /**
     * Calculates the Levenshtein distance of two <code>String</code>s.
     * <p>
     * This implementation has a complexity of O(<i>nm</i>), where <i>n</i> is the
     * length of <code>a</code> and <i>m</i> <code>b</code>.
     *
     * @param a first string
     * @param b second string
     * @return Levenshtein distance between <code>a</code> and <code>b</code>
     */
    public int levenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];

        for (int j = 0; j < costs.length; j++)
            costs[j] = j;

        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;

            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }

        return costs[b.length()];
    }
}
