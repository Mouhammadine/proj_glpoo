package musichub.main;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TableFormatter {
    private List<String[]> lines = new ArrayList<>();
    private int[] maxColumnSize;
    private int columnCount;

    public TableFormatter(String... columnNames) {
        this.lines.add(columnNames);
        this.columnCount = columnNames.length;

        this.maxColumnSize = new int[this.columnCount];

        for (int i = 0; i < this.columnCount; i++)
            this.maxColumnSize[i] = columnNames[i].length();
    }

    public void addLine(Object... objects) {
        if (objects.length != columnCount)
            throw new RuntimeException("length != columnCount");

        String[] strArray = new String[this.columnCount];

        for (int i = 0; i < columnCount; i++) {
            strArray[i] = objects[i].toString();

            if (strArray[i].length() > this.maxColumnSize[i])
                this.maxColumnSize[i] = strArray[i].length();
        }

        this.lines.add(strArray);
    }

    private void displaySepLine(PrintStream stream, char sep) {
        boolean first = true;

        for (int size : maxColumnSize) {
            if (first)
                first = false;
            else
                stream.print(sep);

            for (int i = 0; i < size + 2; i++)
                stream.print('─');
        }
        System.out.println();
    }

    private void displayDataLine(PrintStream stream, String[] data) {
        for (int i = 0; i < columnCount; i++) {
            if (i != 0)
                stream.print('│');

            stream.print(' ');
            stream.print(data[i]);

            int nbWhite = maxColumnSize[i] + 1 - data[i].length();
            for (int j = 0; j < nbWhite; j++)
                stream.print(' ');
        }
        System.out.println();
    }

    public void display(PrintStream stream) {
        displaySepLine(stream, '┬');
        displayDataLine(stream, lines.get(0));
        displaySepLine(stream, '┼');

        for (int i = 1; i < lines.size(); i++)
            displayDataLine(stream, lines.get(i));
        displaySepLine(stream, '┴');
    }
}
