package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainWindow {
    private JFrame frame;

    // 主方法，启动程序
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "窗口初始化失败");
                }
            }
        });
    }

    // 窗口构造方法
    public MainWindow() {
        initialize();
    }

    // 窗口初始化
    private void initialize() {
        // 设置窗口组件风格和字体风格、大小
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.put("TabbedPane.font", new Font("宋体", Font.PLAIN, 16));
            UIManager.put("Label.font", new Font("宋体", Font.PLAIN, 16));
            UIManager.put("ComboBox.font", new Font("宋体", Font.PLAIN, 16));
            UIManager.put("Button.font", new Font("宋体", Font.PLAIN, 14));
            UIManager.put("TextField.font", new Font("宋体", Font.PLAIN, 14));
            UIManager.put("TextArea.font", new Font("宋体", Font.PLAIN, 14));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "无法应用此窗口样式，请使用Windows操作系统！");
        }
        // 设置主窗口的位置、尺寸和布局
        frame = new JFrame("双向加密通信系统");
        frame.setBounds(500, 250, 400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        // 标签页面板，用于设置客户端与服务端的布局展示
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // 设置服务器标签页的主面板
        JPanel mainServerPanel = new JPanel();
        tabbedPane.addTab("服务端", null, mainServerPanel, null);
        mainServerPanel.setLayout(new BoxLayout(mainServerPanel, BoxLayout.Y_AXIS));

        // 服务端面板的上部，包含端口号的标签、文本框
        JPanel panelUp = new JPanel();
        mainServerPanel.add(panelUp, BorderLayout.CENTER);
        GridBagLayout gbl_panelUp = new GridBagLayout();      // 设置布局
        gbl_panelUp.columnWidths = new int[]{100, 200};       // 标签和下拉框的最低宽度
        gbl_panelUp.rowHeights = new int[]{0, 0};
        gbl_panelUp.columnWeights = new double[]{1.0, 2.0};
        gbl_panelUp.rowWeights = new double[]{0.0, 0.0};
        panelUp.setLayout(gbl_panelUp);

        // 端口号的标签
        JLabel serverPortLabel = new JLabel("服务端口号：");
        serverPortLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        serverPortLabel.setAutoscrolls(true);
        GridBagConstraints gbc_serverPortLabel = new GridBagConstraints();
        gbc_serverPortLabel.fill = GridBagConstraints.BOTH;
        gbc_serverPortLabel.insets = new Insets(0, 0, 5, 5);
        gbc_serverPortLabel.gridx = 0;
        gbc_serverPortLabel.gridy = 0;
        panelUp.add(serverPortLabel, gbc_serverPortLabel);

        // 端口号文本框
        JTextField serverPortTextField = new JTextField();
        GridBagConstraints gbc_serverPortTextField = new GridBagConstraints();
        gbc_serverPortTextField.gridwidth = 1;
        gbc_serverPortTextField.fill = GridBagConstraints.BOTH;
        gbc_serverPortTextField.insets = new Insets(0, 0, 5, 5);
        gbc_serverPortTextField.gridx = 1;
        gbc_serverPortTextField.gridy = 0;
        serverPortTextField.setText("8888");
        panelUp.add(serverPortTextField, gbc_serverPortTextField);

        // 端口号的标签
        JLabel infoLabel = new JLabel("<html><div width='300'>本程序只支持两方的通信，请勿使用多个客户端同时连接服务端。</div></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setAutoscrolls(true);
        GridBagConstraints gbc_infoLabel = new GridBagConstraints();
        gbc_infoLabel.gridwidth = 2;
        gbc_infoLabel.fill = GridBagConstraints.BOTH;
        gbc_infoLabel.insets = new Insets(20, 0, 5, 5);
        gbc_infoLabel.gridx = 0;
        gbc_infoLabel.gridy = 1;
        panelUp.add(infoLabel, gbc_infoLabel);

        // 服务端主面板的下部，包括启动通信的按钮
        JPanel panelDown = new JPanel();
        panelDown.setBorder(new EmptyBorder(0, 0, 10, 0));       // 边距
        mainServerPanel.add(panelDown);
        // 点击按钮开启服务端通信窗口
        JButton serverBtn = new JButton("启动服务端");
        serverBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        panelDown.add(serverBtn);
        serverBtn.addActionListener(e -> {
            String port = serverPortTextField.getText();
            // 端口号是否为空
            if (port.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "端口号不可为空！");
            } else {
                try {
                    int portNumber = Integer.parseInt(serverPortTextField.getText());
                    // 建立服务端通信窗口，传入加密算法和哈希算法等的选择以及端口号
                    ServerWindow s = new ServerWindow(150, 100, portNumber, serverBtn);
                    serverBtn.setEnabled(false);
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(frame, "请输入正确的端口号！");
                }
            }
        });


        // 客户端主面板
        JPanel mainClientPanel = new JPanel();
        tabbedPane.addTab("客户端", null, mainClientPanel, null);
        mainClientPanel.setLayout(new BoxLayout(mainClientPanel, BoxLayout.Y_AXIS));

        // 客户端标签页主面板的上半部分，包括三行标签和下拉框以及IP地址、端口号的输入
        JPanel panelUp1 = new JPanel();
        mainClientPanel.add(panelUp1, BorderLayout.CENTER);
        GridBagLayout gbl_panelUp1 = new GridBagLayout();      // 设置布局
        gbl_panelUp1.columnWidths = new int[]{125, 125};       // 标签和下拉框的最低宽度
        gbl_panelUp1.rowHeights = new int[]{0, 0, 0, 0};
        gbl_panelUp1.columnWeights = new double[]{0.0, 0.0};
        gbl_panelUp1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
        panelUp1.setLayout(gbl_panelUp1);
        // 标签1
        JLabel selectLabel1 = new JLabel("对称加密算法：");
        selectLabel1.setHorizontalAlignment(SwingConstants.RIGHT);      // 标签文字靠右对齐
        GridBagConstraints gbc_selectLabel1 = new GridBagConstraints(); // GridBag 布局配置，指明占据的区域
        gbc_selectLabel1.fill = GridBagConstraints.BOTH;
        gbc_selectLabel1.insets = new Insets(0, 0, 5, 5);
        gbc_selectLabel1.gridx = 0;
        gbc_selectLabel1.gridy = 0;
        panelUp1.add(selectLabel1, gbc_selectLabel1);                    // 添加标签到面板
        // 下拉框1
        JComboBox<String> comboBox1 = new JComboBox<>();
        comboBox1.addItem("DES");      // 下拉框的可选值
        comboBox1.addItem("AES-128");  // 下拉框的可选值
        GridBagConstraints gbc_comboBox1 = new GridBagConstraints();
        gbc_comboBox1.fill = GridBagConstraints.BOTH;
        gbc_comboBox1.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox1.gridx = 1;
        gbc_comboBox1.gridy = 0;
        panelUp1.add(comboBox1, gbc_comboBox1);
        // 标签2
        JLabel selectLabel2 = new JLabel("对称加密工作模式：");
        selectLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_selectLabel2 = new GridBagConstraints();
        gbc_selectLabel2.fill = GridBagConstraints.BOTH;
        gbc_selectLabel2.insets = new Insets(0, 0, 5, 5);
        gbc_selectLabel2.gridx = 0;
        gbc_selectLabel2.gridy = 1;
        panelUp1.add(selectLabel2, gbc_selectLabel2);
        // 下拉框2
        JComboBox<String> comboBox2 = new JComboBox<>();
        comboBox2.addItem("ECB");
        comboBox2.addItem("CBC");
        GridBagConstraints gbc_comboBox2 = new GridBagConstraints();
        gbc_comboBox2.fill = GridBagConstraints.BOTH;
        gbc_comboBox2.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox2.gridx = 1;
        gbc_comboBox2.gridy = 1;
        panelUp1.add(comboBox2, gbc_comboBox2);
        // 标签3
        JLabel selectLabel3 = new JLabel("Hash算法：");
        selectLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_selectLabel3 = new GridBagConstraints();
        gbc_selectLabel3.fill = GridBagConstraints.BOTH;
        gbc_selectLabel3.insets = new Insets(0, 0, 5, 5);
        gbc_selectLabel3.gridx = 0;
        gbc_selectLabel3.gridy = 2;
        panelUp1.add(selectLabel3, gbc_selectLabel3);
        // 下拉框3
        JComboBox<String> comboBox3 = new JComboBox<>();
        comboBox3.addItem("MD5");
        comboBox3.addItem("SHA-256");
        GridBagConstraints gbc_comboBox3 = new GridBagConstraints();
        gbc_comboBox3.fill = GridBagConstraints.BOTH;
        gbc_comboBox3.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox3.gridx = 1;
        gbc_comboBox3.gridy = 2;
        panelUp1.add(comboBox3, gbc_comboBox3);
        // 标签4
        JLabel selectLabel4 = new JLabel("非对称加密算法：");
        selectLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_selectLabel4 = new GridBagConstraints();
        gbc_selectLabel4.fill = GridBagConstraints.BOTH;
        gbc_selectLabel4.insets = new Insets(0, 0, 5, 5);
        gbc_selectLabel4.gridx = 0;
        gbc_selectLabel4.gridy = 3;
        panelUp1.add(selectLabel4, gbc_selectLabel4);
        // 下拉框4
        JComboBox<String> comboBox4 = new JComboBox<>();
        comboBox4.addItem("RSA");
        comboBox4.setEnabled(false);
        GridBagConstraints gbc_comboBox4 = new GridBagConstraints();
        gbc_comboBox4.fill = GridBagConstraints.BOTH;
        gbc_comboBox4.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox4.gridx = 1;
        gbc_comboBox4.gridy = 3;
        panelUp1.add(comboBox4, gbc_comboBox4);

        // IP 地址的标签
        JLabel IPLabel = new JLabel("请输入IP地址：");
        IPLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        IPLabel.setAutoscrolls(true);
        GridBagConstraints gbc_IPLabel = new GridBagConstraints();
        gbc_IPLabel.fill = GridBagConstraints.BOTH;
        gbc_IPLabel.insets = new Insets(0, 0, 5, 5);
        gbc_IPLabel.gridx = 0;
        gbc_IPLabel.gridy = 4;
        panelUp1.add(IPLabel, gbc_IPLabel);

        // IP 地址文本框
        JTextField IPTextField = new JTextField();
        GridBagConstraints gbc_IPTextField = new GridBagConstraints();
        gbc_IPTextField.gridwidth = 1;
        gbc_IPTextField.fill = GridBagConstraints.BOTH;
        gbc_IPTextField.insets = new Insets(0, 0, 5, 0);
        gbc_IPTextField.gridx = 1;
        gbc_IPTextField.gridy = 4;
        IPTextField.setText("127.0.0.1");
        panelUp1.add(IPTextField, gbc_IPTextField);

        // 端口号的标签
        JLabel clientPortLabel = new JLabel("请输入端口号：");
        clientPortLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clientPortLabel.setAutoscrolls(true);
        GridBagConstraints gbc_clientPortLabel = new GridBagConstraints();
        gbc_clientPortLabel.fill = GridBagConstraints.BOTH;
        gbc_clientPortLabel.insets = new Insets(0, 0, 5, 5);
        gbc_clientPortLabel.gridx = 0;
        gbc_clientPortLabel.gridy = 5;
        panelUp1.add(clientPortLabel, gbc_clientPortLabel);

        // 端口号文本框
        JTextField clientPortTextField = new JTextField();
        GridBagConstraints gbc_clientPortTextField = new GridBagConstraints();
        gbc_clientPortTextField.gridwidth = 1;
        gbc_clientPortTextField.fill = GridBagConstraints.BOTH;
        gbc_clientPortTextField.insets = new Insets(0, 0, 5, 0);
        gbc_clientPortTextField.gridx = 1;
        gbc_clientPortTextField.gridy = 5;
        clientPortTextField.setText("8888");
        panelUp1.add(clientPortTextField, gbc_clientPortTextField);

        // 客户端主面板的下部，包括启动通信的按钮
        JPanel panelDown1 = new JPanel();
        panelDown1.setBorder(new EmptyBorder(0, 0, 5, 0));       // 边距
        mainClientPanel.add(panelDown1);
        // 点击按钮开启客户端通信窗口
        JButton clientBtn = new JButton("启动客户端");
        clientBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        panelDown1.add(clientBtn);
        clientBtn.addActionListener(e -> {
            String IP = IPTextField.getText();
            // IP 地址是否为空
            if (IP.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "IP地址不可为空！");
            } else {
                try {
                    String regex = "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}" +
                            "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$";
                    // 判断 IP 地址是否合法
                    if (IP.matches(regex)) {
                        String port = clientPortTextField.getText();
                        // 端口号是否为空
                        if (port.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "端口号不可为空！");
                        } else {
                            int encrypt = comboBox1.getSelectedIndex();   // 获取选择的对称加密算法
                            int mode = comboBox2.getSelectedIndex();      // 获取对称加密算法的加密模式
                            int hash = comboBox3.getSelectedIndex();      // 获取 Hash 算法
                            int portNumber = Integer.parseInt(clientPortTextField.getText());
                            // 建立通信窗口：客户端，传入 IP 地址和端口
                            ClientWindow c = new ClientWindow(750, 100, encrypt, mode, hash, IP, portNumber, clientBtn);
                            clientBtn.setEnabled(false);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "请输入合法的IPv4地址！");
                    }
                } catch (NumberFormatException ex1) {
                    JOptionPane.showMessageDialog(frame, "请输入正确的端口号！");
                }
            }
        });
    }
}