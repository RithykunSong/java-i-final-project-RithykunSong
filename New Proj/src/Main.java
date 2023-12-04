import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;

enum Priority {
    LOW, MEDIUM, HIGH
}

class Task {
    private String description;
    private Priority priority;
    private boolean isReminderSet;
    private String dueDate;

    public Task(String description, Priority priority, boolean isReminderSet, String dueDate) {
        this.description = description;
        this.priority = priority;
        this.isReminderSet = isReminderSet;
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isReminderSet() {
        return isReminderSet;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void editTask(String description, Priority priority, boolean isReminderSet, String dueDate) {
        this.description = description;
        this.priority = priority;
        this.isReminderSet = isReminderSet;
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        String taskString = "Task: " + description + " | Priority: " + priority;
        if (isReminderSet) {
            taskString += " | Reminder Set";
        }
        if (dueDate != null && !dueDate.isEmpty()) {
            taskString += " | Due Date: " + dueDate;
        }
        return taskString;
    }
}

class ToDoList {
    private ArrayList<Task> tasks;

    public ToDoList() {
        tasks = new ArrayList<>();
    }

    public void addTask(String description, Priority priority, boolean isReminderSet, String dueDate) {
        Task task = new Task(description, priority, isReminderSet, dueDate);
        tasks.add(task);
        sortTasksByPriority();
    }

    public void markTaskComplete() {
        if (!tasks.isEmpty()) {
            tasks.remove(0);
        }
    }

    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            sortTasksByPriority();
        }
    }

    public void editTask(int index, String description, Priority priority, boolean isReminderSet, String dueDate) {
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            task.editTask(description, priority, isReminderSet, dueDate);
            sortTasksByPriority();
        }
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    private void sortTasksByPriority() {
        Collections.sort(tasks, Comparator.comparing(Task::getPriority)
                .thenComparing(Comparator.comparing(Task::getDescription))
                .thenComparing(Comparator.comparing(Task::isReminderSet, Comparator.reverseOrder())));
    }

    public void sortTasks(Comparator<Task> comparator) {
        Collections.sort(tasks, comparator);
    }

    public ArrayList<Task> filterTasks(Predicate<Task> predicate) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (predicate.test(task)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
}

public class Main extends JFrame {
    private ToDoList toDoList = new ToDoList();
    private DefaultListModel<Task> tasksListModel = new DefaultListModel<>();
    private JList<Task> tasksList = new JList<>(tasksListModel);
    private JTextField taskInput = new JTextField(15);
    private JComboBox<Priority> priorityComboBox = new JComboBox<>(Priority.values());
    private JCheckBox reminderCheckBox = new JCheckBox("Set Reminder");
    private JTextField dueDateInput = new JTextField(10);
    private String userName;

    public Main() {
        userName = JOptionPane.showInputDialog("Welcome! Please enter your name:");
        if (userName == null || userName.trim().isEmpty()) {
            userName = "User";
        }

        setTitle("To-Do List - Welcome, " + userName + "!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        inputPanel.add(new JLabel("Task: "), gbc);
        gbc.gridx++;
        inputPanel.add(taskInput, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Priority: "), gbc);
        gbc.gridx++;
        inputPanel.add(priorityComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Due Date: "), gbc);
        gbc.gridx++;
        inputPanel.add(dueDateInput, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Set Reminder: "), gbc);
        gbc.gridx++;
        inputPanel.add(reminderCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = taskInput.getText();
                Priority priority = (Priority) priorityComboBox.getSelectedItem();
                boolean isReminderSet = reminderCheckBox.isSelected();
                String dueDate = dueDateInput.getText();

                if (isValidInput(description, dueDate)) {
                    toDoList.addTask(description, priority, isReminderSet, dueDate);
                    updateTasksList();
                    clearInputFields();
                } else {
                    // Error message already handled in isValidDueDate
                }
            }
        });
        inputPanel.add(addButton, gbc);

        gbc.gridy++;
        inputPanel.add(new JButton("Mark as Complete"), gbc);

        // Delete Task button
        gbc.gridy++;
        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = tasksList.getSelectedIndex();
                if (selectedIndex != -1) {
                    toDoList.deleteTask(selectedIndex);
                    updateTasksList();
                    clearInputFields();
                } else {
                    showErrorMessage("Please select a task to delete.");
                }
            }
        });
        inputPanel.add(deleteButton, gbc);

        // Sort Tasks button
        gbc.gridy++;
        JButton sortButton = new JButton("Sort Tasks");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toDoList.sortTasks(Comparator.comparing(Task::getDescription));
                updateTasksList();
            }
        });
        inputPanel.add(sortButton, gbc);

        // Filter Tasks button
        gbc.gridy++;
        JButton filterButton = new JButton("Filter Tasks");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Task> filteredTasks = toDoList.filterTasks(task -> task.getPriority() == Priority.HIGH);
                tasksListModel.clear();
                for (Task task : filteredTasks) {
                    tasksListModel.addElement(task);
                }
            }
        });
        inputPanel.add(filterButton, gbc);

        // Exit button
        gbc.gridy++;
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(Main.this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        inputPanel.add(exitButton, gbc);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(tasksList), BorderLayout.CENTER);

        tasksList.setCellRenderer(new PriorityListCellRenderer());
        updateTasksList();
    }

    private class PriorityListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Task) {
                Task task = (Task) value;

                switch (task.getPriority()) {
                    case HIGH:
                        setBackground(Color.RED);
                        break;
                    case MEDIUM:
                        setBackground(Color.ORANGE);
                        break;
                    case LOW:
                        setBackground(Color.GREEN);
                        break;
                }
            }

            return this;
        }
    }

    private boolean isValidInput(String description, String dueDate) {
        return !description.isEmpty() && isValidDueDate(dueDate);
    }

    private boolean isValidDueDate(String dueDate) {
        String dateFormatRegex = "\\d{2}/\\d{2}/\\d{4}";

        if (!dueDate.matches(dateFormatRegex)) {
            showErrorMessage("Invalid due date format. Please use the format DD/MM/YYYY.");
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(dueDate);
        } catch (ParseException e) {
            showErrorMessage("Invalid due date. Please enter a valid date.");
            return false;
        }

        return true;
    }

    private void updateTasksList() {
        tasksListModel.clear();
        for (Task task : toDoList.getTasks()) {
            tasksListModel.addElement(task);
        }
    }

    private void clearInputFields() {
        taskInput.setText("");
        reminderCheckBox.setSelected(false);
        dueDateInput.setText("");
        priorityComboBox.setSelectedIndex(0);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(Main.this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}