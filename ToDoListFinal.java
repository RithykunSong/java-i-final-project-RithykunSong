import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// Enum for task priority
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

    @Override
    public String toString() {
        String taskString = "Priority " + priority + " | Task: " + description;
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

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    private void sortTasksByPriority() {
        Collections.sort(tasks, Comparator.comparing(Task::getPriority)
                .thenComparing(Comparator.comparing(Task::getDescription))
                .thenComparing(Comparator.comparing(Task::isReminderSet, Comparator.reverseOrder())));
    }
}

public class Main extends JFrame {

    private ToDoList toDoList = new ToDoList();
    private DefaultListModel<String> tasksListModel = new DefaultListModel<>();
    private JList<String> tasksList = new JList<>(tasksListModel);
    private JTextField taskInput = new JTextField(15);
    private JComboBox<Priority> priorityComboBox = new JComboBox<>(Priority.values());
    private JCheckBox reminderCheckBox = new JCheckBox("Set Reminder");
    private JTextField dueDateInput = new JTextField(10);

    public Main() {
        setTitle("To-Do List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        inputPanel.add(new JLabel("Task: "));
        inputPanel.add(taskInput);

        inputPanel.add(new JLabel("Priority: "));
        inputPanel.add(priorityComboBox);

        inputPanel.add(reminderCheckBox);
        inputPanel.add(new JLabel("Due Date: "));
        inputPanel.add(dueDateInput);

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = taskInput.getText();
                Priority priority = (Priority) priorityComboBox.getSelectedItem();
                boolean isReminderSet = reminderCheckBox.isSelected();
                String dueDate = dueDateInput.getText();

                if (!description.isEmpty()) {
                    toDoList.addTask(description, priority, isReminderSet, dueDate);
                    updateTasksList();
                    taskInput.setText("");
                    reminderCheckBox.setSelected(false);
                    dueDateInput.setText("");
                }
            }
        });

        JButton completeButton = new JButton("Mark as Complete");
        completeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toDoList.markTaskComplete();
                updateTasksList();
            }
        });

        inputPanel.add(addButton);
        inputPanel.add(completeButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(tasksList), BorderLayout.CENTER);

        updateTasksList();
    }

    private void updateTasksList() {
        tasksListModel.clear();
        for (Task task : toDoList.getTasks()) {
            tasksListModel.addElement(task.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}
