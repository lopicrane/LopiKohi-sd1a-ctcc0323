package csoftware;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSoftware extends JFrame implements ActionListener {
    private static final int PRICE_SMALL = 5;
    private static final int PRICE_LARGE = 10;

    private JComboBox<String> categoryComboBox;
    private JComboBox<String> drinkComboBox;
    private JComboBox<String> sizeComboBox;
    private JTextArea orderTextArea;
    private int totalCost = 0;
    private Map<String, Integer> drinkCountMap = new HashMap<>();
    private List<String> orderList = new ArrayList<>();
    private StringBuilder orderSummary = new StringBuilder();

    public CSoftware() {
        setTitle("Lopi-Kohi Milktea Shop");
        setSize(515, 600); // Increased size for better readability
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set background image
        JLabel backgroundLabel = new JLabel(new ImageIcon("background.png"));
        setContentPane(backgroundLabel);
        setLayout(null);

        // Create labels
        JLabel categoryLabel = new JLabel("Category:");
        JLabel drinkLabel = new JLabel("Drink:");
        JLabel sizeLabel = new JLabel("Size:");
        JLabel orderLabel = new JLabel("Order Summary:");

        // Order Summary TextArea
        orderTextArea = new JTextArea(12, 30); // Reduced rows for smaller height
        orderTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderTextArea);

        // ComboBoxes
        categoryComboBox = new JComboBox<>(new String[]{"None", "Milkteas", "Iced Coffees", "Fruit Teas"});
        drinkComboBox = new JComboBox<>();
        sizeComboBox = new JComboBox<>(new String[]{"Small (16oz)", "Large (22oz)"});

        // JButtons
        JButton addButton = new JButton("Add Drink");
        JButton removeButton = new JButton("Remove Drink");
        JButton finalizeButton = new JButton("Finalize Order");
        JButton clearButton = new JButton("Clear Order");
        JButton exitButton = new JButton("Exit");

        // Clearing the Area of Buttons
        addButton.setContentAreaFilled(false);
        removeButton.setContentAreaFilled(false);
        finalizeButton.setContentAreaFilled(false);
        clearButton.setContentAreaFilled(false);
        exitButton.setContentAreaFilled(false);

        // What each button will do
        addButton.setActionCommand("Add Drink");
        removeButton.setActionCommand("Remove Drink");
        finalizeButton.setActionCommand("Finalize Order");
        clearButton.setActionCommand("Clear Order");
        exitButton.setActionCommand("Exit");

        // Action Listeners
        categoryComboBox.addActionListener(this);
        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        finalizeButton.addActionListener(this);
        clearButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Set Bounds
        categoryLabel.setBounds(20, 70, 80, 25);
        categoryComboBox.setBounds(110, 70, 150, 25);
        drinkLabel.setBounds(20, 100, 80, 25);
        drinkComboBox.setBounds(110, 100, 150, 25);
        sizeLabel.setBounds(20, 130, 80, 25);
        sizeComboBox.setBounds(110, 130, 150, 25);
        addButton.setBounds(280, 70, 150, 30);
        removeButton.setBounds(280, 110, 150, 30); 
        finalizeButton.setBounds(280, 150, 150, 30);
        clearButton.setBounds(280, 190, 150, 30); 
        exitButton.setBounds(280, 230, 150, 30); 
        orderLabel.setBounds(20, 265, 150, 25);
        scrollPane.setBounds(20, 295, 460, 200); 

        // Add components to the frame
        backgroundLabel.add(categoryLabel);
        backgroundLabel.add(categoryComboBox);
        backgroundLabel.add(drinkLabel);
        backgroundLabel.add(drinkComboBox);
        backgroundLabel.add(sizeLabel);
        backgroundLabel.add(sizeComboBox);
        backgroundLabel.add(addButton);
        backgroundLabel.add(removeButton);
        backgroundLabel.add(finalizeButton);
        backgroundLabel.add(clearButton);
        backgroundLabel.add(exitButton);
        backgroundLabel.add(orderLabel);
        backgroundLabel.add(scrollPane);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Add Drink":
                addDrinkToOrder();
                break;
            case "Remove Drink":
                removeDrinkFromOrder();
                break;
            case "Finalize Order":
                finalizeOrder();
                break;
            case "Clear Order":
                clearOrder();
                break;
            case "Exit":
                System.exit(0);
                break;
            default:
                updateDrinkOptions();
                break;
        }
    }

    private void updateDrinkOptions() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        if (selectedCategory.equals("None")) {
            drinkComboBox.setModel(new DefaultComboBoxModel<>(new String[]{})); // No drinks for "None"
        } else {
            String[] drinks = getDrinks(selectedCategory);
            drinkComboBox.setModel(new DefaultComboBoxModel<>(drinks));
        }
    }

    private void addDrinkToOrder() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        if (selectedCategory.equals("None")) {
            JOptionPane.showMessageDialog(this, "Please select a category.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String drink = (String) drinkComboBox.getSelectedItem();
        String size = (String) sizeComboBox.getSelectedItem();
        int price = size.equals("Small (16oz)") ? PRICE_SMALL : PRICE_LARGE;

        String drinkKey = drink + " (" + size + ")";
        drinkCountMap.put(drinkKey, drinkCountMap.getOrDefault(drinkKey, 0) + 1);
        orderList.add(drinkKey);

        totalCost += price;
        updateOrderSummary();
    }

    private void removeDrinkFromOrder() {
        if (orderList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No drinks to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String lastDrink = orderList.remove(orderList.size() - 1);
        String size = lastDrink.contains("Small (16oz)") ? "Small (16oz)" : "Large (22oz)";
        int price = size.equals("Small (16oz)") ? PRICE_SMALL : PRICE_LARGE;

        drinkCountMap.put(lastDrink, drinkCountMap.get(lastDrink) - 1);
        if (drinkCountMap.get(lastDrink) <= 0) {
            drinkCountMap.remove(lastDrink);
        }

        totalCost -= price;
        updateOrderSummary();
    }

    private void updateOrderSummary() {
        orderSummary.setLength(0);
        for (Map.Entry<String, Integer> entry : drinkCountMap.entrySet()) {
            orderSummary.append(entry.getKey()).append(" ").append(entry.getValue()).append("x - $")
                    .append((entry.getKey().contains("Small (16oz)") ? PRICE_SMALL : PRICE_LARGE) * entry.getValue())
                    .append("\n");
        }
        orderSummary.append("\nTotal Cost: $").append(totalCost);
        orderTextArea.setText(orderSummary.toString());
    }

    private void finalizeOrder() {
        orderSummary.append("\nOrder Finalized.");
        orderTextArea.setText(orderSummary.toString());
    }

    private void clearOrder() {
        orderSummary.setLength(0);
        drinkCountMap.clear();
        orderList.clear();
        totalCost = 0;
        orderTextArea.setText("");
    }

    private String[] getDrinks(String category) {
        switch (category) {
            case "Milkteas":
                return new String[]{"Taro", "Okinawa", "Matcha", "Mocha", "Cookies & Cream"};
            case "Iced Coffees":
                return new String[]{"Cappuccino", "Caramel", "Hazelnut", "Vanilla", "Matcha"};
            case "Fruit Teas":
                return new String[]{"Blueberry", "Lychee", "Lemon", "Peach", "Mango"};
            default:
                return new String[]{};
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CSoftware::new);
    }
}
