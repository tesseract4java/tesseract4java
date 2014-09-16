package de.vorb.tesseract.tools.training;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Unicharset {
    private static final Pattern LONG_LINE =
            Pattern.compile("^(\\S+) (\\d+) (\\d+),(\\d+),(\\d+),(\\d+),(\\d+),"
                    + "(\\d+),(\\d+),(\\d+),(\\d+),(\\d+) (\\S+) (\\d+) (\\d+) "
                    + "(\\d+) (.+)$");
    private static final Pattern FIRST_LINE =
            Pattern.compile("^NULL 0 NULL 0$");
    private static final Pattern DELIM = Pattern.compile("\\s+|,");

    private List<Char> characters;

    public Unicharset(List<Char> charset) {
        this.characters = charset;
    }

    public List<Char> getCharacters() {
        return Collections.unmodifiableList(characters);
    }

    public static Unicharset readFrom(BufferedReader in) throws IOException {
        final int size = Integer.parseInt(in.readLine());
        final List<Char> charset = new ArrayList<>(size);

        String line;
        Scanner scanner;
        while ((line = in.readLine()) != null) {
            if (LONG_LINE.matcher(line).matches()) {
                scanner = new Scanner(line);
                scanner.useDelimiter(DELIM);

                final String text = scanner.next();
                final CharacterProperties props =
                        CharacterProperties.forByteCode(scanner.nextByte());
                final CharacterDimensions dims = new CharacterDimensions(
                        scanner.nextInt() /* min bottom */,
                        scanner.nextInt() /* max bottom */,
                        scanner.nextInt() /* min top */,
                        scanner.nextInt() /* max top */,
                        scanner.nextInt() /* min width */,
                        scanner.nextInt() /* max width */,
                        scanner.nextInt() /* min bearing */,
                        scanner.nextInt() /* max bearing */,
                        scanner.nextInt() /* min advance */,
                        scanner.nextInt() /* max advance */);
                final String script = scanner.next();
                final int otherCase = scanner.nextInt();
                final int direction = scanner.nextInt();
                final int mirror = scanner.nextInt();
                final String normed = scanner.nextLine().trim();
                charset.add(new Char(text, props, dims, script, otherCase,
                        direction, mirror, normed));

                scanner.close();
            } else if (FIRST_LINE.matcher(line).matches()) {
                charset.add(new Char("NULL",
                        CharacterProperties.forByteCode((byte) 0), " ", 0));
            }
        }

        in.close();

        return new Unicharset(charset);
    }
}
