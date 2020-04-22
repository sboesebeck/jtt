package de.caluga.jtt;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KeyboardView {
    private Map<String, String[]> mapping = new HashMap<>();
    private Map<String, JLabel> labelByCoord = new HashMap<>();
    private Map<String, JLabel> labelByChar = new HashMap<>();
    private Map<String, String> coordForChar = new HashMap<>();
    private Map<String, JLabel> labelForFinger = new HashMap<>();
    private Map<String, JLabel> statsLabelForChar = new HashMap<>();

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(50);
    private Map<String, String> fingerForKey;
    private Map<String, Color> colorForFinger;

    public void highlightKeyByChar(String ch) {

        if (labelByChar.containsKey(ch)) {
            labelByChar.get(ch).setBorder(BorderFactory.createLineBorder(Color.green, 2));
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    labelByChar.get(ch).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                }
            }, 500, TimeUnit.MILLISECONDS);
        }
    }

    public void highlightErrorKeyByChar(String ch) {

        if (labelByChar.containsKey(ch)) {
            labelByChar.get(ch).setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    labelByChar.get(ch).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                }
            }, 500, TimeUnit.MILLISECONDS);
        }
    }

    public String getFingerForChar(String chr) {
        return fingerForKey.get(coordForChar.get(chr));
    }

    public String getCharsForFinger(String finger) {
        StringBuilder b = new StringBuilder();
        for (String k : fingerForKey.keySet()) {
            if (fingerForKey.get(k).equals(finger)) {
                if (labelByCoord.get(k) != null && labelByCoord.get(k).getText().length()==1)
                    b.append(labelByCoord.get(k).getText());
            }
        }
        return b.toString();
    }

    public String[] getRow(String row) {
        return mapping.get(row);
    }

    public void updateBoard(String layout) {
        InputStream in = getClass().getClassLoader().getResourceAsStream("mapping_" + layout + ".txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String l = null;

        try {
            while ((l = br.readLine()) != null) {
                var tokens = l.split(" ");
                mapping.put(tokens[0], tokens);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String rows = "edcba";
        for (int row = 0; row < 5; row++) {
            String r = "" + rows.charAt(row);
            String[] rowMappings = mapping.get(r);
            for (int col = 0; col < rowMappings.length - 1; col++) {
                if (labelByCoord.get(r + col) == null) {
                    continue;
                }
                labelByCoord.get(r + col).setText(rowMappings[col + 1]);
                labelByChar.put(rowMappings[col + 1], labelByCoord.get(r + col));
            }
        }
    }

    public void drawBoard(JFrame fr, String layout) {
        InputStream in = getClass().getClassLoader().getResourceAsStream("mapping_" + layout + ".txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String l = null;

        try {
            while ((l = br.readLine()) != null) {
                var tokens = l.split(" ");
                mapping.put(tokens[0], tokens);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int y = 330;
        int x = 25;

        int u1 = 50;
        int u125 = 63;
        int u15 = 75;
        int u2 = 100;

        int txtSize = 15;
        int keySize = 32;

        colorForFinger = new LinkedHashMap<>();
        colorForFinger.put("pinky_l", Color.pink);
        colorForFinger.put("ring_l", Color.BLUE);
        colorForFinger.put("middle_l", Color.YELLOW);
        colorForFinger.put("pointer_l", Color.RED);
        colorForFinger.put("pointer_r", Color.MAGENTA);
        colorForFinger.put("middle_r", Color.ORANGE);
        colorForFinger.put("ring_r", Color.cyan);
        colorForFinger.put("pinky_r", Color.green);


        fingerForKey = new HashMap<>();

        fingerForKey.put("b0", "pinky_l");
        fingerForKey.put("b1", "pinky_l");
        fingerForKey.put("c1", "pinky_l");
        fingerForKey.put("d1", "pinky_l");
        fingerForKey.put("e0", "pinky_l");
        fingerForKey.put("e1", "pinky_l");

        fingerForKey.put("b2", "ring_l");
        fingerForKey.put("c2", "ring_l");
        fingerForKey.put("d2", "ring_l");
        fingerForKey.put("e2", "ring_l");

        fingerForKey.put("b3", "middle_l");
        fingerForKey.put("c3", "middle_l");
        fingerForKey.put("d3", "middle_l");
        fingerForKey.put("e3", "middle_l");

        fingerForKey.put("e4", "pointer_l");
        fingerForKey.put("d4", "pointer_l");
        fingerForKey.put("c4", "pointer_l");
        fingerForKey.put("b4", "pointer_l");
        fingerForKey.put("b5", "pointer_l");
        fingerForKey.put("c5", "pointer_l");
        fingerForKey.put("d5", "pointer_l");
        fingerForKey.put("e5", "pointer_l");

        fingerForKey.put("e6", "pointer_r");
        fingerForKey.put("d6", "pointer_r");
        fingerForKey.put("c6", "pointer_r");
        fingerForKey.put("b6", "pointer_r");
        fingerForKey.put("b7", "pointer_r");
        fingerForKey.put("c7", "pointer_r");
        fingerForKey.put("d7", "pointer_r");
        fingerForKey.put("e7", "pointer_r");

        fingerForKey.put("b8", "middle_r");
        fingerForKey.put("c8", "middle_r");
        fingerForKey.put("d8", "middle_r");
        fingerForKey.put("e8", "middle_r");

        fingerForKey.put("b9", "ring_r");
        fingerForKey.put("c9", "ring_r");
        fingerForKey.put("d9", "ring_r");
        fingerForKey.put("e9", "ring_r");

        fingerForKey.put("e10", "pinky_r");
        fingerForKey.put("d10", "pinky_r");
        fingerForKey.put("c10", "pinky_r");
        fingerForKey.put("b10", "pinky_r");
        fingerForKey.put("e11", "pinky_r");
        fingerForKey.put("d11", "pinky_r");
        fingerForKey.put("c11", "pinky_r");
        fingerForKey.put("b11", "pinky_r");
        fingerForKey.put("b12", "pinky_r");
        fingerForKey.put("e12", "pinky_r");
        fingerForKey.put("d12", "pinky_r");
        fingerForKey.put("d13", "pinky_r");
        fingerForKey.put("d14", "pinky_r");


        String rows = "edcba";

        for (int row = 0; row < 4; row++) {
            JLabel key = null;

            String[] mappedRow = mapping.get("" + rows.charAt(row));
            key = new JLabel(mappedRow[1]);
            key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
            labelByChar.put(mappedRow[1], key);
            labelByCoord.put("" + rows.charAt(row) + "0", key);
            coordForChar.put(mappedRow[1], "" + rows.charAt(row) + "0");
            if (fingerForKey.containsKey(rows.charAt(row) + "0")) {
                key.setForeground(colorForFinger.get(fingerForKey.get(rows.charAt(row) + "0")));
            }
            switch (row) {
                case 0:
                    key.setBounds(x, y, u1, u1);
                    x += u1 + 5;
                    break;
                case 1:
                    key.setBounds(x, y, u125, u1);
                    x += u125 + 5;
                    break;
                case 3:
                    key.setBounds(x, y, u2, u1);
                    x += u2 + 5;
                    break;
                case 2:
                    key.setBounds(x, y, u15, u1);
                    x += u15 + 5;
                    break;
                default:
                    key = new JLabel("CTRL");
                    break;
            }
            key.setBorder(BorderFactory.createLineBorder(Color.black));
            fr.add(key);

            int idx = mappedRow.length - 1;
            for (int col = 0; col < idx - 2; col++) {

                key = new JLabel(mappedRow[col + 2]);
                key.setFont(new Font("Menlo", Font.PLAIN, keySize));
                key.setBounds(x, y, u1, u1);
                key.setBorder(BorderFactory.createLineBorder(Color.black));
                String coord = "" + rows.charAt(row) + "" + (col + 1);
                if (fingerForKey.get(coord) != null) {
                    key.setForeground(colorForFinger.get(fingerForKey.get(coord)));
                }
                labelByCoord.put(coord, key);
                coordForChar.put(mappedRow[col + 2], coord);
                labelByChar.put(mappedRow[col + 2], key);
                labelByChar.put(mapping.get("" + rows.toUpperCase().charAt(row))[col + 2], key);
                fr.add(key);
                x += u1 + 5;

            }


            key = new JLabel(mappedRow[idx]);
            key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
            labelByChar.put(mappedRow[idx], key);
            if (fingerForKey.containsKey(rows.charAt(row) + "" + idx)) {
                key.setForeground(colorForFinger.get(fingerForKey.get(rows.charAt(row) + "" + idx)));
            }
            switch (row) {
                case 0:
                    key.setBounds(x, y, u15, u1);
                    x += u15 + 5;
                    break;
                case 1:

                    key.setBounds(x, y, u125, u1);
                    x += u125 + 5;
                    break;
                case 2:
                    key.setBounds(x, y, u2 + 5, u1);
                    x += u2 + 15;
                    break;
                case 3:
                    key.setBounds(x, y, u2 + 30, u1);
                    x += u2 + 35;
                    break;
                default:
                    key = new JLabel("CTRL");
                    break;
            }

            key.setBorder(BorderFactory.createLineBorder(Color.black));
            fr.add(key);
            y += u1 + 5;
            x = 25;
        }

        int c = 0;

        JLabel key = new JLabel("CTRL");
        key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
        key.setBounds(x, y, u15, u1);
        key.setBorder(BorderFactory.createLineBorder(Color.black));
        fr.add(key);
        labelByCoord.put("a" + c, key);
        coordForChar.put("ctrl_l", "a" + c);
        c++;
        x += u15 + 5;

        key = new JLabel("OPT");
        key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
        key.setBounds(x, y, u15, u1);
        key.setBorder(BorderFactory.createLineBorder(Color.black));
        fr.add(key);
        labelByCoord.put("a" + c, key);
        coordForChar.put("opt_l", "a" + c);
        c++;
        x += u15 + 5;

        key = new JLabel("CMD");
        key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
        key.setBounds(x, y, u125, u1);
        key.setBorder(BorderFactory.createLineBorder(Color.black));
        fr.add(key);
        labelByCoord.put("a" + c, key);
        coordForChar.put("cmd_l", "a" + c);
        c++;
        x += u125 + 5;

        key = new JLabel("");
        key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
        key.setBounds(x, y, u1 * 7, u1);
        key.setBorder(BorderFactory.createLineBorder(Color.black));
        fr.add(key);
        labelByChar.put(" ", key);
        labelByCoord.put("a" + c, key);
        coordForChar.put(" ", "a" + c);
        c++;
        x += u1 * 7 + 5;

        key = new JLabel("CMD");
        key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
        key.setBounds(x, y, u125, u1);
        key.setBorder(BorderFactory.createLineBorder(Color.black));
        fr.add(key);
        labelByCoord.put("a" + c, key);
        coordForChar.put("CMD_R", "a" + c);
        c++;
        x += u125 + 5;

        key = new JLabel("OPT");
        key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
        key.setBounds(x, y, u15, u1);
        key.setBorder(BorderFactory.createLineBorder(Color.black));
        fr.add(key);
        labelByCoord.put("a" + c, key);
        coordForChar.put("opt_r", "a" + c);
        c++;
        x += u15 + 5;


        key = new JLabel("CTRL");
        key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
        key.setBounds(x, y, u1, u1);
        key.setBorder(BorderFactory.createLineBorder(Color.black));
        fr.add(key);
        labelByCoord.put("a" + c, key);
        coordForChar.put("ctrl_r", "a" + c);
        c++;
        x += u1 + 5;


        y += 85;
        x = 25;
        for (String finger : colorForFinger.keySet()) {
            key = new JLabel("0%");
            key.setFont(new Font("Menlo", Font.PLAIN, txtSize));
            key.setForeground(colorForFinger.get(finger));
            key.setBounds(x, y, 100, txtSize + 5);
            labelForFinger.put(finger, key);
            fr.add(key);

            key = new JLabel(finger);
            key.setFont(new Font("Menlo", Font.PLAIN, 10));
            key.setForeground(colorForFinger.get(finger));
            key.setBounds(x, y - 15, 100, 15);
            fr.add(key);

            x += 100;
        }

        y += 35;

        for (int row = 0; row < 4; row++) {
            x = 35;
            y += 25;
            String[] mappedRow = mapping.get("" + rows.charAt(row));
            for (int cs = 2; cs < mappedRow.length; cs++) {
                String k = mappedRow[cs];
                if (k.length() > 1) continue;
                key = new JLabel(k + ": 0%");
                key.setFont(new Font("Menlo", Font.PLAIN, 12));
                key.setForeground(Color.gray);
                key.setBounds(x, y, 80, 12 + 5);
                statsLabelForChar.put(k, key);
                x += 85;
                fr.add(key);
            }
        }

    }

    public JLabel getFingerLabel(String finger) {
        return labelForFinger.get(finger);
    }

    public void setCharAcuracy(String ch, double perc) {
        if (statsLabelForChar.containsKey(ch)) {
            statsLabelForChar.get(ch).setText(ch + ": " + perc + "%");
        }
    }

    public void resetStats() {
        for (JLabel f : labelForFinger.values()) {
            f.setText("0%");
        }
        for (String c : statsLabelForChar.keySet()) {
            statsLabelForChar.get(c).setText(c + ": 0%");
        }
    }
}
