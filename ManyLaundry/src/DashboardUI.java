import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

/**
 * DashboardUI.java
 * Main Swing GUI — POS / kiosk-style laundry management interface.
 */
public class DashboardUI extends JFrame {

    // ── Palette ────────────────────────────────────────────
    // Clean white-and-teal professional theme — high contrast, WCAG-safe
    private static final Color CLR_BG          = new Color(0xF5F7FA);  // off-white page bg
    private static final Color CLR_SIDEBAR      = new Color(0x1C3049);  // deep navy sidebar
    private static final Color CLR_SIDEBAR_SEL  = new Color(0x1A7F8E);  // teal highlight
    private static final Color CLR_ACCENT       = new Color(0x0F6674);  // dark teal — readable on white
    private static final Color CLR_ACCENT2      = new Color(0x1A7F8E);  // medium teal
    private static final Color CLR_WHITE        = Color.WHITE;
    private static final Color CLR_TEXT_DARK    = new Color(0x111827);  // near-black — max legibility
    private static final Color CLR_TEXT_MED     = new Color(0x374151);  // dark gray — still easy to read
    private static final Color CLR_SUCCESS      = new Color(0x166534);  // dark green — readable
    private static final Color CLR_DANGER       = new Color(0x9B1C1C);  // dark red — readable
    private static final Color CLR_WARNING      = new Color(0x92400E);  // dark amber — readable on white
    private static final Color CLR_CARD_BORDER  = new Color(0xCBD5E1);  // neutral gray border

    // ── Fonts ──────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_MONO    = new Font("Monospaced", Font.PLAIN, 12);

    // ── Core ──────────────────────────────────────────────
    private LaundrySystem system;

    // ── Panels ────────────────────────────────────────────
    private JPanel  contentArea;
    private CardLayout cardLayout;

    // Sidebar buttons
    private JButton btnDashboard, btnNewOrder, btnOrders, btnMachines, btnSearch;

    // ── Order table ───────────────────────────────────────
    private DefaultTableModel orderTableModel;
    private JTable            orderTable;

    // ── Machine status labels ─────────────────────────────
    private JLabel[] machineLabels = new JLabel[5];

    // ── Dashboard stats ───────────────────────────────────
    private JLabel lblPending, lblCompleted, lblCancelled, lblAvailMachines;

    public DashboardUI(LaundrySystem system) {
        this.system = system;
        initFrame();
        buildUI();
        showPanel("DASHBOARD");
    }

    // ─────────────────────────────────────────────────────────
    //  FRAME SETUP
    // ─────────────────────────────────────────────────────────

    private void initFrame() {
        setTitle("Many Laundry — Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(CLR_BG);
    }

    // ─────────────────────────────────────────────────────────
    //  MAIN LAYOUT
    // ─────────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildTopBar(),  BorderLayout.NORTH);

        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(CLR_BG);

        contentArea.add(buildDashboardPanel(), "DASHBOARD");
        contentArea.add(buildNewOrderPanel(),  "NEW_ORDER");
        contentArea.add(buildOrdersPanel(),    "ORDERS");
        contentArea.add(buildMachinesPanel(),  "MACHINES");
        contentArea.add(buildSearchPanel(),    "SEARCH");

        add(contentArea, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────
    //  SIDEBAR
    // ─────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(CLR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Logo area
        JPanel logo = new JPanel();
        logo.setBackground(new Color(0x0F1E2E));
        logo.setMaximumSize(new Dimension(200, 90));
        logo.setPreferredSize(new Dimension(200, 90));
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        logo.setBorder(BorderFactory.createEmptyBorder(18, 16, 14, 16));

        JLabel logoIcon = new JLabel("🧺");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoText = new JLabel("MANY LAUNDRY");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoText.setForeground(CLR_WHITE);
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoSub = new JLabel("Management System");
        logoSub.setFont(FONT_SMALL);
        logoSub.setForeground(new Color(0x94A3B8));
        logoSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        logo.add(logoIcon);
        logo.add(Box.createVerticalStrut(4));
        logo.add(logoText);
        logo.add(logoSub);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(12));

        btnDashboard = makeSidebarBtn("🏠", "Dashboard");
        btnNewOrder  = makeSidebarBtn("➕", "New Order");
        btnOrders    = makeSidebarBtn("📋", "All Orders");
        btnMachines  = makeSidebarBtn("🌀", "Machines");
        btnSearch    = makeSidebarBtn("🔍", "Search Order");

        btnDashboard.addActionListener(e -> showPanel("DASHBOARD"));
        btnNewOrder .addActionListener(e -> showPanel("NEW_ORDER"));
        btnOrders   .addActionListener(e -> { refreshOrderTable(); showPanel("ORDERS"); });
        btnMachines .addActionListener(e -> { refreshMachinePanel(); showPanel("MACHINES"); });
        btnSearch   .addActionListener(e -> showPanel("SEARCH"));

        sidebar.add(btnDashboard);
        sidebar.add(btnNewOrder);
        sidebar.add(btnOrders);
        sidebar.add(btnMachines);
        sidebar.add(btnSearch);
        sidebar.add(Box.createVerticalGlue());

        // Version label
        JLabel ver = new JLabel("v1.0 — Many Laundry");
        ver.setFont(FONT_SMALL);
        ver.setForeground(new Color(0x64748B));
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(ver);
        sidebar.add(Box.createVerticalStrut(12));

        return sidebar;
    }

