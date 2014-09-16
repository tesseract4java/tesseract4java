package de.vorb.tesseract.tools.eval.dist;

public class Aligner {
    private EditTable alignTab(String str1, String str2) {
        final int len1 = str1.length();
        final int len2 = str2.length();

        final int[][] distances = new int[2][len2 + 1];
        final EditTable table = new EditTable(len1 + 1, len2 + 1);

        // fill first row
        table.set(0, 0, EditOperation.KEEP);
        for (int y = 1; y <= len2; ++y) {
            distances[0][y] = distances[0][y - 1] + 1;
            table.set(0, y, EditOperation.INSERT);
        }
        
        // other rows
        for (int x = 1; x <= len1; ++x) {
            final char c1 = str1.charAt(len1-x);
            // distances[x%2][0] = distances[]
        }

        return table;
    }
}
