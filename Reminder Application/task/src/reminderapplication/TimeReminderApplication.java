package reminderapplication;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.util.Timer;
import java.util.TimerTask;

public class TimeReminderApplication extends JFrame {

    private final JButton addReminderButton;
    private final JButton editReminderButton;
    private final JButton deleteReminderButton;
    private final DefaultListModel<String> reminderListModel;
    private final JList<String> reminderList;
    private Timer timer;

    public TimeReminderApplication() {
        setTitle("Reminder Application");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        reminderListModel = new DefaultListModel<>();
        reminderList = new JList<>(reminderListModel);
        reminderList.setName("List of Reminders");

        JScrollPane scrollPane = new JScrollPane(reminderList);
        scrollPane.setName("Scroll Pane");
        scrollPane.setBounds(10, 10, 480, 100);
        add(scrollPane);

        addReminderButton = new JButton("Add Reminder");
        addReminderButton.setName("AddReminder");
        addReminderButton.setBounds(50, 220, 135, 30);
        add(addReminderButton);

        editReminderButton = new JButton("Edit Reminder");
        editReminderButton.setName("EditReminder");
        editReminderButton.setBounds(190, 220, 135, 30);
        add(editReminderButton);

        deleteReminderButton = new JButton("Delete Reminder");
        deleteReminderButton.setName("DeleteReminder");
        deleteReminderButton.setBounds(330, 220, 135, 30);
        add(deleteReminderButton);

        addReminderButton.addActionListener(this::showSetReminderWindow);
        editReminderButton.addActionListener(this::editReminder);
        deleteReminderButton.addActionListener(this::deleteReminder);

        setVisible(true);
    }

