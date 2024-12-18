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
    JPanel controlPanel;
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

    DefaultListModel<String> songListModel;
    JList<String> songList;

    public musicPlayer() {
        initUI();
        addActionEvents();
        playThread = new Thread(runnablePlay);
        resumeThread = new Thread(runnableResume);
    }

    public void initUI() {
        songName = new JLabel("", SwingConstants.CENTER);
        songName.setFont(new Font("Times New Roman", Font.BOLD, 14));
        songName.setForeground(Color.WHITE);

        selectSong = new JButton("Select an MP3 song");
        selectSong.setBackground(Color.LIGHT_GRAY);
        selectSong.setForeground(Color.BLACK);

        controlPanel = new JPanel();
        controlPanel.setBackground(Color.DARK_GRAY);

        playIcon = new ImageIcon("C:/Users/Tamanna/Desktop/musicPlayer/com/dataflair/music-player-icons/play-button.png");
        pauseIcon = new ImageIcon("C:/Users/Tamanna/Desktop/musicPlayer/com/dataflair/music-player-icons/pause-button.png");
        resumeIcon = new ImageIcon("C:/Users/Tamanna/Desktop/musicPlayer/com/dataflair/music-player-icons/resume-button.png");
        stopIcon = new ImageIcon("C:/Users/Tamanna/Desktop/musicPlayer/com/dataflair/music-player-icons/stop-button.png");

        playButton = new JButton(playIcon);
        pauseButton = new JButton(pauseIcon);
        resumeButton = new JButton(resumeIcon);
        stopButton = new JButton(stopIcon);

        playButton.setBackground(Color.GREEN);
        pauseButton.setBackground(Color.ORANGE);
        resumeButton.setBackground(Color.CYAN);
        stopButton.setBackground(Color.RED);

        playButton.setFocusPainted(false);
        pauseButton.setFocusPainted(false);
        resumeButton.setFocusPainted(false);
        stopButton.setFocusPainted(false);

        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);
        songList.setBackground(Color.WHITE);
        songList.setForeground(Color.BLACK);

        songList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = songList.getSelectedIndex();
                    if (index != -1) {
                        filePath = songListModel.get(index);
                        myFile = new File(filePath);
                        filename = myFile.getName();
                        frame.setTitle("🎶 Playing: " + filename);

                        if (playThread.isAlive()) {
                            playThread.interrupt();
                        }
                        playThread = new Thread(runnablePlay);
                        playThread.start();
                    }
                }
            }
        });

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon("C:/Users/Tamanna/Desktop/musicPlayer/com/dataflair/music-player-icons/background.png").getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(songName, BorderLayout.NORTH);
        backgroundPanel.add(new JScrollPane(songList), BorderLayout.CENTER);
        backgroundPanel.add(selectSong, BorderLayout.SOUTH);

        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resumeButton);
        controlPanel.add(stopButton);

        frame = new JFrame("Music Player!!");
        frame.setLayout(new BorderLayout());
        frame.add(backgroundPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
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
            fileChooser.setCurrentDirectory(new File("C:/Users/Tamanna/Music"));
            fileChooser.setDialogTitle("Select an MP3 file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files", "mp3"));

            if (fileChooser.showOpenDialog(selectSong) == JFileChooser.APPROVE_OPTION) {
                myFile = fileChooser.getSelectedFile();
                filename = fileChooser.getSelectedFile().getName();
                filePath = fileChooser.getSelectedFile().getPath();
                songName.setText("File Selected : " + filename);
                songListModel.addElement(filePath);
            }
        }

        if (e.getSource().equals(playButton)) {
            if (filename != null) {
                if (playThread.isAlive()) {
                    playThread.interrupt();
                }
                playThread = new Thread(runnablePlay);
                playThread.start();
                frame.setTitle("🎶 Playing: " + filename);
            }
            else {
                JOptionPane.showMessageDialog(frame, "No song selected! Please choose a song to play.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        if (e.getSource().equals(pauseButton)) {
            if (player != null && filename != null) {
                try {
                    pauseLength = fileInputStream.available();
                    player.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                } 
            }
        }
    
        if (e.getSource() == resumeButton) {
            if (songList.getSelectedIndex() < songListModel.size() - 1) {
                int index = songList.getSelectedIndex() + 1;
                songList.setSelectedIndex(index);
                filePath = songListModel.get(index);
                myFile = new File(filePath);
                filename = myFile.getName();
                frame.setTitle("🎶 Playing: " + filename);

                if (playThread.isAlive()) {
                    playThread.interrupt();
                }
                playThread = new Thread(runnablePlay);
                playThread.start();
            }
        }
    
        if (e.getSource() == stopButton) {
            if (player != null) {
                player.close(); 
                playThread.interrupt();
                songName.setText(""); 
                frame.setTitle("Music Player");
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
