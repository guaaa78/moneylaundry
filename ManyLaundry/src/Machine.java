/**
 * Machine.java
 * Represents a single washing machine in the laundry shop.
 */
public class Machine {

    // Machine states
    public static final String STATE_AVAILABLE = "AVAILABLE";
    public static final String STATE_OCCUPIED  = "OCCUPIED";

    private int    machineId;
    private String state;
    private String currentOrderId; // null when available

    public Machine(int machineId) {
        this.machineId      = machineId;
        this.state          = STATE_AVAILABLE;
        this.currentOrderId = null;
    }

    // ── Getters ────────────────────────────────────────────
    public int    getMachineId()      { return machineId; }
    public String getState()          { return state; }
    public String getCurrentOrderId() { return currentOrderId; }

    // ── Business helpers ───────────────────────────────────
    public boolean isAvailable() {
        return STATE_AVAILABLE.equals(state);
    }

    public void assign(String orderId) {
        this.state          = STATE_OCCUPIED;
        this.currentOrderId = orderId;
    }

    public void release() {
        this.state          = STATE_AVAILABLE;
        this.currentOrderId = null;
    }

    @Override
    public String toString() {
        return "Machine #" + machineId + " [" + state + "]"
                + (currentOrderId != null ? " → Order " + currentOrderId : "");
    }
}