    private void showSetReminderWindow(ActionEvent event) {
        addReminderButton.setEnabled(false);
        editReminderButton.setEnabled(false);
        deleteReminderButton.setEnabled(false);

        JFrame setReminderFrame = new JFrame("Set Reminder");
        setReminderFrame.setName("Set Reminder");
        setReminderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setReminderFrame.setSize(320, 220);
        setReminderFrame.setLayout(null);
        setReminderFrame.setResizable(false);

        JTextField reminderTextField = new JTextField();
        reminderTextField.setName("Field");
        reminderTextField.setBounds(10, 35, 280, 25);
        setReminderFrame.add(reminderTextField);

        JLabel reminderTextLabel = new JLabel("Reminder Text");
        reminderTextLabel.setName("Reminder Text Label");
        reminderTextLabel.setBounds(10, 10, 280, 25);
        setReminderFrame.add(reminderTextLabel);

        JComboBox<Integer> setDelayComboBox = new JComboBox<>(new Integer[]{30, 25, 15, 5});
        setDelayComboBox.setName("set Delay");
        setDelayComboBox.setBounds(10, 95, 130, 25);
        setReminderFrame.add(setDelayComboBox);

        JLabel setDelayLabel = new JLabel("Set Delay");
        setDelayLabel.setName("Set Delay Label");
        setDelayLabel.setBounds(10, 70, 130, 25);
        setReminderFrame.add(setDelayLabel);

        JLabel delaysLabel = new JLabel("Delays");
        delaysLabel.setName("Delays Label");
        delaysLabel.setBounds(10, 120, 130, 25);
        setReminderFrame.add(delaysLabel);

        JComboBox<Integer> setPeriodComboBox = new JComboBox<>(new Integer[]{0, 5, 10, 20});
        setPeriodComboBox.setName("set Period");
        setPeriodComboBox.setBounds(160, 95, 130, 25);
        setReminderFrame.add(setPeriodComboBox);

        JLabel setPeriodLabel = new JLabel("Set Period");
        setPeriodLabel.setName("Set Repeat Period Label");
        setPeriodLabel.setBounds(160, 70, 130, 25);
        setReminderFrame.add(setPeriodLabel);

        JLabel periodLabel = new JLabel("0");
        periodLabel.setName("Period label");
        periodLabel.setBounds(160, 120, 130, 25);
        setReminderFrame.add(periodLabel);

        JButton okButton = new JButton("OK");
        okButton.setName("OK");
        okButton.setBounds(45, 150, 80, 25);
        okButton.addActionListener(e -> {
            String reminderText = reminderTextField.getText();
            int delay = (Integer) setDelayComboBox.getSelectedItem();
            int period = (Integer) setPeriodComboBox.getSelectedItem();

            String reminderDetails = String.format("Reminder Text: %s; Delay: %d; Period: %d;", reminderText.isEmpty() ? "" : reminderText, delay, period);
            reminderListModel.addElement(reminderDetails);

            // Re-schedule the timer for the new reminder
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer("ReminderTimer", true);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        reminderTextField.setText(reminderText); // Set the text of the reminder
                        okButton.setEnabled(false); // Disable the OK button
                        setReminderFrame.setVisible(true); // Show the reminder window

                        // Set up a timer to dispose of the window after 5 seconds
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SwingUtilities.invokeLater(() -> {
                                    setReminderFrame.setVisible(false); // Hide the reminder window
                                    okButton.setEnabled(true); // Re-enable the OK button
                                    addReminderButton.setEnabled(true);
                                    editReminderButton.setEnabled(true);
                                    deleteReminderButton.setEnabled(true);
                                });
                            }
                        }, 5000);
                    });
                    // If period is 0, cancel further executions
                    if (period == 0) {
                        cancel();
                    }
                }
            };

            // Schedule the task for repeated execution only if period > 0
            if (period > 0) {
                timer.scheduleAtFixedRate(task, delay * 1000L, period * 1000L);
            } else {
                timer.schedule(task, delay * 1000L);
            }

            // Dispose of the Set Reminder window after scheduling the task
            setReminderFrame.dispose();
            addReminderButton.setEnabled(true);
            editReminderButton.setEnabled(true);
            deleteReminderButton.setEnabled(true);
        });

        setReminderFrame.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setName("Cancel");
        cancelButton.setBounds(175, 150, 80, 25);
        cancelButton.addActionListener(e -> {
            setReminderFrame.dispose();
            addReminderButton.setEnabled(true);
            editReminderButton.setEnabled(true);
            deleteReminderButton.setEnabled(true);
        });
        setReminderFrame.add(cancelButton);

        setReminderFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                addReminderButton.setEnabled(true);
                editReminderButton.setEnabled(true);
                deleteReminderButton.setEnabled(true);
            }
        });

        setReminderFrame.setLocationRelativeTo(this);
        setReminderFrame.setVisible(true);
    }

    private int extractNumberAfterKeyword(String reminderDetails, String keyword) {
        int keywordLocation = reminderDetails.indexOf(keyword);
        if (keywordLocation == -1) {
            // Keyword not found
            return -1;
        }

        // Start extracting the number immediately after the keyword
        int start = keywordLocation + keyword.length();

        // Assume that the number ends before the next semicolon
        int end = reminderDetails.indexOf(';', start);

        if (end == -1) {
            // Semicolon not found, might be the end of the string
            end = reminderDetails.length();
        }

        // Extract the substring and parse it to an integer
        String numberStr = reminderDetails.substring(start, end).trim();
        try {
            return Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            // Handle the case where parsing is not possible due to invalid format
            e.printStackTrace();
            return -1;
        }
    }

    private void editReminder(ActionEvent e) {
        int selectedIndex = reminderList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedReminder = reminderListModel.getElementAt(selectedIndex);
            // Assuming the reminder details are separated by "; "
            String[] details = selectedReminder.split("; ");
            if (details.length >= 3) {
                // Parse details - this may need to be adjusted based on your reminder string format
                int delay = extractNumberAfterKeyword(selectedReminder, "Delay: ");
                int period = extractNumberAfterKeyword(selectedReminder, "Period: ");
                if (delay != -1 && period != -1) {
                    // Get the reminder text
                    int textStart = selectedReminder.indexOf("Reminder Text: ") + "Reminder Text: ".length();
                    int textEnd = selectedReminder.indexOf("; Delay: ");
                    String reminderText = selectedReminder.substring(textStart, textEnd);

                    showSetReminderWindowForEdit(reminderText, delay, period, selectedIndex);
                } else {
                    // Handle error if delay or period could not be extracted
                    JOptionPane.showMessageDialog(this, "Error parsing reminder details.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteReminder(ActionEvent e) {
        int selectedIndex = reminderList.getSelectedIndex();
        if (selectedIndex != -1) {
            reminderListModel.remove(selectedIndex);
        }
    }

    private void showSetReminderWindowForEdit(String text, int delay, int period, int selectedIndex) {
        addReminderButton.setEnabled(false);
        editReminderButton.setEnabled(false);
        deleteReminderButton.setEnabled(false);

        JFrame setReminderFrame = new JFrame("Set Reminder");
        setReminderFrame.setName("Set Reminder");
        setReminderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setReminderFrame.setSize(320, 220);
        setReminderFrame.setLayout(null);
        setReminderFrame.setResizable(false);

        JTextField reminderTextField = new JTextField(text);
        reminderTextField.setName("Field");
        reminderTextField.setBounds(10, 35, 280, 25);
        setReminderFrame.add(reminderTextField);

        JLabel reminderTextLabel = new JLabel("Reminder Text");
        reminderTextLabel.setName("Reminder Text Label");
        reminderTextLabel.setBounds(10, 10, 280, 25);
        setReminderFrame.add(reminderTextLabel);

        JComboBox<Integer> setDelayComboBox = new JComboBox<>(new Integer[]{30, 25, 15, 5});
        setDelayComboBox.setName("set Delay");
        setDelayComboBox.setBounds(10, 95, 130, 25);
        setReminderFrame.add(setDelayComboBox);

        JLabel setDelayLabel = new JLabel("Set Delay");
        setDelayLabel.setName("Set Delay Label");
        setDelayLabel.setBounds(10, 70, 130, 25);
        setReminderFrame.add(setDelayLabel);

        JLabel delaysLabel = new JLabel("Delays");
        delaysLabel.setName("Delays Label");
        delaysLabel.setBounds(10, 120, 130, 25);
        setReminderFrame.add(delaysLabel);

        JComboBox<Integer> setPeriodComboBox = new JComboBox<>(new Integer[]{0, 5, 10, 20});
        setPeriodComboBox.setName("set Period");
        setPeriodComboBox.setBounds(160, 95, 130, 25);
        setReminderFrame.add(setPeriodComboBox);

        JLabel setPeriodLabel = new JLabel("Set Period");
        setPeriodLabel.setName("Set Repeat Period Label");
        setPeriodLabel.setBounds(160, 70, 130, 25);
        setReminderFrame.add(setPeriodLabel);

        JLabel periodLabel = new JLabel("0");
        periodLabel.setName("Period label");
        periodLabel.setBounds(160, 120, 130, 25);
        setReminderFrame.add(periodLabel);

        JButton okButton = new JButton("OK");
        okButton.setName("OK");
        okButton.setBounds(45, 150, 80, 25);
        okButton.addActionListener(e -> {
            String updatedText = reminderTextField.getText();
            int updatedDelay = (Integer) setDelayComboBox.getSelectedItem();
            int updatedPeriod = (Integer) setPeriodComboBox.getSelectedItem();

            String updatedReminder = String.format("Reminder Text: %s; Delay: %d; Period: %d;", updatedText.isEmpty() ? "" : updatedText, updatedDelay, updatedPeriod);
            reminderListModel.set(selectedIndex, updatedReminder);

            // Re-schedule the timer for the new reminder
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer("ReminderTimer", true);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        reminderTextField.setText(updatedText); // Set the text of the reminder
                        okButton.setEnabled(false); // Disable the OK button
                        setReminderFrame.setVisible(true); // Show the reminder window

                        // Set up a timer to dispose of the window after 5 seconds
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SwingUtilities.invokeLater(() -> {
                                    setReminderFrame.setVisible(false); // Hide the reminder window
                                    okButton.setEnabled(true); // Re-enable the OK button
                                    addReminderButton.setEnabled(true);
                                    editReminderButton.setEnabled(true);
                                    deleteReminderButton.setEnabled(true);
                                });
                            }
                        }, 5000);
                    });
                    // If period is 0, cancel further executions
                    if (period == 0) {
                        cancel();
                    }
                }
            };

            // Schedule the task for repeated execution only if period > 0
            if (period > 0) {
                timer.scheduleAtFixedRate(task, delay * 1000L, period * 1000L);
            } else {
                timer.schedule(task, delay * 1000L);
            }

            // Dispose of the Set Reminder window after scheduling the task
            setReminderFrame.dispose();
            addReminderButton.setEnabled(true);
            editReminderButton.setEnabled(true);
            deleteReminderButton.setEnabled(true);
        });
        setReminderFrame.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(175, 150, 80, 25);
        cancelButton.addActionListener(e -> setReminderFrame.dispose());
        setReminderFrame.add(cancelButton);

        setReminderFrame.setLocationRelativeTo(null);
        setReminderFrame.setVisible(true);
    }

}