    private JButton makeSidebarBtn(String icon, String label) {
        JButton btn = new JButton(icon + "  " + label);
        btn.setFont(FONT_BODY);
        btn.setForeground(new Color(0xCBD5E1));
        btn.setBackground(CLR_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 10));
        btn.setMaximumSize(new Dimension(200, 44));
        btn.setPreferredSize(new Dimension(200, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0x243B55)); btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(CLR_SIDEBAR); btn.setForeground(new Color(0xCBD5E1)); }
        });
        return btn;
    }

    // ─────────────────────────────────────────────────────────
    //  TOP BAR
    // ─────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CLR_WHITE);
        bar.setPreferredSize(new Dimension(0, 54));
        bar.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, CLR_CARD_BORDER),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));

        JLabel title = new JLabel("🌊  Many Laundry — POS & Management");
        title.setFont(FONT_TITLE);
        title.setForeground(CLR_TEXT_DARK);

        JLabel time = new JLabel("Quezon City, Philippines");
        time.setFont(FONT_SMALL);
        time.setForeground(CLR_TEXT_MED);

        bar.add(title, BorderLayout.WEST);
        bar.add(time,  BorderLayout.EAST);
        return bar;
    }

    // ─────────────────────────────────────────────────────────
    //  DASHBOARD PANEL
    // ─────────────────────────────────────────────────────────

    private JPanel buildDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CLR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Title
        JLabel ttl = new JLabel("Dashboard Overview");
        ttl.setFont(FONT_TITLE);
        ttl.setForeground(CLR_TEXT_DARK);
        p.add(ttl, BorderLayout.NORTH);

        // Stats row
        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setBackground(CLR_BG);
        stats.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        lblPending       = new JLabel("0");
        lblCompleted     = new JLabel("0");
        lblCancelled     = new JLabel("0");
        lblAvailMachines = new JLabel("5");

        stats.add(makeStatCard("⏳ Pending Orders",    lblPending,       new Color(0x92400E)));  // dark amber
        stats.add(makeStatCard("✅ Completed Orders",  lblCompleted,     new Color(0x166534)));  // dark green
        stats.add(makeStatCard("❌ Cancelled Orders",  lblCancelled,     new Color(0x9B1C1C)));  // dark red
        stats.add(makeStatCard("🌀 Free Machines",     lblAvailMachines, new Color(0x0F6674)));  // dark teal

        // Quick actions
        JPanel actions = new JPanel(new GridLayout(2, 3, 14, 14));
        actions.setBackground(CLR_BG);

        actions.add(makeQuickAction("➕  New Order",       "Place a new laundry order",        CLR_ACCENT,  () -> showPanel("NEW_ORDER")));
        actions.add(makeQuickAction("📋  View All Orders", "Browse and manage orders",         CLR_ACCENT2, () -> { refreshOrderTable(); showPanel("ORDERS"); }));
        actions.add(makeQuickAction("🔍  Search Order",    "Find an order by ID",              new Color(0x6F42C1), () -> showPanel("SEARCH")));
        actions.add(makeQuickAction("🌀  Machine Status",  "See all machine states",           new Color(0x20C997), () -> { refreshMachinePanel(); showPanel("MACHINES"); }));
        actions.add(makeQuickAction("✅  Complete Order",  "Mark a pending order as done",     CLR_SUCCESS, this::showCompleteDialog));
        actions.add(makeQuickAction("❌  Cancel Order",    "Cancel an active order",            CLR_DANGER,  this::showCancelDialog));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(CLR_BG);
        center.add(stats,   BorderLayout.NORTH);
        center.add(actions, BorderLayout.CENTER);

        p.add(center, BorderLayout.CENTER);

        return p;
    }

    private JPanel makeStatCard(String label, JLabel numLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(CLR_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(CLR_CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        numLabel.setForeground(accent);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(new Color(0x374151));

        card.add(numLabel, BorderLayout.CENTER);
        card.add(lbl,      BorderLayout.SOUTH);
        return card;
    }

    private JPanel makeQuickAction(String title, String sub, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(CLR_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(CLR_CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel t = new JLabel(title);
        t.setFont(FONT_HEADER);
        t.setForeground(accent);

        JLabel s = new JLabel(sub);
        s.setFont(FONT_SMALL);
        s.setForeground(new Color(0x374151));

        card.add(t, BorderLayout.CENTER);
        card.add(s, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { action.run(); }
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(0xEFF6FF)); }
            public void mouseExited(MouseEvent e)  { card.setBackground(CLR_WHITE); }
        });
        return card;
    }

    // ─────────────────────────────────────────────────────────
    //  NEW ORDER PANEL
    // ─────────────────────────────────────────────────────────

    private JTextField tfCustomerName;
    private JComboBox<String> cmbServiceType;
    private JTextField tfWeight;
    private JLabel lblPricePreview, lblDurationPreview, lblWeightNote;

    private JPanel buildNewOrderPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(CLR_BG);
        outer.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel ttl = new JLabel("➕  Place New Order");
        ttl.setFont(FONT_TITLE);
        ttl.setForeground(CLR_TEXT_DARK);
        outer.add(ttl, BorderLayout.NORTH);

        // Form card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CLR_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(CLR_CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Row 0 – Customer name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        card.add(makeLabel("Customer Name *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        tfCustomerName = makeTextField("Enter full name");
        card.add(tfCustomerName, gbc);

        // Row 1 – Service
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        card.add(makeLabel("Service Type *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        String[] services = {
            "— Select a service —",
            Order.WASH_LITE, Order.WASH_REGULAR,
            Order.DRY_LITE,  Order.DRY_REGULAR,
            Order.FULL_LITE, Order.FULL_REGULAR,
            Order.SPEC_COMFORTER_SMALL, Order.SPEC_COMFORTER_MEDIUM, Order.SPEC_COMFORTER_LARGE,
            Order.SPEC_BARONG_BLAZER, Order.SPEC_GOWN, Order.SPEC_SNEAKERS
        };
        cmbServiceType = new JComboBox<>(services);
        cmbServiceType.setFont(FONT_BODY);
        cmbServiceType.setBackground(CLR_WHITE);
        cmbServiceType.addActionListener(e -> updateOrderPreview());
        card.add(cmbServiceType, gbc);

        // Row 2 – Weight
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        card.add(makeLabel("Weight (kg)"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        tfWeight = makeTextField("e.g. 5.0  (not required for special items)");
        card.add(tfWeight, gbc);

        // Row 3 – weight note
        gbc.gridx = 1; gbc.gridy = 3;
        lblWeightNote = new JLabel(" ");
        lblWeightNote.setFont(FONT_SMALL);
        lblWeightNote.setForeground(CLR_TEXT_MED);
        card.add(lblWeightNote, gbc);

        // Row 4 – Price preview
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        card.add(makeLabel("Price Preview"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        lblPricePreview = new JLabel("—");
        lblPricePreview.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPricePreview.setForeground(new Color(0x0F6674));
        card.add(lblPricePreview, gbc);

        // Row 5 – Duration
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        card.add(makeLabel("Est. Duration"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        lblDurationPreview = new JLabel("—");
        lblDurationPreview.setFont(FONT_BODY);
        lblDurationPreview.setForeground(CLR_TEXT_MED);
        card.add(lblDurationPreview, gbc);

        // Row 6 – Submit
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.weightx = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        JButton btnSubmit = makeAccentButton("  🧺  Place Order  ", CLR_ACCENT);
        btnSubmit.addActionListener(e -> placeOrder());
        card.add(btnSubmit, gbc);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrap.setBackground(CLR_BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        wrap.add(card);

        outer.add(wrap, BorderLayout.CENTER);
        return outer;
    }

    private void updateOrderPreview() {
        String sel = (String) cmbServiceType.getSelectedItem();
        if (sel == null || sel.startsWith("—")) {
            lblPricePreview.setText("—");
            lblDurationPreview.setText("—");
            lblWeightNote.setText(" ");
            return;
        }
        double price    = Order.calculatePrice(sel);
        int    duration = Order.calculateDuration(sel);
        lblPricePreview.setText(String.format("₱ %.2f", price));
        int mins = duration;
        String dur = mins < 60 ? mins + " min"
                   : mins < 1440 ? (mins/60) + " hr" + (mins/60>1?"s":"")
                   : (mins/1440) + " day" + (mins/1440>1?"s":"");
        lblDurationPreview.setText(dur);

        boolean isSpecial = Order.CAT_SPECIAL_ITEM.equals(Order.getCategory(sel));
        if (isSpecial) {
            lblWeightNote.setText("Weight not required for this service.");
            tfWeight.setEnabled(false);
        } else {
            tfWeight.setEnabled(true);
            if (sel.contains("Lite"))    lblWeightNote.setText("Lite load: maximum 4 kg.");
            else if (sel.contains("Regular")) lblWeightNote.setText("Regular load: 4.1 – 8 kg.");
            else lblWeightNote.setText(" ");
        }
    }

    private void placeOrder() {
        String name    = tfCustomerName.getText();
        String service = (String) cmbServiceType.getSelectedItem();
        String wText   = tfWeight.getText().trim();

        double weight = 0;
        if (!wText.isEmpty()) {
            try { weight = Double.parseDouble(wText); }
            catch (NumberFormatException ex) {
                showError("Invalid weight. Please enter a number (e.g. 5.0).");
                return;
            }
        }

        if (service == null || service.startsWith("—")) {
            showError("Please select a service type.");
            return;
        }

        try {
            Order order = system.createOrder(name, service, weight);
            refreshDashboard();
            showReceipt(order);
            // Reset form
            tfCustomerName.setText("");
            cmbServiceType.setSelectedIndex(0);
            tfWeight.setText("");
            lblPricePreview.setText("—");
            lblDurationPreview.setText("—");
            lblWeightNote.setText(" ");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  ORDERS PANEL
    // ─────────────────────────────────────────────────────────

    private JPanel buildOrdersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CLR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel ttl = new JLabel("📋  All Orders");
        ttl.setFont(FONT_TITLE);
        ttl.setForeground(CLR_TEXT_DARK);
        p.add(ttl, BorderLayout.NORTH);

        // Table
        String[] cols = {"Order ID", "Customer", "Service Type", "Weight (kg)",
                         "Machine", "Duration", "Total (₱)", "Status", "Date"};
        orderTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.setFont(FONT_BODY);
        orderTable.setRowHeight(30);
        orderTable.setSelectionBackground(new Color(0xD6EAFF));
        orderTable.getTableHeader().setFont(FONT_HEADER);
        orderTable.getTableHeader().setBackground(new Color(0x1C3049));
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.setGridColor(CLR_CARD_BORDER);
        orderTable.setIntercellSpacing(new Dimension(6, 4));

        // Status column renderer
        orderTable.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());

        JScrollPane sp = new JScrollPane(orderTable);
        sp.setBorder(new LineBorder(CLR_CARD_BORDER, 1, true));
        sp.getViewport().setBackground(CLR_WHITE);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btns.setBackground(CLR_BG);

        JButton btnComplete = makeAccentButton("✅  Complete", CLR_SUCCESS);
        JButton btnCancel   = makeAccentButton("❌  Cancel",   CLR_DANGER);
        JButton btnReceipt  = makeAccentButton("🧾  Receipt",  CLR_ACCENT);
        JButton btnRefresh  = makeAccentButton("🔄  Refresh",  CLR_TEXT_MED);

        btnComplete.addActionListener(e -> completeSelectedOrder());
        btnCancel  .addActionListener(e -> cancelSelectedOrder());
        btnReceipt .addActionListener(e -> receiptSelectedOrder());
        btnRefresh .addActionListener(e -> refreshOrderTable());

        btns.add(btnComplete);
        btns.add(btnCancel);
        btns.add(btnReceipt);
        btns.add(btnRefresh);

        p.add(sp,   BorderLayout.CENTER);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    private void refreshOrderTable() {
        if (orderTableModel == null) return;
        orderTableModel.setRowCount(0);
        for (Order o : system.getAllOrders()) {
            orderTableModel.addRow(new Object[]{
                o.getOrderId(),
                o.getCustomerName(),
                o.getServiceType(),
                o.getWeightKg() > 0 ? o.getWeightKg() + " kg" : "—",
                o.getMachineNumber() > 0 ? "#" + o.getMachineNumber() : "—",
                o.getDurationDisplay(),
                String.format("₱ %.2f", o.getTotalCost()),
                o.getStatus(),
                o.getFormattedDate()
            });
        }
    }

    private void completeSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) { showError("Please select an order from the table."); return; }
        String id = (String) orderTableModel.getValueAt(row, 0);
        try {
            system.completeOrder(id);
            showInfo("Order " + id + " marked as COMPLETED.");
            refreshOrderTable();
            refreshDashboard();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void cancelSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) { showError("Please select an order from the table."); return; }
        String id = (String) orderTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel order " + id + "?",
            "Confirm Cancel", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            system.cancelOrder(id);
            showInfo("Order " + id + " has been CANCELLED.");
            refreshOrderTable();
            refreshDashboard();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void receiptSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) { showError("Please select an order from the table."); return; }
        String id = (String) orderTableModel.getValueAt(row, 0);
        Order o = system.findOrder(id);
        if (o != null) showReceipt(o);
    }

    // ─────────────────────────────────────────────────────────
    //  MACHINES PANEL
    // ─────────────────────────────────────────────────────────

    private JPanel machineCardsPanel;

    private JPanel buildMachinesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CLR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel ttl = new JLabel("🌀  Machine Status");
        ttl.setFont(FONT_TITLE);
        ttl.setForeground(CLR_TEXT_DARK);
        p.add(ttl, BorderLayout.NORTH);

        machineCardsPanel = new JPanel(new GridLayout(1, 5, 16, 0));
        machineCardsPanel.setBackground(CLR_BG);
        machineCardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (int i = 0; i < 5; i++) {
            machineLabels[i] = new JLabel();
            machineCardsPanel.add(buildMachineCard(i + 1, machineLabels[i]));
        }

        refreshMachinePanel();
        p.add(machineCardsPanel, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildMachineCard(int id, JLabel statusLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(CLR_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(CLR_CARD_BORDER, 2, true),
            BorderFactory.createEmptyBorder(20, 16, 20, 16)
        ));

        JLabel icon = new JLabel("🌀", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));

        JLabel num = new JLabel("Machine #" + id, SwingConstants.CENTER);
        num.setFont(FONT_HEADER);
        num.setForeground(CLR_TEXT_DARK);

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        card.add(icon,        BorderLayout.NORTH);
        card.add(num,         BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.SOUTH);
        return card;
    }

    private void refreshMachinePanel() {
        if (machineLabels[0] == null) return;
        List<Machine> machines = system.getAllMachines();
        for (int i = 0; i < machines.size() && i < 5; i++) {
            Machine m = machines.get(i);
            if (m.isAvailable()) {
                machineLabels[i].setText("● AVAILABLE");
                machineLabels[i].setForeground(new Color(0x166534));
            } else {
                machineLabels[i].setText("<html><center>● OCCUPIED<br><small>" + m.getCurrentOrderId() + "</small></center></html>");
                machineLabels[i].setForeground(new Color(0x9B1C1C));
            }
        }
        // update sidebar machine panel cards background colors
        if (machineCardsPanel != null) {
            Component[] comps = machineCardsPanel.getComponents();
            for (int i = 0; i < comps.length && i < machines.size(); i++) {
                Machine m = machines.get(i);
                comps[i].setBackground(m.isAvailable() ? new Color(0xF0FDF4) : new Color(0xFEF2F2));
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    //  SEARCH PANEL
    // ─────────────────────────────────────────────────────────

    private JTextField tfSearchId;
    private JTextArea  taSearchResult;

    private JPanel buildSearchPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CLR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel ttl = new JLabel("🔍  Search Order");
        ttl.setFont(FONT_TITLE);
        ttl.setForeground(CLR_TEXT_DARK);
        p.add(ttl, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(CLR_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(CLR_CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));

        // Search row
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(CLR_WHITE);
        tfSearchId = makeTextField("Enter Order ID (e.g. ML-1001)");
        JButton btnSearch2 = makeAccentButton("🔍  Search", CLR_ACCENT);
        btnSearch2.addActionListener(e -> doSearch());
        tfSearchId.addActionListener(e -> doSearch());
        row.add(tfSearchId,  BorderLayout.CENTER);
        row.add(btnSearch2,  BorderLayout.EAST);

        // Result area
        taSearchResult = new JTextArea(18, 40);
        taSearchResult.setFont(FONT_MONO);
        taSearchResult.setEditable(false);
        taSearchResult.setBackground(new Color(0xF8FAFC));
        taSearchResult.setForeground(new Color(0x111827));
        taSearchResult.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        taSearchResult.setText("Enter an Order ID above and press Search.");
        JScrollPane sp = new JScrollPane(taSearchResult);
        sp.setBorder(new LineBorder(CLR_CARD_BORDER, 1, true));

        // Action buttons for found order
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionRow.setBackground(CLR_WHITE);
        JButton btnComp = makeAccentButton("✅  Complete This Order", CLR_SUCCESS);
        JButton btnCanc = makeAccentButton("❌  Cancel This Order",   CLR_DANGER);
        JButton btnRcpt = makeAccentButton("🧾  View Receipt",         CLR_ACCENT);
        btnComp.addActionListener(e -> completeFromSearch());
        btnCanc.addActionListener(e -> cancelFromSearch());
        btnRcpt.addActionListener(e -> receiptFromSearch());
        actionRow.add(btnComp);
        actionRow.add(btnCanc);
        actionRow.add(btnRcpt);

        card.add(row,       BorderLayout.NORTH);
        card.add(sp,        BorderLayout.CENTER);
        card.add(actionRow, BorderLayout.SOUTH);

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private Order lastFoundOrder = null;

    private void doSearch() {
        String id = tfSearchId.getText().trim();
        if (id.isEmpty()) { showError("Please enter an Order ID."); return; }
        Order o = system.findOrder(id);
        if (o == null) {
            lastFoundOrder = null;
            taSearchResult.setText("No order found with ID: " + id);
        } else {
            lastFoundOrder = o;
            Receipt r = new Receipt(o);
            taSearchResult.setText(r.generate());
        }
    }

    private void completeFromSearch() {
        if (lastFoundOrder == null) { showError("Search for an order first."); return; }
        try {
            system.completeOrder(lastFoundOrder.getOrderId());
            showInfo("Order " + lastFoundOrder.getOrderId() + " marked as COMPLETED.");
            doSearch(); refreshDashboard();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void cancelFromSearch() {
        if (lastFoundOrder == null) { showError("Search for an order first."); return; }
        try {
            system.cancelOrder(lastFoundOrder.getOrderId());
            showInfo("Order " + lastFoundOrder.getOrderId() + " has been CANCELLED.");
            doSearch(); refreshDashboard();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void receiptFromSearch() {
        if (lastFoundOrder == null) { showError("Search for an order first."); return; }
        // Re-fetch to get updated status
        Order updated = system.findOrder(lastFoundOrder.getOrderId());
        showReceipt(updated);
    }

    // ─────────────────────────────────────────────────────────
    //  COMPLETE / CANCEL DIALOGS (from dashboard)
    // ─────────────────────────────────────────────────────────

    private void showCompleteDialog() {
        String id = JOptionPane.showInputDialog(this, "Enter Order ID to mark as COMPLETED:",
                                                "Complete Order", JOptionPane.PLAIN_MESSAGE);
        if (id == null || id.trim().isEmpty()) return;
        try {
            system.completeOrder(id.trim());
            showInfo("Order " + id.trim() + " marked as COMPLETED.");
            refreshDashboard();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void showCancelDialog() {
        String id = JOptionPane.showInputDialog(this, "Enter Order ID to CANCEL:",
                                                "Cancel Order", JOptionPane.PLAIN_MESSAGE);
        if (id == null || id.trim().isEmpty()) return;
        try {
            system.cancelOrder(id.trim());
            showInfo("Order " + id.trim() + " has been CANCELLED.");
            refreshDashboard();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    // ─────────────────────────────────────────────────────────
    //  RECEIPT DIALOG
    // ─────────────────────────────────────────────────────────

    private void showReceipt(Order order) {
        Receipt receipt  = new Receipt(order);
        String  text     = receipt.generate();

        JDialog dlg = new JDialog(this, "🧾  Order Receipt — " + order.getOrderId(), true);
        dlg.setSize(440, 540);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(CLR_WHITE);

        JTextArea ta = new JTextArea(text);
        ta.setFont(FONT_MONO);
        ta.setEditable(false);
        ta.setBackground(CLR_WHITE);
        ta.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(null);

        JButton close = makeAccentButton("  Close  ", CLR_ACCENT);
        close.addActionListener(e -> dlg.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        btnPanel.setBackground(CLR_WHITE);
        btnPanel.add(close);

        dlg.add(sp,       BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────
    //  NAVIGATION
    // ─────────────────────────────────────────────────────────

    private void showPanel(String name) {
        if ("DASHBOARD".equals(name)) refreshDashboard();
        cardLayout.show(contentArea, name);
    }

    private void refreshDashboard() {
        if (lblPending == null) return;
        lblPending.setText("" + system.getPendingCount());
        lblCompleted.setText("" + system.getCompletedCount());
        lblCancelled.setText("" + system.getCancelledCount());
        lblAvailMachines.setText("" + system.getAvailableMachineCount());
    }

    // ─────────────────────────────────────────────────────────
    //  UI HELPERS
    // ─────────────────────────────────────────────────────────

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(CLR_TEXT_MED);
        return l;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setForeground(CLR_TEXT_DARK);
        tf.setPreferredSize(new Dimension(280, 36));
        tf.setBorder(new CompoundBorder(
            new LineBorder(CLR_CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        // Placeholder behaviour
        tf.setForeground(new Color(0xAAAAAA));
        tf.setText(placeholder);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText(""); tf.setForeground(CLR_TEXT_DARK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setForeground(new Color(0xAAAAAA)); tf.setText(placeholder);
                }
            }
        });
        return tf;
    }

    private JButton makeAccentButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setBackground(bg);
        b.setForeground(CLR_WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            Color orig = bg;
            public void mouseEntered(MouseEvent e) { b.setBackground(orig.darker()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(orig); }
        });
        return b;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────
    //  STATUS RENDERER
    // ─────────────────────────────────────────────────────────

    private static class StatusRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            String s = val == null ? "" : val.toString();
            if (Order.STATUS_COMPLETED.equals(s)) {
                setForeground(new Color(0x166534)); setFont(getFont().deriveFont(Font.BOLD));
            } else if (Order.STATUS_CANCELLED.equals(s)) {
                setForeground(new Color(0x9B1C1C)); setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setForeground(new Color(0x92400E)); setFont(getFont().deriveFont(Font.BOLD));
            }
            setHorizontalAlignment(CENTER);
            return this;
        }
    }
}
