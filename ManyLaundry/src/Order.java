import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Order.java
 * Represents a single laundry order in the system.
 */
public class Order {

    // ── Service categories ─────────────────────────────────
    public static final String CAT_DIY_WASH     = "DIY Self-Service – Wash";
    public static final String CAT_DIY_DRY      = "DIY Self-Service – Dry";
    public static final String CAT_FULL_SERVICE = "Full Service (Wash + Dry + Fold)";
    public static final String CAT_SPECIAL_ITEM = "Specialized Item";

    // ── Service sub-types ──────────────────────────────────
    // DIY Wash
    public static final String WASH_LITE    = "Wash – Lite (4 kg, 22 min)";
    public static final String WASH_REGULAR = "Wash – Regular (8 kg, 36 min)";
    // DIY Dry
    public static final String DRY_LITE     = "Dry – Lite (4 kg, 25 min)";
    public static final String DRY_REGULAR  = "Dry – Regular (8 kg, 40 min)";
    // Full service
    public static final String FULL_LITE    = "Full Service – Lite (<4 kg)";
    public static final String FULL_REGULAR = "Full Service – Regular (5–8 kg)";
    // Specialized items
    public static final String SPEC_COMFORTER_SMALL  = "Comforter / Blanket – Small";
    public static final String SPEC_COMFORTER_MEDIUM = "Comforter / Blanket – Medium";
    public static final String SPEC_COMFORTER_LARGE  = "Comforter / Blanket – Large";
    public static final String SPEC_BARONG_BLAZER    = "Barong / Blazer";
    public static final String SPEC_GOWN             = "Gown";
    public static final String SPEC_SNEAKERS         = "Sneakers";

    // ── Order statuses ─────────────────────────────────────
    public static final String STATUS_PENDING   = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // ── Fields ─────────────────────────────────────────────
    private String        orderId;
    private String        customerName;
    private String        serviceCategory;
    private String        serviceType;
    private double        weightKg;        // 0 for special items
    private int           machineNumber;   // -1 for special items
    private int           estimatedMinutes;
    private double        totalCost;
    private String        status;
    private LocalDateTime createdAt;

    public Order(String orderId, String customerName,
                 String serviceCategory, String serviceType,
                 double weightKg, int machineNumber,
                 int estimatedMinutes, double totalCost) {
        this.orderId           = orderId;
        this.customerName      = customerName;
        this.serviceCategory   = serviceCategory;
        this.serviceType       = serviceType;
        this.weightKg          = weightKg;
        this.machineNumber     = machineNumber;
        this.estimatedMinutes  = estimatedMinutes;
        this.totalCost         = totalCost;
        this.status            = STATUS_PENDING;
        this.createdAt         = LocalDateTime.now();
    }

    // ── Getters ────────────────────────────────────────────
    public String        getOrderId()          { return orderId; }
    public String        getCustomerName()      { return customerName; }
    public String        getServiceCategory()   { return serviceCategory; }
    public String        getServiceType()       { return serviceType; }
    public double        getWeightKg()          { return weightKg; }
    public int           getMachineNumber()     { return machineNumber; }
    public int           getEstimatedMinutes()  { return estimatedMinutes; }
    public double        getTotalCost()         { return totalCost; }
    public String        getStatus()            { return status; }
    public LocalDateTime getCreatedAt()         { return createdAt; }

    // ── Setters ────────────────────────────────────────────
    public void setStatus(String status)        { this.status = status; }

    // ── Helpers ────────────────────────────────────────────
    public String getFormattedDate() {
        return createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy  hh:mm a"));
    }

    public boolean requiresMachine() {
        return !CAT_SPECIAL_ITEM.equals(serviceCategory);
    }

    public boolean isPending()   { return STATUS_PENDING.equals(status); }
    public boolean isCompleted() { return STATUS_COMPLETED.equals(status); }
    public boolean isCancelled() { return STATUS_CANCELLED.equals(status); }

