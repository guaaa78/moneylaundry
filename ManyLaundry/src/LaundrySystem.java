import java.util.ArrayList;
import java.util.List;

/**
 * LaundrySystem.java
 * Central business-logic controller — manages orders and machines.
 */
public class LaundrySystem {

    private static final int MACHINE_COUNT = 5;

    private ArrayList<Order>   orders;
    private ArrayList<Machine> machines;
    private int                orderCounter;

    public LaundrySystem() {
        orders       = new ArrayList<>();
        machines     = new ArrayList<>();
        orderCounter = 1000;

        for (int i = 1; i <= MACHINE_COUNT; i++) {
            machines.add(new Machine(i));
        }
    }

    // ─────────────────────────────────────────────────────────
    //  ORDER CREATION
    // ─────────────────────────────────────────────────────────

    /**
     * Creates and registers a new order.
     * Returns the created Order on success, throws RuntimeException on failure.
     */
    public Order createOrder(String customerName, String serviceType, double weightKg)
            throws RuntimeException {

        // 1. Validate customer name
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new RuntimeException("Customer name cannot be empty.");
        }

        // 2. Validate service type
        if (serviceType == null || serviceType.trim().isEmpty()) {
            throw new RuntimeException("Please select a service type.");
        }

        // 3. Determine category
        String category = Order.getCategory(serviceType);
        if (category == null) {
            throw new RuntimeException("Invalid service type selected.");
        }

        // 4. Weight validation (skip for special items)
        if (!Order.CAT_SPECIAL_ITEM.equals(category)) {
            String weightError = Order.validateWeight(serviceType, weightKg);
            if (weightError != null) throw new RuntimeException(weightError);
        }

        // 5. Machine assignment (skip for special items)
        int assignedMachine = -1;
        if (!Order.CAT_SPECIAL_ITEM.equals(category)) {
            Machine m = findAvailableMachine();
            if (m == null) {
                throw new RuntimeException(
                    "All 5 washing machines are currently occupied.\n" +
                    "Please wait for a machine to become available.");
            }
            assignedMachine = m.getMachineId();
        }

        // 6. Build order
        orderCounter++;
        String orderId         = "ML-" + orderCounter;
        double price           = Order.calculatePrice(serviceType);
        int    duration        = Order.calculateDuration(serviceType);

        Order order = new Order(orderId, customerName.trim(), category,
                                serviceType, weightKg, assignedMachine,
                                duration, price);

        // 7. Assign machine
        if (assignedMachine != -1) {
            getMachine(assignedMachine).assign(orderId);
        }

        orders.add(order);
        return order;
    }

    // ─────────────────────────────────────────────────────────
    //  ORDER LIFECYCLE
    // ─────────────────────────────────────────────────────────

    public void completeOrder(String orderId) throws RuntimeException {
        Order order = findOrder(orderId);
        if (order == null)            throw new RuntimeException("Order not found: " + orderId);
        if (order.isCancelled())      throw new RuntimeException("Cannot complete a CANCELLED order.");
        if (order.isCompleted())      throw new RuntimeException("Order is already COMPLETED.");

        order.setStatus(Order.STATUS_COMPLETED);
        releaseMachineFor(orderId);
    }

    public void cancelOrder(String orderId) throws RuntimeException {
        Order order = findOrder(orderId);
        if (order == null)        throw new RuntimeException("Order not found: " + orderId);
        if (order.isCompleted())  throw new RuntimeException("Cannot cancel a COMPLETED order.");
        if (order.isCancelled())  throw new RuntimeException("Order is already CANCELLED.");

        order.setStatus(Order.STATUS_CANCELLED);
        releaseMachineFor(orderId);
    }

    // ─────────────────────────────────────────────────────────
    //  QUERY
    // ─────────────────────────────────────────────────────────

    public Order findOrder(String orderId) {
        for (Order o : orders) {
            if (o.getOrderId().equalsIgnoreCase(orderId.trim())) return o;
        }
        return null;
    }

    public List<Order> getAllOrders()     { return orders; }
    public List<Machine> getAllMachines() { return machines; }

    public int getPendingCount() {
        int c = 0;
        for (Order o : orders) if (o.isPending()) c++;
        return c;
    }

    public int getCompletedCount() {
        int c = 0;
        for (Order o : orders) if (o.isCompleted()) c++;
        return c;
    }

    public int getCancelledCount() {
        int c = 0;
        for (Order o : orders) if (o.isCancelled()) c++;
        return c;
    }

    public int getAvailableMachineCount() {
        int c = 0;
        for (Machine m : machines) if (m.isAvailable()) c++;
        return c;
    }

    // ─────────────────────────────────────────────────────────
    //  INTERNAL HELPERS
    // ─────────────────────────────────────────────────────────

    private Machine findAvailableMachine() {
        for (Machine m : machines) {
            if (m.isAvailable()) return m;
        }
        return null;
    }

    private Machine getMachine(int id) {
        for (Machine m : machines) {
            if (m.getMachineId() == id) return m;
        }
        return null;
    }

    private void releaseMachineFor(String orderId) {
        for (Machine m : machines) {
            if (orderId.equals(m.getCurrentOrderId())) {
                m.release();
                return;
            }
        }
    }
}
