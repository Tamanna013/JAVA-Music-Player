package com.dataflair.mp3.dataflair.mp3musicplayer;
import javazoom.jl.player.Player;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

class musicPlayer implements ActionListener {
    JFrame frame;
    JLabel songName;
    JButton selectSong;
    JPanel playerPanel, controlPanel;
    Icon playIcon, pauseIcon, resumeIcon, stopIcon;
    JButton playButton, pauseButton, resumeButton, stopButton;

    JFileChooser fileChooser;
    FileInputStream fileInputStream;
    BufferedInputStream bufferedInputStream;
    File myFile = null;
    String filename, filePath;
    long totalLength, pauseLength;
    Player player;
    Thread playThread, resumeThread;

    public musicPlayer() {
        initUI();
        addActionEvents();
        playThread = new Thread(runnablePlay);
        resumeThread = new Thread(runnableResume);
    }

    public void initUI() {
        songName = new JLabel("", SwingConstants.CENTER);
        selectSong = new JButton("Select an MP3 song");
        playerPanel = new JPanel();
        controlPanel = new JPanel();
        playIcon = new ImageIcon("C:\Users\Tamanna\Desktop\musicPlayer\com\dataflair\music-player-icons");
        pauseIcon = new ImageIcon("C:\Users\Tamanna\Desktop\musicPlayer\com\dataflair\music-player-icons");
        resumeIcon = new ImageIcon("C:\Users\Tamanna\Desktop\musicPlayer\com\dataflair\music-player-icons");
        stopIcon = new ImageIcon("C:\Users\Tamanna\Desktop\musicPlayer\com\dataflair\music-player-icons");

        playButton = new JButton(playIcon);
        pauseButton = new JButton(pauseIcon);
        resumeButton = new JButton(resumeIcon);
        stopButton = new JButton(stopIcon);

        playerPanel.setLayout(new GridLayout(2, 1));
        playerPanel.add(selectSong);
        playerPanel.add(songName);

        controlPanel.setLayout(new GridLayout(1, 4));
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resumeButton);
        controlPanel.add(stopButton);

        playButton.setBackground(Color.WHITE);
        pauseButton.setBackground(Color.WHITE);
        resumeButton.setBackground(Color.WHITE);
        stopButton.setBackground(Color.WHITE);

        frame = new JFrame();
        frame.setTitle("Let's play some music!!");

        frame.add(playerPanel, BorderLayout.NORTH);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setBackground(Color.white);
        frame.setSize(400, 200);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void addActionEvents() {
        selectSong.addActionListener(this);
        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        resumeButton.addActionListener(this);
        stopButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(selectSong)) {
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(" ")); // Check this...
            fileChooser.setDialogTitle("Select an MP3 file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files", "mp3"));
            if (fileChooser.showOpenDialog(selectSong) == JFileChooser.APPROVE_OPTION) {
                myFile = fileChooser.getSelectedFile();
                filename = fileChooser.getSelectedFile().getName();
                filePath = fileChooser.getSelectedFile().getPath();
                songName.setText("File Selected : " + filename);
            }
        }
      
        if (e.getSource().equals(playButton)) {
            if (filename!=null) {
                playThread.start();
                songName.setText("Now playing : " + filename);
            }
            else songName.setText("Select a song to play!");
        }
      
        if (e.getSource().equals(pauseButton)) {
            if (player!=null && filename!=null) {
                try {
                    pauseLength = fileInputStream.available();
                    player.close();
                }
                catch (IOException e2){
                  e2.printStackTrace();
                } 
            }
        }
      
        if (e.getSource().equals(resumeButton)) {
            if (filename != null) resumeThread.start();
            else songName.setText("No File was selected!");
        }
      
        if (e.getSource().equals(stopButton)) {
            if (player!=null) {
                player.close();
                songName.setText("");
            }

        }

    }

    Runnable runnablePlay = new Runnable() {
        @Override
        public void run() {
            try {
                fileInputStream = new FileInputStream(myFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                player = new Player(bufferedInputStream);
                totalLength = fileInputStream.available();
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable runnableResume = new Runnable() {
        @Override
        public void run() {
            try {
                fileInputStream = new FileInputStream(myFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                player = new Player(bufferedInputStream);
                fileInputStream.skip(totalLength - pauseLength);
                player.play();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    };

    public static void main(String[] args) {
        musicPlayer mp = new musicPlayer();
    }
}