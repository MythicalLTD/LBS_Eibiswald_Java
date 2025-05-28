package systems.mythical.myjavaproject;

/**
 * -----
 * @setting(allow_ai_documenation, true)
 * @setting(logic, "systems.mythical.ai.documenator.gpt4")
 * -----
 */
import java.util.Scanner;

/**
 * A program that allows the user to interact with a custom doubly linked list.
 * 
 * @author Cassian Gherman
 * @version 1.0
 * @since 2025-05-28
 */
public class ListProgram {
    private static final CustomLinkedList list = new CustomLinkedList();
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * The main method that runs the program.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (scanner) {
            boolean running = true;
            while (running) {
                printMenu();
                int choice = getMenuChoice();
                
                switch (choice) {
                    case 1 -> {
                        try {
                            System.out.print("Enter value to add at start: ");
                            list.addFirst(scanner.nextLine());
                        } catch (Exception e) {
                            System.out.println("Invalid input!");
                        }
                    }
                    case 2 -> {
                        try {
                            System.out.print("Enter value to add at end: ");
                            list.addLast(scanner.nextLine());
                        } catch (Exception e) {
                            System.out.println("Invalid input!");
                        }
                    }
                    case 3 -> {
                        System.out.print("Enter index: ");
                        int index = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter value: ");
                        String value = scanner.nextLine();
                        try {
                            list.addIndex(index, value);
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Invalid index!");
                        }
                    }
                    case 4 -> {
                        list.deleteFirst();
                        System.out.println("First element deleted");
                    }
                    case 5 -> {
                        list.deleteLast();
                        System.out.println("Last element deleted");
                    }
                    case 6 -> {
                        System.out.print("Enter index to delete: ");
                        try {
                            list.deleteIndex(Integer.parseInt(scanner.nextLine()));
                            System.out.println("Element deleted");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Invalid index!");
                        }
                    }
                    case 7 -> list.printAll();
                    case 8 -> {
                        list.next();
                        System.out.println("Moved cursor to next element");
                        list.printAll();
                    }
                    case 9 -> {
                        list.prev();
                        System.out.println("Moved cursor to previous element");
                        list.printAll();
                    }
                    case 10 -> {
                        list.deleteCurrent();
                        System.out.println("Deleted current element");
                    }
                    case 11 -> {
                        list.sort();
                        System.out.println("List sorted");
                    }
                    case 12 -> running = false;
                    default -> System.out.println("Invalid choice!");
                }
            }
        }
        System.out.println("Program terminated");
    }

    /**
     * Prints the menu of the program.
     */
    private static void printMenu() {
        System.out.println("\n=== Linked List Menu ===");
        System.out.println("1. Add First");
        System.out.println("2. Add Last");
        System.out.println("3. Add at Index");
        System.out.println("4. Delete First");
        System.out.println("5. Delete Last");
        System.out.println("6. Delete at Index");
        System.out.println("7. Print All");
        System.out.println("8. Move Cursor Next");
        System.out.println("9. Move Cursor Previous");
        System.out.println("10. Delete Current Element");
        System.out.println("11. Sort List");
        System.out.println("12. Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * Gets the user's choice from the menu.
     * 
     * @return the user's choice
     */
    private static int getMenuChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets the list.
     * 
     * @return the list
     */
    public static CustomLinkedList getList() {
        return list;
    }
}