import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.List;

public class DataStreamsGUI extends JFrame {
    private final JTextField searchField;
    private final JTextArea originalTextArea;
    private final JTextArea filteredTextArea;
    private final JButton searchButton;
    private Path selectedFilePath;

    public DataStreamsGUI() {
        setTitle("Data Streams Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create top panel for controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadButton = new JButton("Load File");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.setEnabled(false); // Disabled until file is loaded
        JButton quitButton = new JButton("Quit");

        topPanel.add(loadButton);
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(quitButton);

        // Create center panel for text areas
        JPanel textPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Original file text area (left)
        originalTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        JScrollPane originalScrollPane = new JScrollPane(originalTextArea);
        originalScrollPane.setBorder(BorderFactory.createTitledBorder("Original File"));

        // Filtered results text area (right)
        filteredTextArea = new JTextArea();
        filteredTextArea.setEditable(false);
        JScrollPane filteredScrollPane = new JScrollPane(filteredTextArea);
        filteredScrollPane.setBorder(BorderFactory.createTitledBorder("Filtered Results"));

        textPanel.add(originalScrollPane);
        textPanel.add(filteredScrollPane);

        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(textPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Load file button action
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files", "txt"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFilePath = fileChooser.getSelectedFile().toPath();
                try (Stream<String> lines = Files.lines(selectedFilePath)) {
                    originalTextArea.setText("");
                    lines.forEach(line -> originalTextArea.append(line + "\n"));
                    searchButton.setEnabled(true);
                    filteredTextArea.setText("");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error reading file: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    selectedFilePath = null;
                    searchButton.setEnabled(false);
                }
            }
        });

        // Search button action
        searchButton.addActionListener(e -> {
            String searchString = searchField.getText().trim();
            if (searchString.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a search string!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Stream<String> lines = Files.lines(selectedFilePath)) {
                List<String> matchingLines = lines
                        .filter(line -> line.contains(searchString))
                        .toList();

                filteredTextArea.setText("");
                if (matchingLines.isEmpty()) {
                    filteredTextArea.append("No matches found for: " + searchString);
                } else {
                    matchingLines.forEach(line -> filteredTextArea.append(line + "\n"));
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Quit button action
        quitButton.addActionListener(e -> System.exit(0));
    }
}