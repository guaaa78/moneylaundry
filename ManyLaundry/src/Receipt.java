/**
 * Receipt.java
 * Generates a kiosk-style text receipt for a given order.
 */
public class Receipt {

    private Order order;

    public Receipt(Order order) {
        this.order = order;
    }

    /** Returns the full receipt as a formatted string. */
    public String generate() {
        StringBuilder sb = new StringBuilder();
        String line  = "─────────────────────────────────────\n";
        String dline = "═════════════════════════════════════\n";

        sb.append(dline);
        sb.append("          🧺 MANY LAUNDRY 🧺\n");
        sb.append("      Your Trusted Clean Partner\n");
        sb.append(dline);
        sb.append("\n");

        sb.append("  ORDER ID    : ").append(order.getOrderId()).append("\n");
        sb.append("  CUSTOMER    : ").append(order.getCustomerName()).append("\n");
        sb.append("  DATE & TIME : ").append(order.getFormattedDate()).append("\n");
        sb.append("\n");
        sb.append(line);

        sb.append("  SERVICE CATEGORY\n");
        sb.append("  ").append(order.getServiceCategory()).append("\n");
        sb.append("\n");
        sb.append("  SERVICE TYPE\n");
        sb.append("  ").append(order.getServiceType()).append("\n");
        sb.append("\n");

        if (order.getWeightKg() > 0) {
            sb.append("  WEIGHT      : ").append(order.getWeightKg()).append(" kg\n");
        }

        if (order.getMachineNumber() != -1) {
            sb.append("  MACHINE NO. : #").append(order.getMachineNumber()).append("\n");
        } else {
            sb.append("  MACHINE NO. : N/A (Hand-processed)\n");
        }

        sb.append("  EST. TIME   : ").append(order.getDurationDisplay()).append("\n");
        sb.append("\n");
        sb.append(line);

        sb.append(String.format("  TOTAL COST  :  ₱ %.2f%n", order.getTotalCost()));
        sb.append("\n");
        sb.append("  STATUS      : ").append(order.getStatus()).append("\n");
        sb.append("\n");
        sb.append(dline);
        sb.append("  Thank you for choosing Many Laundry!\n");
        sb.append("     Please keep this receipt safe.\n");
        sb.append(dline);

        return sb.toString();
    }
}
