package de.caluga.jtt;

import de.caluga.jtt.data.Lesson;
import de.caluga.jtt.data.LineTyped;
import de.caluga.jtt.data.TypedKey;
import de.caluga.morphium.Morphium;
import de.caluga.morphium.MorphiumConfig;
import de.caluga.morphium.driver.inmem.InMemoryDriver;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class App {
    private Logger log = new Logger(App.class);

    String currentLineToType = "";
    JLabel currentLineLabel;

    List<String> linesOfExcercise = new ArrayList<>();
    Map<Integer, Double> acuracyByLine = new HashMap<>();

    private int typed = 0;
    private int typedWords = 0;
    private int typedWordsLine=0;

    private int errors = 0;
    private int currentLine = 0;
    private int errorsInCurrentLine = 0;
    private int typedInCurrentLine = 0;


    private String currentLayout = "qwertz";

    Map<String,AtomicInteger> typedByFinger=new HashMap<>();
    Map<String,AtomicInteger> errorsByFinger=new HashMap<>();

    Map<String,AtomicInteger> typedByCharacter=new HashMap<>();
    Map<String,AtomicInteger> errorsByCharacter=new HashMap<>();


    StringBuilder typedText = new StringBuilder();
    private JLabel charsCountLabel;
    private JLabel errorsCountLabel;
    private JLabel typedTextLabel;
    private JLabel speedCharLabel;
    private JLabel speedWordLabel;
    private JLabel acuracyPercent;
    private JLabel durationLabel;

    long start = 0;
    private JCheckBox dynamic;
    private JLabel lastAcuracyLine;
    private JLabel currentAcuracyLine;
    private KeyboardView keyboard;
    private ArrayList<String> availableLayouts;
    private JComboBox<String> lessonCBX;
    private Map<String, String> availableLessons = new HashMap<>();
    private JLabel nextLineLabel;
    private JLabel typedWordsCountLabel;
    private long pauseTime=0;
    private String currentLesson;

    private Morphium morphium;
    private JProgressBar progressBar;
    private int backSpaceTyped=0;

    private Lesson currentLessonStats;


    App(String[] args) {

        MorphiumConfig cfg=new MorphiumConfig();
        if (args.length>0 && args[0].equals("useMongo")){
            cfg.addHostToSeed(args[1]);
        } else {
            cfg.setDriverClass(InMemoryDriver.class.getName());
            cfg.addHostToSeed("inmem");
        }
        cfg.setDatabase("jtt");
        morphium=new Morphium(cfg);

        new Thread() {
            public void run() {
                while (true) {
                    if (durationLabel!=null) {
                        if (start != 0) {
                            long dur = System.currentTimeMillis() - start;
                            dur = dur / 1000;
                            int mins = (int) dur / 60;
                            int secs = (int) dur - mins * 60;
                            String s = "" + secs;
                            if (secs < 10) s = "0" + secs;
                            durationLabel.setText(mins + ":" + s);


                            int speedCPM = (int) ((double) typed / ((double) (System.currentTimeMillis() - start) / 1000.0 / 60.0));
                            speedCharLabel.setText("" + speedCPM + "cpm");

                            int speedWpm = (int) ((double) (typedWords + typedWordsLine) / ((double) (System.currentTimeMillis() - start) / 1000.0 / 60.0));
                            speedWordLabel.setText(speedWpm + "wpm");

                            for (String finger : typedByFinger.keySet()) {
                                if (errorsByFinger.get(finger) != null) {
                                    int errorsByF = 0;
                                    int typedByF = typedByFinger.get(finger).get();
                                    errorsByF = errorsByFinger.get(finger).get();
                                    double perc = ((int) ((1.0 - ((double) errorsByF) / ((double) typedByF)) * 1000.0)) / 10.0;
                                    if (keyboard.getFingerLabel(finger) != null) {
                                        keyboard.getFingerLabel(finger).setText("" + perc + "%");
                                    }
                                } else {
                                    if (keyboard.getFingerLabel(finger) != null) {
                                        keyboard.getFingerLabel(finger).setText("100%");
                                    }
                                }
                            }

                            for (String ch : typedByCharacter.keySet()) {
                                if (errorsByCharacter.containsKey(ch)) {
                                    int errorsByC = 0;
                                    int typedByC = typedByCharacter.get(ch).get();
                                    errorsByC = errorsByCharacter.get(ch).get();
                                    double perc = ((int) ((1.0 - ((double) errorsByC) / ((double) typedByC)) * 1000.0)) / 10.0;
                                    keyboard.setCharAcuracy(ch, perc);
                                } else {
                                    keyboard.setCharAcuracy(ch, 100);
                                }
                            }


                        } else {
                            durationLabel.setText("paused");
                        }
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                    }

                }
            }
        }.start();
    }

    private void readLesson(String name) {
        currentLesson =name;
        if (name.equals(currentLayout + " homerow random")) {
            List<String> keys = new ArrayList<>();
            keys.addAll(Arrays.asList(keyboard.getRow("c")));
            keys.remove(0); //name of row
            keys.remove(0); //CAPS
            keys.remove(keys.size()-1); //last key on homerow is enter

            linesOfExcercise.clear();
            generateLesson(keys);
            restart();

        } else if (name.equals( "all letters random chars + capitals")) {
            String chars=".,!?abcdefhijklmnopqrstuvwxyz";
            chars=chars+chars.toUpperCase();
            List<String>keys=new ArrayList<>(Arrays.asList(chars.split("")));
            //umlauts?
            if (Arrays.asList(keyboard.getRow("c")).contains("ö") ||Arrays.asList(keyboard.getRow("b")).contains("ö") ||Arrays.asList(keyboard.getRow("e")).contains("ö")){
                keys.add("ö");
                keys.add("Ö");
                keys.add("ä");
                keys.add("Ä");
                keys.add("Ü");
                keys.add("ü");
                keys.add("ß");
            }
            linesOfExcercise.clear();
            generateLesson(keys);
            restart();
        } else if (name.equals( "all letters random chars")) {
            String chars=",.!?abcdefhijklmnopqrstuvwxyz";
            List<String>keys=new ArrayList<>(Arrays.asList(chars.split("")));
            //umlauts?
            if (Arrays.asList(keyboard.getRow("c")).contains("ö") ||Arrays.asList(keyboard.getRow("b")).contains("ö") ||Arrays.asList(keyboard.getRow("e")).contains("ö")){
                keys.add("ö");
                keys.add("ä");
                keys.add("ü");
                keys.add("ß");
            }
            linesOfExcercise.clear();
            generateLesson(keys);
            restart();
        } else if (name.startsWith(currentLayout + " homerow+up random")) {
            List<String> keys = new ArrayList<>();
            keys.addAll(Arrays.asList(keyboard.getRow("c")));
            keys.remove(0); //name of row
            keys.remove(0); //shift
            keys.remove(keys.size()-1); //last key on homerow is enter
            List<String> d1 = new ArrayList<>(Arrays.asList(keyboard.getRow("d")));
            d1.remove(0);
            d1.remove(0); //shift
            d1.remove(d1.size()-1); //shift
            keys.addAll(d1);
            linesOfExcercise.clear();
            generateLesson(keys);
            restart();
        } else if (name.startsWith("random words")) {
            String suffix="en";
            if (name.contains("(german)")) {
                suffix="de";
            }

            InputStream in = getClass().getClassLoader().getResourceAsStream("words_"+suffix+".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String l = null;
            List<String> words=new ArrayList<>();
            try {
                while ((l = br.readLine()) != null) {
                    words.add(l.toLowerCase());
                }
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            linesOfExcercise.clear();

            StringBuilder line=new StringBuilder();
            for (int i=0;i<20;i++){
                line.setLength(0);
                while(line.length()<52){
                    line.append(words.get((int)(words.size()*Math.random()*Math.random())));
                    line.append(" ");
                }
                line.setLength(line.length()-1);
                if (line.length()>52){
                    line.setLength(line.lastIndexOf(" "));
                }
                linesOfExcercise.add(line.toString());
            }
            restart();
        } else if (name.endsWith("finger") && name.startsWith("random chars for")) {
            linesOfExcercise.clear();
            String finger=name.substring(17);
            finger=finger.substring(0,finger.lastIndexOf(" ")).trim();
            for(int i=0;i<20;i++){
                String l=getNextRandomLine(finger);
                linesOfExcercise.add(l);
            }
            restart();
        } else if (name.startsWith("random 500")) {
            String suffix="en";
            if (name.contains("(german)")) {
                suffix="de";
            }

            InputStream in = getClass().getClassLoader().getResourceAsStream("500_"+suffix+".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String l = null;
            List<String> words=new ArrayList<>();
            try {
                while ((l = br.readLine()) != null) {
                    words.add(l.toLowerCase());
                }
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            linesOfExcercise.clear();

            StringBuilder line=new StringBuilder();
            for (int i=0;i<20;i++){
                line.setLength(0);
                while(line.length()<52){
                    line.append(words.get((int)(words.size()*Math.random())));
                    line.append(" ");
                }
                line.setLength(line.length()-1);
                if (line.length()>52){
                    line.setLength(line.lastIndexOf(" "));
                }
                linesOfExcercise.add(line.toString());
            }
            restart();
        } else {
            InputStream in = getClass().getClassLoader().getResourceAsStream(availableLessons.get(name));
            if (in == null) {
                log.info("Did not find file " + name);
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String l = null;
            currentLine = 0;

            linesOfExcercise.clear();
            try {
                while ((l = br.readLine()) != null) {
                    processLine(l);
                }
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        restart();
    }

    private void processLine(String l) {
        if (l.length() > 52) {
            int idx = 0;
            while (l.indexOf(" ", idx + 1) != -1 && l.indexOf(" ", idx + 1) <= 52) {
                idx = l.indexOf(" ", idx + 1);
            }
            linesOfExcercise.add(l.substring(0, idx));
            processLine(l.substring(idx + 1));
        } else {
            linesOfExcercise.add(l);
        }
    }

    public void show() {
        JFrame fr = new JFrame("JTT");

        fr.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (start == 0) {
                    start = System.currentTimeMillis()-pauseTime;
                    pauseTime=0;
                    newLessonIfnull();
                }
                if (e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (typedText.length() > 0)
                        typedText.setLength(typedText.length() - 1);
                    typedTextLabel.setText(typedText.toString());
                    updateCurrentLine();
                    backSpaceTyped++;
                    return;
                } else if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
                    pauseTime=System.currentTimeMillis()-start;
                    start=0;
                    morphium.store(currentLessonStats);
                    return;
                } else if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                    handleEnter();
                    return;
                }
                App.this.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        fr.setLayout(null);
        currentLineLabel = new JLabel(currentLineToType);
        currentLineLabel.setFont(new Font("Menlo", Font.PLAIN, 30));
        currentLineLabel.setBounds(50, 75, 1150, 50);
        fr.add(currentLineLabel);

        nextLineLabel = new JLabel();
        nextLineLabel.setFont(new Font("Menlo", Font.PLAIN, 24));
        nextLineLabel.setBounds(50, 115, 1100, 50);
        nextLineLabel.setForeground(Color.gray);
        fr.add(nextLineLabel);

        charsCountLabel = new JLabel("Typed: 0");
        charsCountLabel.setBounds(100, 20, 100, 30);
        charsCountLabel.setFont(new Font("Menlo", Font.PLAIN, 12));

        typedWordsCountLabel = new JLabel("Words: 0");
        typedWordsCountLabel.setBounds(185, 20, 100, 30);
        typedWordsCountLabel.setFont(new Font("Menlo", Font.PLAIN, 12));
        fr.add(typedWordsCountLabel);

        errorsCountLabel = new JLabel("Errors: 0");
        errorsCountLabel.setBounds(280, 20, 100, 30);
        errorsCountLabel.setFont(new Font("Menlo", Font.PLAIN, 12));

        acuracyPercent = new JLabel("Acuracy: 0.0%");
        acuracyPercent.setBounds(420, 20, 120, 30);
        acuracyPercent.setFont(new Font("Menlo", Font.PLAIN, 12));

        speedCharLabel = new JLabel("0 cpm");
        speedCharLabel.setBounds(550, 20, 100, 30);
        speedCharLabel.setFont(new Font("Menlo", Font.PLAIN, 12));

        speedWordLabel = new JLabel("0 wpm");
        speedWordLabel.setBounds(620, 20, 100, 30);
        speedWordLabel.setFont(new Font("Menlo", Font.PLAIN, 12));


        currentAcuracyLine = new JLabel("current: 0%");
        currentAcuracyLine.setBounds(970, 210, 100, 15);
        currentAcuracyLine.setFont(new Font("Menlo", Font.PLAIN, 9));

        lastAcuracyLine = new JLabel("last:  0%");
        lastAcuracyLine.setBounds(970, 190, 100, 15);
        lastAcuracyLine.setFont(new Font("Menlo", Font.PLAIN, 9));

        typedTextLabel = new JLabel("");
        typedTextLabel.setFont(new Font("Menlo", Font.PLAIN, 25));
        typedTextLabel.setForeground(Color.GRAY);
        typedTextLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        typedTextLabel.setBounds(50, 190, 900, 40);


        durationLabel = new JLabel("0:00");
        durationLabel.setFont(new Font("Menlo", Font.PLAIN, 15));
        durationLabel.setForeground(Color.GRAY);
        durationLabel.setBounds(800, 20, 100, 30);

        progressBar = new JProgressBar(0,100);
        progressBar.setToolTipText("Progress in lesson");
        progressBar.setValue(0);
        progressBar.setBounds(880,20,150,30);


        dynamic = new JCheckBox("dynamic lesson");
        dynamic.setBounds(400, 250, 200, 30);
        dynamic.setFocusable(false);

        keyboard = new KeyboardView();
        keyboard.drawBoard(fr, currentLayout);

        lessonCBX = new JComboBox<>();
        updateLessons();

        lessonCBX.setBounds(520, 250, 250, 30);
        lessonCBX.setFocusable(false);
        fr.add(lessonCBX);
        lessonCBX.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !e.getItem().toString().equals("-----------------")) {
                readLesson(e.getItem().toString());
            } else  if (e.getStateChange() == ItemEvent.SELECTED && e.getItem().toString().equals("-----------------")){
                ((JComboBox)e.getSource()).setSelectedItem(currentLesson);
            }

        });


        JButton btn = new JButton("restart");
        btn.addActionListener(e -> {
            restart();


        });
        btn.setBounds(25, 250, 100, 35);
        btn.setFocusable(false);
        fr.add(btn);
        fr.add(progressBar);


//        btn = new JButton("Store");
//        btn.setBounds(180, 250, 150, 35);
//        btn.setFocusable(false);
//        fr.add(btn);
//        btn.addActionListener(e -> {
//            JFileChooser fc = new JFileChooser();
//            fc.setApproveButtonText("store");
//            fc.setFileFilter(new FileNameExtensionFilter("Java Typing Tutor files", ".jtt"));
//            fc.showDialog(fr, "store");
//            if (fc.getSelectedFile() != null) {
//                log.info("Storing to " + fc.getSelectedFile().getAbsolutePath());
//                Preferences prefs = Preferences.userNodeForPackage(this.getClass());
//                prefs.put("file", fc.getSelectedFile().getAbsolutePath());
//                try {
//                    prefs.sync();
//                } catch (BackingStoreException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });


        InputStream in = getClass().getClassLoader().getResourceAsStream("layouts.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String l = null;
        availableLayouts = new ArrayList<>();
        try {
            while ((l = br.readLine()) != null) {
                availableLayouts.add(l);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel lb = new JLabel("Keyboard Layout:");
        lb.setFont(new Font("Menlo", Font.PLAIN, 15));
        lb.setForeground(Color.GRAY);
        lb.setBounds(850, 310, 200, 30);
        JComboBox<String> layoutCBX = new JComboBox<>(availableLayouts.toArray(new String[]{}));
        layoutCBX.setBounds(830, 340, 250, 30);
        layoutCBX.setFocusable(false);
        layoutCBX.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                log.info(e.getItem().toString());
                currentLayout = e.getItem().toString();
                keyboard.updateBoard(currentLayout);
                updateLessons();
            }

        });


        fr.add(layoutCBX);
        fr.add(lb);
        fr.add(currentAcuracyLine);
        fr.add(lastAcuracyLine);
        fr.add(durationLabel);
        fr.add(dynamic);

        fr.add(acuracyPercent);
        fr.add(typedTextLabel);
        fr.add(speedCharLabel);
        fr.add(speedWordLabel);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fr.add(errorsCountLabel);
        fr.add(charsCountLabel);

        fr.setSize(1200, 900);
        fr.setVisible(true);
        fr.setResizable(false);
        restart();
        updateCurrentLine();

    }

    private void newLessonIfnull() {
        if (currentLessonStats==null) {
            currentLessonStats = new Lesson();
            currentLessonStats.setLessonName(currentLesson);
            currentLessonStats.setTimestamp(System.currentTimeMillis());
            currentLessonStats.setLinesOfLesson(new ArrayList<>(linesOfExcercise));
            currentLessonStats.getTypedLines().add(new LineTyped());
            morphium.store(currentLessonStats);
        }
    }

    private void restart() {
        currentLessonStats=null;
        start = 0;
        pauseTime=0;
        errorsInCurrentLine = 0;
        errors = 0;
        typedText.setLength(0);
        updateCurrentLine();
        typed = 0;
        typedWords = 0;
        typedWordsLine = 0;
        durationLabel.setText("0:00");
//            readLesson(currentLayout, currentLesson);
        errorsCountLabel.setText("Errors: 0");
        speedWordLabel.setText("0 wpm");
        charsCountLabel.setText("0");
        speedCharLabel.setText("0 cpm");
        lastAcuracyLine.setText("last: 0%");
        currentAcuracyLine.setText("current: 0%");
        acuracyPercent.setForeground(Color.black);
        acuracyPercent.setText("0 %");
        typedTextLabel.setText("");
        currentLine=0;

        currentLineToType=linesOfExcercise.get(0);
        currentLineLabel.setText(currentLineToType);
        nextLineLabel.setText(linesOfExcercise.get(1));
        keyboard.resetStats();
        progressBar.setMaximum(linesOfExcercise.size());
        progressBar.setValue(0);
    }

    private void generateLesson(List<String> keys) {
        double wordMax = 3;
        int lines = 10;
        int wordsPerLine = 12;

        for (int l = 0; l < lines; l++) {
            StringBuilder line = new StringBuilder();
            for (int w = 0; w < wordsPerLine; w++) {
                int len = (int) (Math.random() * wordMax) + 2;
                String word = "";
                for (int c = 0; c < len; c++) {
                    word += keys.get((int) ((keys.size()) * Math.random()));
                }
                line.append(word);
                line.append(" ");
            }
            line.setLength(line.length() - 1);
            linesOfExcercise.add(line.toString());
        }
    }


    private String getNextRandomLine(String finger){
        String chars=keyboard.getCharsForFinger(finger+"_l");
        int maxLength=40;
        StringBuilder b=new StringBuilder();
        chars=" "+chars+" ";
        chars+=keyboard.getCharsForFinger(finger+"_r");
        chars+=" ";
        chars=chars.replaceAll("[0-9]","");
        for (int i=0;i<maxLength;i++){
            int v = (int)(chars.length() * Math.random());
            b.append(chars.substring(v,v+1));
        }
        String ret=b.toString();
        return ret.replaceAll("  *"," ");
    }

    private void updateLessons() {
        lessonCBX.removeAllItems();
        lessonCBX.addItem(currentLayout + " homerow random");
        lessonCBX.addItem(currentLayout + " homerow+up random");
        lessonCBX.addItem("all letters random chars");
        lessonCBX.addItem("all letters random chars + capitals");
        lessonCBX.addItem("-----------------");
        lessonCBX.addItem("random 500 (english)");
        lessonCBX.addItem("random 500 (german)");
        lessonCBX.addItem("random words (german)");
        lessonCBX.addItem("random words (english)");
        lessonCBX.addItem("-----------------");
        lessonCBX.addItem("random chars for pointer finger");
        lessonCBX.addItem("random chars for middle finger");
        lessonCBX.addItem("random chars for ring finger");
        lessonCBX.addItem("random chars for pinky finger");
        lessonCBX.addItem("-----------------");

        InputStream in = getClass().getClassLoader().getResourceAsStream("lessons_" + currentLayout + ".txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        availableLessons.clear();//fileName for Name

        String l = null;
        try {
            while ((l = br.readLine()) != null) {
                String[] spl = l.split(";");
                String fileName = spl[0];
                String descr = spl[1];
                availableLessons.put(descr, currentLayout+"_"+fileName+".txt");
                lessonCBX.addItem(descr);
            }
            br.close();
            lessonCBX.addItem("-----------------");

            //all lessons
            in = getClass().getClassLoader().getResourceAsStream("lessons_all.txt");
            br = new BufferedReader(new InputStreamReader(in));
            while ((l = br.readLine()) != null) {
                String[] spl = l.split(";");
                String fileName = spl[0];
                String descr = spl[1];
                availableLessons.put(descr, "all_"+fileName+".txt");
                lessonCBX.addItem(descr);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lessonCBX.setSelectedIndex(0);
        readLesson(lessonCBX.getSelectedItem().toString());
        progressBar.setMinimum(0);
        progressBar.setMaximum(linesOfExcercise.size());
        progressBar.setValue(0);
    }

    private void keyTyped(KeyEvent e) {
        TypedKey tk=new TypedKey();
        tk.setCorrect(true);
        tk.setCharacter(""+e.getKeyChar());
        tk.setAtDuration(System.currentTimeMillis()-start);

        currentLessonStats.getTypedLines().get(currentLine).getKeyStrokes().add(tk);

        typedText.append(e.getKeyChar());

        String finger=keyboard.getFingerForChar(""+e.getKeyChar());
        typedByFinger.putIfAbsent(finger,new AtomicInteger(0));
        typedByFinger.get(finger).incrementAndGet();
        tk.setFinger(finger);
        if (currentLineToType.length()>=typedText.length()) {

            char c = currentLineToType.charAt(typedText.length() - 1);
            typedByCharacter.putIfAbsent("" + c, new AtomicInteger(0));
            typedByCharacter.get("" + c).incrementAndGet();
            if (c != e.getKeyChar()) {
                errorsByFinger.putIfAbsent(finger, new AtomicInteger(0));
                errorsByFinger.get(finger).incrementAndGet();
                errorsByCharacter.putIfAbsent("" + c, new AtomicInteger(0));
                errorsByCharacter.get("" + c).incrementAndGet();
                keyboard.highlightErrorKeyByChar("" + e.getKeyChar());
                tk.setCorrect(false);
            } else {
                keyboard.highlightKeyByChar("" + e.getKeyChar());
            }
        } else {
            keyboard.highlightErrorKeyByChar("" + e.getKeyChar());
            tk.setCorrect(false);
        }
        typedTextLabel.setText(typedText.toString());
        updateCurrentLine();
        typed++;
        typedInCurrentLine++;
        if (typedText.length() > currentLineToType.length()) {
            errors++;
            errorsInCurrentLine++;
        } else if (typedText.length() > 0) {
            char c = currentLineToType.charAt(typedText.length() - 1);
            if (c != e.getKeyChar()) {
                errors++;
                errorsInCurrentLine++;
            }
        }
        typedWordsLine=typedText.toString().split(" ").length;
        typedWordsCountLabel.setText("Words: "+(typedWords+typedWordsLine));
        errorsCountLabel.setText("Errors: " + errors);

        charsCountLabel.setText("Typed: " + typed);

        double acuracy = 1 - (double) errors / (double) typed;
        acuracy = acuracy * 1000;
        double ac = ((int) acuracy) / 10.0;
        if (ac < 0) {
            ac = 0;
        }
        acuracyPercent.setText("Acuracy: " + ac + "%");
        if (ac < 50) {
            acuracyPercent.setForeground(Color.RED);
        } else if (ac < 75) {
            acuracyPercent.setForeground(Color.YELLOW);
        } else if (ac > 75 && ac < 90) {
            acuracyPercent.setForeground(Color.black);
        } else {
            acuracyPercent.setForeground(Color.GREEN);
        }

        double acuracyForLine = 1;
        if (typedInCurrentLine != 0) acuracyForLine = 1.0 - (double) errorsInCurrentLine / (double) typedInCurrentLine;
        acuracyForLine = ((int) (acuracyForLine * 1000) / 10.0);
        currentAcuracyLine.setText("current: " + acuracyForLine + "%");

    }

    private void updateCurrentLine() {
        StringBuilder label = new StringBuilder("<html><body>");
        for (int i = 0; i < typedText.length() && i < currentLineToType.length(); i++) {
            String color = "red";
            if (typedText.charAt(i) == currentLineToType.charAt(i)) {
                color = "green";
            }
            label.append("<font color=");
            label.append(color);
            label.append(">");
            if (currentLineToType.charAt(i) == ' ' && (color.equals("red") || color.equals("green"))) {
                label.append("_");
            } else {
                label.append(currentLineToType.charAt(i));
            }
            label.append("</font>");
        }
        try {
            if (typedText.length() < currentLineToType.length()) {
                int idx=0;
                if (typedText.length()>0){
                    idx=typedText.length();
                }
                label.append("<font color=yellow>");
                String ch = currentLineToType.substring(idx, idx + 1);
                if (ch.equals(" ")) ch="_";
                label.append(ch);
                idx++;
                label.append("</font>");
                if (idx<currentLineToType.length()) {
                    label.append("<font color=black>");
                    label.append(currentLineToType.substring(idx));
                    label.append("</font>");
                }
                label.append("<font color=gray>");
                label.append("↵");
                label.append("</font>");
            } else if (typedText.length() == currentLineToType.length()) {
                label.append("<font color=gray>");
                label.append("↵");
                label.append("</font>");
            } else {
                label.append("<font color=red>");
                label.append("↵");
                label.append(typedText.substring(currentLineToType.length()));
                label.append("</font>");
            }
            label.append("</body></html>");
            currentLineLabel.setText(label.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEnter() {

        typedWords += typedText.toString().split("\\s+").length;
        double speedWpm = ((double) (typedWords+typedWordsLine) / ((double) (System.currentTimeMillis() - start) / 1000.0 / 60.0));
        double speedCpm = ((double) (typed+typedInCurrentLine) / ((double) (System.currentTimeMillis() - start) / 1000.0 / 60.0));
        typedWordsLine=0;
//        speedWordLabel.setText(speedWpm + "wpm");
        log.info("handle enter");

        if (typedText.length() < currentLineToType.length()) {
            errors += currentLineToType.length() - typedText.length();
            errorsCountLabel.setText("Errors: " + errors);
        }
        double acuracyForLine = 1.0 - (double) errorsInCurrentLine / (double) currentLineToType.length();
        acuracyByLine.put(currentLine, acuracyForLine);
        newLessonIfnull();
        currentLessonStats.setBackspaces(backSpaceTyped);
        currentLessonStats.setErrorsTotal(errors);
        currentLessonStats.setTypedTotal(typed);
        currentLessonStats.setCpm(speedCpm);
        currentLessonStats.setWpm(speedWpm);
        currentLessonStats.setTypedWords(typedWords);
        currentLessonStats.setDuration(System.currentTimeMillis()-start);

        currentLessonStats.getTypedLines().add(new LineTyped());

        currentLessonStats.getLastLineTyped().setAtDuration(start-System.currentTimeMillis());
        currentLessonStats.getLastLineTyped().setTypedInLine(typedInCurrentLine);
        currentLessonStats.getLastLineTyped().setTypedString(typedText.toString());
        currentLessonStats.getLastLineTyped().setLineInLesson(currentLine);
        currentLessonStats.getLastLineTyped().setLineToType(currentLineToType);
        currentLessonStats.getLastLineTyped().setErrorsInLine(errorsInCurrentLine);
        morphium.store(currentLessonStats);

        typedText.setLength(0);
        typedTextLabel.setText("");
        if (dynamic.isSelected()) {
            int line = currentLine;

            while (line < linesOfExcercise.size() && acuracyByLine.get(line) != null) {
                line++;
            }
            if (line >= linesOfExcercise.size()) {
                progressBar.setMaximum(100);
                double acuracy = 1 - (double) errors / (double) typed;
                acuracy=acuracy*100.0;
                progressBar.setValue((int)acuracy);
                progressBar.setToolTipText("Acuracy");
                progressBar.setBackground(Color.green);

                //all upcoming lines are already typed
                //find the one with lowest acuracy
                double ac = 100;

                for (int i = 0; i < linesOfExcercise.size(); i++) {
                    if (acuracyByLine.get(i) > ac) {
                        ac = acuracyByLine.get(i);
                        line = i;
                    }
                }
                if (line == currentLine) {
                    currentLine++;
                } else {
                    currentLine = line;
                }
            } else {
                currentLine = line;
                progressBar.setValue(currentLine);
            }
        } else {
            currentLine++;
            progressBar.setValue(currentLine);
        }
        if (currentLine >= linesOfExcercise.size() || currentLine < 0) {
            currentLine = 0;
        }
        currentLineToType = linesOfExcercise.get(currentLine).trim();
        if (dynamic.isSelected() && acuracyByLine.get(currentLine+1)==null){
            nextLineLabel.setText("");
        } else {
            if (currentLine < linesOfExcercise.size() - 1) {
                nextLineLabel.setText(linesOfExcercise.get(currentLine + 1));
            } else {
                nextLineLabel.setText(linesOfExcercise.get(0));
            }
        }
        errorsInCurrentLine = 0;
        typedInCurrentLine = 0;
        typed++;
        updateCurrentLine();
        currentAcuracyLine.setText("current: 0.0%");

        if (acuracyByLine.get(currentLine) != null) {
            lastAcuracyLine.setText("last:  " + ((int) (acuracyByLine.get(currentLine) * 1000) / 10.0));
        } else {
            lastAcuracyLine.setText("last: n.v.");
        }

    }

    public static void main(final String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new App(args).show();
            }
        });
    }


}