    // ── Pricing lookup (static) ────────────────────────────
    public static double calculatePrice(String serviceType) {
        switch (serviceType) {
            case WASH_LITE:             return 55;
            case WASH_REGULAR:          return 75;
            case DRY_LITE:              return 45;
            case DRY_REGULAR:           return 65;
            case FULL_LITE:             return 130;
            case FULL_REGULAR:          return 170;
            case SPEC_COMFORTER_SMALL:  return 175;   // midpoint 100–250
            case SPEC_COMFORTER_MEDIUM: return 375;   // midpoint 250–500
            case SPEC_COMFORTER_LARGE:  return 600;   // midpoint 500–700
            case SPEC_BARONG_BLAZER:    return 400;   // midpoint 300–500
            case SPEC_GOWN:             return 1350;  // midpoint 700–2000
            case SPEC_SNEAKERS:         return 475;   // midpoint 350–600
            default:                    return 0;
        }
    }

    // ── Duration lookup (static) ──────────────────────────
    public static int calculateDuration(String serviceType) {
        switch (serviceType) {
            case WASH_LITE:             return 22;
            case WASH_REGULAR:          return 36;
            case DRY_LITE:              return 25;
            case DRY_REGULAR:           return 40;
            case FULL_LITE:
            case FULL_REGULAR:          return 60;
            case SPEC_COMFORTER_SMALL:
            case SPEC_COMFORTER_MEDIUM: return 120;   // 2 hrs
            case SPEC_COMFORTER_LARGE:  return 1440;  // 1 day
            case SPEC_BARONG_BLAZER:
            case SPEC_SNEAKERS:         return 180;   // 3 hrs
            case SPEC_GOWN:             return 2880;  // 2 days
            default:                    return 60;
        }
    }

    // ── Weight validation ──────────────────────────────────
    /**
     * Returns null if weight is valid, or an error message string.
     */
    public static String validateWeight(String serviceType, double kg) {
        if (CAT_SPECIAL_ITEM.equals(getCategory(serviceType))) return null; // no weight needed

        if (kg <= 0) return "Weight must be greater than 0 kg.";

        if (WASH_LITE.equals(serviceType) || DRY_LITE.equals(serviceType) || FULL_LITE.equals(serviceType)) {
            if (kg > 4) return "Lite service maximum is 4 kg. Got: " + kg + " kg.";
        } else if (WASH_REGULAR.equals(serviceType) || DRY_REGULAR.equals(serviceType)) {
            if (kg < 4.1) return "Regular service minimum is above 4 kg. Got: " + kg + " kg.";
            if (kg > 8)   return "Regular service maximum is 8 kg. Got: " + kg + " kg.";
        } else if (FULL_REGULAR.equals(serviceType)) {
            if (kg < 5)  return "Full Regular service minimum is 5 kg. Got: " + kg + " kg.";
            if (kg > 8)  return "Full Regular service maximum is 8 kg. Got: " + kg + " kg.";
        }
        return null;
    }

    /** Derives service category from service type string. */
    public static String getCategory(String serviceType) {
        if (serviceType == null) return null;
        if (serviceType.startsWith("Wash"))         return CAT_DIY_WASH;
        if (serviceType.startsWith("Dry"))          return CAT_DIY_DRY;
        if (serviceType.startsWith("Full"))         return CAT_FULL_SERVICE;
        return CAT_SPECIAL_ITEM;
    }

    // ── Duration display ──────────────────────────────────
    public String getDurationDisplay() {
        if (estimatedMinutes < 60) return estimatedMinutes + " min";
        if (estimatedMinutes < 1440) return (estimatedMinutes / 60) + " hr" + (estimatedMinutes / 60 > 1 ? "s" : "");
        return (estimatedMinutes / 1440) + " day" + (estimatedMinutes / 1440 > 1 ? "s" : "");
    }
}
