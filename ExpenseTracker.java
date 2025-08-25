import java.util.*; 
import java.io.*; 
import java.time.LocalDate;

public class ExpenseTracker
{
    static class Expense
    {
        private double amount; 
        private String category; 
        private String description; 
        private LocalDate date; 

        public Expense(double amount, String category, String description, LocalDate date)
        {
            this.amount = amount; 
            this.category = category; 
            this.description = description; 
            this.date = date; 
        }

        public double getAmount() { return amount; }
        public String getCategory() { return category; }
        public LocalDate getDate() { return date; }
        public String getDescription() { return description; }

        @Override
        public String toString() 
        {
            return String.format("%-10s | $%-8.2f | %-15s | %s", date, amount, category, description);
        }

        public String toCSV()
        {
            return amount + "," + category + "," + description + "," + date;
        }

         public static Expense fromCSV(String line) 
         {
            String[] parts = line.split(",");
            return new Expense
            (Double.parseDouble(parts[0]), parts[1], parts[2], LocalDate.parse(parts[3]));
        }
    }

    private static final List<Expense> expenses = new ArrayList<>();
    private static final String FILE_NAME = "expenses.csv";

    public static void main(String[] args)
    {
        loadExpenses(); 
        Scanner scanner = new Scanner(System.in); 

        while (true) 
        {
            System.out.println("\n=== Expense Tracker ===");
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. View Summary");
            System.out.println("4. Save & Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) 
            {
                case 1 -> addExpense(scanner);
                case 2 -> viewExpenses();
                case 3 -> viewSummary();
                case 4 -> 
                {
                    saveExpenses();
                    System.out.println("Expenses saved. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }
    private static void addExpense(Scanner scanner) 
    {
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter category (Food, Rent, Entertainment, etc.): ");
        String category = scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        LocalDate date = LocalDate.now();

        Expense expense = new Expense(amount, category, description, date);
        expenses.add(expense);
        System.out.println("Expense added successfully!");
    }

    private static void viewExpenses() 
    {
        if (expenses.isEmpty()) 
        {
            System.out.println("No expenses recorded.");
            return;
        }
        System.out.println("\nDate       | Amount   | Category        | Description");
        System.out.println("---------------------------------------------------------");
        for (Expense e : expenses) 
        {
            System.out.println(e);
        }
    }

    private static void viewSummary() 
    {
        if (expenses.isEmpty()) 
        {
            System.out.println("No expenses recorded.");
            return;
        }
        double total = 0;
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense e : expenses) 
        {
            total += e.getAmount();
            categoryTotals.put(e.getCategory(),
                    categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }

        System.out.println("\n--- Expense Summary ---");
        System.out.printf("Total spent: $%.2f\n", total);

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) 
        {
            System.out.printf("%-15s : $%.2f\n", entry.getKey(), entry.getValue());
        }
    }

    private static void saveExpenses() 
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) 
        {
            for (Expense e : expenses) 
            {
                writer.println(e.toCSV());
            }
        } catch (IOException e) 
        {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    private static void loadExpenses() 
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                expenses.add(Expense.fromCSV(line));
            }
        } catch (IOException e) 
        {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }
}