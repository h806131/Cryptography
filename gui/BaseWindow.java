package gui;

import algorithm.*;
import util.Convert;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

// 客户端与服务端的基类窗口
public class BaseWindow extends JFrame {
    protected JButton parentBtn;
    protected JPanel contentPanel;
    protected ServerSocket serverSocket = null;
    protected Socket clientSocket = null;
    protected InputStream in = null;
    protected OutputStream out = null;
    protected JTextArea msgEditArea;
    protected JTextField keySeedTextField;
    protected JTextField vectorTextField;
    protected JTextField fileTextField;
    protected JTextArea msgTextArea;
    protected JTextArea debugTextArea;
    protected JButton genIVBtn;
    protected File file;   // 需要发送的文件
    protected int port;    // 端口号
    protected int encrypt; // 对称加密算法
    protected int mode;    // 对称加密算法
    protected int hash;    // 哈希算法
    protected byte[] key;  // 对称加密密钥
    protected int keyLen;  // 对称加密密钥长度（这里也等于分组长度）
    protected int hashLen; // Hash 值的长度
    protected BigInteger[] myKeyPair; // 自己的非对称密钥（公钥和私钥，n，d，e）
    protected BigInteger n, e; // 对方的公钥

    // 窗口构造方法
    public BaseWindow(int x, int y, int port, JButton parentBtn) {
        this.parentBtn = parentBtn;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 默认的窗口关闭行为

        setBounds(x, y, 570, 670); // 设置窗口坐标和尺寸
        contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPanel);
        contentPanel.setLayout(new BorderLayout(0, 0));

        // 窗口上半部分的面板，包括文本框和按钮等
        JPanel panelUp = new JPanel();
        panelUp.setBorder(new EmptyBorder(10, 0, 0, 0));
        contentPanel.add(panelUp);
        GridBagLayout gbl_panelUp = new GridBagLayout();
        gbl_panelUp.columnWeights = new double[]{1.0, 2.0, 2.0, 1.0};
        gbl_panelUp.columnWidths = new int[]{80, 160, 160, 80};
        gbl_panelUp.rowHeights = new int[]{0, 0, 0, 0, 0};
        gbl_panelUp.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
        panelUp.setLayout(gbl_panelUp);

        // 随机种子的标签、文本框和按钮
        JLabel seedLabel = new JLabel("请输入种子：");
        seedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        seedLabel.setAutoscrolls(true);
        GridBagConstraints gbc_seedLabel = new GridBagConstraints();
        gbc_seedLabel.fill = GridBagConstraints.BOTH;
        gbc_seedLabel.insets = new Insets(0, 0, 5, 5);
        gbc_seedLabel.gridx = 0;
        gbc_seedLabel.gridy = 0;
        panelUp.add(seedLabel, gbc_seedLabel);

        // 随机种子文本框
        keySeedTextField = new JTextField();
        GridBagConstraints gbc_keySeedTextField = new GridBagConstraints();
        gbc_keySeedTextField.gridwidth = 2;
        gbc_keySeedTextField.fill = GridBagConstraints.BOTH;
        gbc_keySeedTextField.insets = new Insets(0, 0, 5, 5);
        gbc_keySeedTextField.gridx = 1;
        gbc_keySeedTextField.gridy = 0;
        panelUp.add(keySeedTextField, gbc_keySeedTextField);

        // 生成对称密钥的按钮
        JButton genKeyMsgBtn = new JButton("生成密钥");
        GridBagConstraints gbc_genKeyMsgBtn = new GridBagConstraints();
        gbc_genKeyMsgBtn.fill = GridBagConstraints.BOTH;
        gbc_genKeyMsgBtn.insets = new Insets(0, 0, 5, 0);
        gbc_genKeyMsgBtn.gridx = 3;
        gbc_genKeyMsgBtn.gridy = 0;
        panelUp.add(genKeyMsgBtn, gbc_genKeyMsgBtn);
        genKeyMsgBtn.addActionListener(e -> genKey()); // 绑定密钥生成方法

        // 消息输入框
        msgEditArea = new JTextArea();
        msgEditArea.setRows(5);
        msgEditArea.setLineWrap(true); // 自动换行
        DefaultCaret caret_ = (DefaultCaret) msgEditArea.getCaret();
        caret_.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // 文本域添加新的内容之后滚动条自动下移
        GridBagConstraints gbc_msgEditArea = new GridBagConstraints();
        gbc_msgEditArea.gridwidth = 3;
        gbc_msgEditArea.insets = new Insets(0, 0, 5, 5);
        gbc_msgEditArea.fill = GridBagConstraints.BOTH;
        gbc_msgEditArea.gridx = 0;
        gbc_msgEditArea.gridy = 1;
        JScrollPane jsp = new JScrollPane(msgEditArea);
        jsp.setBounds(10, 10, 350, 350);
        // 让滚动条一直显示
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelUp.add(jsp, gbc_msgEditArea);

        // 发送消息按钮
        JButton sendMsgBtn = new JButton("发送消息");
        GridBagConstraints gbc_sendMsgBtn = new GridBagConstraints();
        gbc_sendMsgBtn.fill = GridBagConstraints.HORIZONTAL;
        gbc_sendMsgBtn.insets = new Insets(0, 0, 5, 0);
        gbc_sendMsgBtn.gridx = 3;
        gbc_sendMsgBtn.gridy = 1;
        panelUp.add(sendMsgBtn, gbc_sendMsgBtn);
        sendMsgBtn.addActionListener(e -> sendMsg()); // 绑定消息发送的方法

        // 文件选择按钮
        JButton fileBtn = new JButton("选择文件");
        fileBtn.addActionListener(e -> chooseFile());
        GridBagConstraints gbc_fileBtn = new GridBagConstraints();
        gbc_sendMsgBtn.gridwidth = 1;
        gbc_fileBtn.fill = GridBagConstraints.HORIZONTAL;
        gbc_fileBtn.insets = new Insets(0, 0, 5, 5);
        gbc_fileBtn.gridx = 0;
        gbc_fileBtn.gridy = 2;
        panelUp.add(fileBtn, gbc_fileBtn);

        // 文本框，显示文件路径信息
        fileTextField = new JTextField();
        fileTextField.setEnabled(false);
        fileTextField.setText("文件信息");
        GridBagConstraints gbc_fileTextField = new GridBagConstraints();
        gbc_fileTextField.gridwidth = 2;
        gbc_fileTextField.insets = new Insets(0, 0, 5, 5);
        gbc_fileTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_fileTextField.gridx = 1;
        gbc_fileTextField.gridy = 2;
        panelUp.add(fileTextField, gbc_fileTextField);

        // 发送文件按钮
        JButton sendFileBtn = new JButton("发送文件");
        sendFileBtn.addActionListener(e -> sendFile()); // 绑定文件发送的方法
        GridBagConstraints gbc_sendFileBtn = new GridBagConstraints();
        gbc_sendFileBtn.insets = new Insets(0, 0, 5, 0);
        gbc_sendFileBtn.gridwidth = 1;
        gbc_sendFileBtn.fill = GridBagConstraints.HORIZONTAL;
        gbc_sendFileBtn.gridx = 3;
        gbc_sendFileBtn.gridy = 2;
        panelUp.add(sendFileBtn, gbc_sendFileBtn);

        // 初始向量的标签、文本框和按钮
        JLabel vectorLabel = new JLabel("初始向量(Hex)：");
        seedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        seedLabel.setAutoscrolls(true);
        GridBagConstraints gbc_vectorLabel = new GridBagConstraints();
        gbc_vectorLabel.fill = GridBagConstraints.BOTH;
        gbc_vectorLabel.insets = new Insets(0, 0, 5, 5);
        gbc_vectorLabel.gridx = 0;
        gbc_vectorLabel.gridy = 3;
        panelUp.add(vectorLabel, gbc_vectorLabel);

        // 初始向量文本框
        vectorTextField = new JTextField();
        GridBagConstraints gbc_vectorTextField = new GridBagConstraints();
        gbc_vectorTextField.gridwidth = 2;
        gbc_vectorTextField.fill = GridBagConstraints.BOTH;
        gbc_vectorTextField.insets = new Insets(0, 0, 5, 5);
        gbc_vectorTextField.gridx = 1;
        gbc_vectorTextField.gridy = 3;
        vectorTextField.setEnabled(false);
        panelUp.add(vectorTextField, gbc_vectorTextField);

        // 发送文件按钮
        genIVBtn = new JButton("自动生成");
        genIVBtn.addActionListener(e -> genIV()); // 绑定文件发送的方法
        GridBagConstraints gbc_genIVBtn = new GridBagConstraints();
        gbc_genIVBtn.insets = new Insets(0, 0, 5, 0);
        gbc_genIVBtn.gridwidth = 1;
        gbc_genIVBtn.fill = GridBagConstraints.HORIZONTAL;
        gbc_genIVBtn.gridx = 3;
        gbc_genIVBtn.gridy = 3;
        genIVBtn.setEnabled(false);
        panelUp.add(genIVBtn, gbc_genIVBtn);

        // 标签：消息列表
        JLabel msgLabel = new JLabel("消息列表");
        GridBagConstraints gbc_msgLabel = new GridBagConstraints();
        gbc_msgLabel.gridwidth = 2;
        gbc_msgLabel.insets = new Insets(0, 0, 5, 5);
        gbc_msgLabel.gridx = 0;
        gbc_msgLabel.gridy = 4;
        panelUp.add(msgLabel, gbc_msgLabel);

        // 标签：运行细节
        JLabel debugLabel = new JLabel("运行细节");
        GridBagConstraints gbc_debugLabel = new GridBagConstraints();
        gbc_debugLabel.gridwidth = 2;
        gbc_debugLabel.insets = new Insets(0, 0, 5, 0);
        gbc_debugLabel.gridx = 2;
        gbc_debugLabel.gridy = 4;
        panelUp.add(debugLabel, gbc_debugLabel);

        // 消息列表（文本域）
        msgTextArea = new JTextArea();
        msgTextArea.setEditable(false);
        msgTextArea.setRows(25);
        msgTextArea.setLineWrap(true); // 自动换行
        DefaultCaret caret = (DefaultCaret) msgTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // 文本域添加新的内容之后滚动条自动下移
        GridBagConstraints gbc_msgTextArea = new GridBagConstraints();
        gbc_msgTextArea.gridwidth = 2;
        gbc_msgTextArea.insets = new Insets(0, 0, 0, 5);
        gbc_msgTextArea.fill = GridBagConstraints.HORIZONTAL;
        gbc_msgTextArea.gridx = 0;
        gbc_msgTextArea.gridy = 5;
        JScrollPane jsp1 = new JScrollPane(msgTextArea);
        jsp1.setBounds(10, 10, 350, 350);
        // 让滚动条一直显示
        jsp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelUp.add(jsp1, gbc_msgTextArea);

        // 运行细节（文本域）
        debugTextArea = new JTextArea();
        debugTextArea.setEditable(false);
        debugTextArea.setRows(25);
        debugTextArea.setLineWrap(true); // 自动换行
        DefaultCaret caret1 = (DefaultCaret) debugTextArea.getCaret();
        caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        GridBagConstraints gbc_debugTextArea = new GridBagConstraints();
        gbc_debugTextArea.gridwidth = 2;
        gbc_debugTextArea.fill = GridBagConstraints.HORIZONTAL;
        gbc_debugTextArea.gridx = 2;
        gbc_debugTextArea.gridy = 5;
        JScrollPane jsp2 = new JScrollPane(debugTextArea);
        jsp2.setBounds(10, 10, 350, 350);
        jsp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelUp.add(jsp2, gbc_debugTextArea);

        // 窗口关闭时的资源处理
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        this.port = port;
    }

    // 根据随机种子生成对称加密密钥
    private void genKey() {
        String seed = keySeedTextField.getText(); // 获取随机种子
        if (seed.isEmpty()) {
            JOptionPane.showMessageDialog(this, "种子不可为空！");
        } else {
            SecureRandom random = new SecureRandom(seed.getBytes());
            try {
                // 判断对称加密算法并生成密钥，设置密钥长度
                if (encrypt == 0) {
                    // 获取DES密钥生成器
                    KeyGenerator keyGen = KeyGenerator.getInstance("DES");
                    keyGen.init(random);                          // 用种子对密钥生成器进行初始化
                    SecretKey secretKey = keyGen.generateKey();   // 生成64位DES密钥
                    key = secretKey.getEncoded();                 // 获取密钥字节数组
                } else {
                    // 获取AES密钥生成器
                    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                    keyGen.init(128, random);              // 设置密钥长度为128位
                    SecretKey secretKey = keyGen.generateKey();   // 生成 AES-128 密钥
                    key = secretKey.getEncoded();                 // 获取密钥字节数组
                }
                debugTextArea.append("生成对称密钥：" + Convert.bytesToHexStr(key) + "，通信用的对称密钥已更新\n\n");
            } catch (NoSuchAlgorithmException ex) {
                JOptionPane.showMessageDialog(this, "无法生成此算法的密钥！");
            }
        }
    }

    // 保存字节数组到文件。这里主要用于保存加密了的文件和签名。
    protected void saveFile(byte[] data, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath + ".enc");
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "文件保存失败！");
        }
    }

    // 发送消息
    protected void sendMsg() {
        String msg = msgEditArea.getText();
        // 判断消息是否为空
        if (msg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "发送的消息不可为空！");
        } else {
            send(msgEditArea.getText().getBytes(), false); // false 表示发送的不是文件
        }
    }

    // 发送文件
    private void sendFile() {
        // 判断文件是否为空
        if (file == null) {
            JOptionPane.showMessageDialog(this, "请先选择文件！");
        } else {
            try {
                send(Files.readAllBytes(Paths.get(file.getAbsolutePath())), true); // true 表示发送的是文件
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    // 发送的所有操作，包括生成摘要、签名、加密等
    protected void send(byte[] data, boolean isFile) {
        // 首先判断是否生成了对称加密密钥
        if (key == null) {
            JOptionPane.showMessageDialog(this, "发送前需要先生成对称密钥或者接收到对方的对称密钥！");
        } else {
            if (clientSocket != null && clientSocket.isConnected()) {
                DataOutputStream dos = new DataOutputStream(out);
                try {
                    // 初始向量
                    byte[] IV = null;
                    if (mode == 1) {
                        String IVStr = vectorTextField.getText();
                        if (IVStr.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "初始向量不可为空！");
                            return;
                        } else if (IVStr.length() != keyLen * 2) {
                            JOptionPane.showMessageDialog(this, "初始向量应为" + keyLen * 2 + "位十六进制数");
                            return;
                        } else {
                            // 初始向量文本框清空，用户下一次需要重新生成初始向量（防止用户重复使用同一个初始向量发送消息）
                            IV = Convert.hexStrToBytes(IVStr);
                            vectorTextField.setText("");
                        }
                    }

                    byte[] digest, cipher, head;

                    debugTextArea.append("===========发送开始=============\n");
                    // 执行 Hash 算法生成摘要
                    if (hash == 0) {
                        digest = MD5.encrypt(data);
                        debugTextArea.append("MD5摘要：" + Convert.bytesToHexStr(digest) + "\n\n");
                    } else {
                        digest = SHA.encrypt(data);
                        debugTextArea.append("SHA-256摘要：" + Convert.bytesToHexStr(digest) + "\n\n");
                    }

                    // 摘要签名
                    byte[] sign = RSA.sign(digest, myKeyPair[0], myKeyPair[1]);
                    debugTextArea.append("对摘要进行签名：" + Convert.bytesToHexStr(sign) + "\n\n");

                    // 拼接明文消息和摘要
                    byte[] msgAndSign = new byte[data.length + sign.length];
                    System.arraycopy(data, 0, msgAndSign, 0, data.length);
                    System.arraycopy(sign, 0, msgAndSign, data.length, sign.length);

                    if (isFile) {
                        debugTextArea.append("正在加密文件和签名...\n");
                    } else {
                        debugTextArea.append("原始消息和签名拼接：" + Convert.bytesToHexStr(msgAndSign) + "\n\n");
                    }
                    // 对明文消息和摘要进行对称加密。文件可能很大，因此不展示加密结果
                    cipher = encrypt == 0 ? DES.encrypt(msgAndSign, key, IV) : AES.encrypt(msgAndSign, key, IV);
                    if (!isFile) {
                        debugTextArea.append((encrypt == 0 ? "DES" : "AES") + "加密明文和签名：" + Convert.bytesToHexStr(cipher) + "\n\n");
                    } else {
                        saveFile(cipher, file.getAbsolutePath());
                        debugTextArea.append("加密完成，已保存到相同目录下，添加扩展名.enc。\n");
                    }

                    // 如果是文件则首部 head 的 32 号字节设为1，后接文件名
                    if (isFile) {
                        head = new byte[33 + file.getName().getBytes().length];
                        head[32] = 1;
                        System.arraycopy(file.getName().getBytes(), 0, head, 33, file.getName().getBytes().length);
                    } else { // 如果不是文件则首部 head 的 32 号字节设为 0，其后没有数据
                        head = new byte[33];
                        head[32] = 0;
                    }

                    // head 首部的 0~31 字节存储消息载荷的字节长度，以便对方能够按照长度进行接收
                    Convert.intToBytes(cipher.length, head, 0, true);
                    dos.writeInt(head.length); // 发送消息首部 head 的长度
                    dos.write(head);   // 发送首部 head
                    dos.write(cipher); // 发送加密数据
                    dos.flush();

                    if (isFile) {
                        debugTextArea.append("文件已发送。\n");
                    } else {
                        debugTextArea.append("消息已发送。\n");
                        msgTextArea.append("发送：" + new String(data) + "\n");
                    }
                    byte[] key_cipher = RSA.encrypt(key, n, e); // 用对方公钥加密对称密钥
                    // 根据加密模式判断是否传输初始向量（向量不加密）
                    if (mode == 0) {
                        // 发送已加密的对称密钥的长度，以便对方接收
                        dos.writeInt(key_cipher.length);
                        dos.write(key_cipher); // 发送用对方公钥加密过的对称密钥
                        dos.flush();
                        debugTextArea.append("用对方公钥加密的对称密钥已发送。\n\n");
                    } else if (mode == 1) {
                        byte[] key_IV = new byte[key_cipher.length + keyLen];
                        System.arraycopy(key_cipher, 0, key_IV, 0, key_cipher.length);
                        System.arraycopy(IV, 0, key_IV, key_cipher.length, keyLen);
                        // 发送已加密的对称密钥的长度，以便对方接收
                        dos.writeInt(key_IV.length);
                        dos.write(key_IV);   // 发送用对方公钥加密过的对称密钥和未加密的IV
                        dos.flush();
                        debugTextArea.append("用对方公钥加密的对称密钥已发送。\n初始向量已发送，下次请使用新的初始向量。\n");
                    }
                    debugTextArea.append("===========发送结束=============\n\n");
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                } catch (Exception e) {
                    handleException(e);
                }
            } else {
                JOptionPane.showMessageDialog(this, "没有客户端连接！");
            }
        }
    }


    // 接收，另开一个线程循环监听
    protected void receive() {
        new Thread(() -> {
            DataInputStream dis = new DataInputStream(in);
            while (true) {
                int headLen;
                try {
                    headLen = dis.readInt(); // 接收首部 head 长度
                    debugTextArea.append("===========接收开始=============\n");
                    byte[] head = new byte[headLen];
                    dis.readFully(head);     // 接收首部 head
                    int contentLen = Convert.bytesToInt(head, 0, true); // 从首部获取数据长度
                    byte[] data = new byte[contentLen];
                    dis.readFully(data);     // 接收完整的数据
                    boolean isFile = head[32] > 0;
                    if (!isFile) {
                        debugTextArea.append("接收到加密的完整数据：" + Convert.bytesToHexStr(data) + "\n\n");
                    }

                    byte[] IV = null, key_cipher;         // 初始向量和 RSA 加密过的密钥
                    key = new byte[keyLen];               // 对称密钥
                    if (mode == 0) {
                        int cipherKeyLen = dis.readInt(); // 接收 RSA 加密过的对称密钥的长度
                        key_cipher = new byte[cipherKeyLen];
                        dis.readFully(key_cipher);        // 接收 RSA 加密过的对称密钥
                    } else {
                        int keyAndIVLen = dis.readInt();  // 接收 RSA 加密过的对称密钥和初始向量总长度
                        byte[] key_IV = new byte[keyAndIVLen];
                        dis.readFully(key_IV);            // 接收 RSA 加密过的对称密钥和未加密的初始向量
                        IV = new byte[keyLen];
                        key_cipher = new byte[keyAndIVLen - keyLen];
                        // 拆分加密过的密钥和未加密的初始向量，分别存入 IV 和 key_cipher
                        System.arraycopy(key_IV, keyAndIVLen - keyLen, IV, 0, keyLen);
                        System.arraycopy(key_IV, 0, key_cipher, 0, keyAndIVLen - keyLen);
                        debugTextArea.append("接收到初始向量（只用于本次解密）：" + Convert.bytesToHexStr(IV) + "\n\n");
                    }
                    byte[] tempKey = RSA.decrypt(key_cipher, myKeyPair[0], myKeyPair[1]); // 解密得到对称密钥
                    // 解决 BigInteger 转换字节数组可能出现的缺少起始的0x00或者添加了额外的0x00的情况
                    if (tempKey.length < keyLen) {
                        System.arraycopy(tempKey, 0, key, keyLen - tempKey.length, tempKey.length);
                        for (int i = 0; i < keyLen - tempKey.length; i++) {
                            key[i] = 0x00;
                        }
                    } else {
                        System.arraycopy(tempKey, tempKey.length - keyLen, key, 0, keyLen);
                    }
                    debugTextArea.append("接收到用自己公钥加密的对称密钥，解密得到：" + Convert.bytesToHexStr(key) + "，通信用的对称密钥已更新\n\n");

                    byte[] msgAndSign, newDigest;
                    // 解密，得到明文和签名的拼接结果
                    if (encrypt == 0) {
                        msgAndSign = DES.decrypt(data, key, IV);
                    } else {
                        msgAndSign = AES.decrypt(data, key, IV);
                    }

                    if (!isFile) {
                        debugTextArea.append("解密得到明文和签名：" + Convert.bytesToHexStr(msgAndSign) + "\n\n");
                    } else {
                        debugTextArea.append("解密得到文件和签名。\n\n");
                    }

                    // 拆分得到明文和签名
                    int signLen = 128;
                    byte[] msg = new byte[msgAndSign.length - signLen];
                    byte[] sign = new byte[signLen];
                    System.arraycopy(msgAndSign, msg.length, sign, 0, signLen);
                    debugTextArea.append("拆分得到签名：" + Convert.bytesToHexStr(sign) + "\n\n");

                    // 验签，得到消息摘要
                    byte[] tempDigest = RSA.verify(sign, n, e);
                    byte[] digest = new byte[hashLen];
                    if (tempDigest.length < hashLen) {
                        System.arraycopy(tempDigest, 0, digest, hashLen - tempDigest.length, tempDigest.length);
                        for (int i = 0; i < hashLen - tempDigest.length; i++) {
                            digest[i] = 0x00;
                        }
                    } else {
                        System.arraycopy(tempDigest, tempDigest.length - hashLen, digest, 0, hashLen);
                    }
                    System.arraycopy(msgAndSign, 0, msg, 0, msg.length);
                    if (hash == 0) {
                        newDigest = MD5.encrypt(msg);
                        debugTextArea.append("计算得到MD5摘要：" + Convert.bytesToHexStr(digest) + "\n\n");
                    } else {
                        newDigest = SHA.encrypt(msg);
                        debugTextArea.append("计算得到SHA-256摘要：" + Convert.bytesToHexStr(digest) + "\n\n");
                    }

                    // 判断摘要是否相同
                    if (!Arrays.equals(digest, newDigest)) {
                        JOptionPane.showMessageDialog(this, "消息被篡改！");
                    } else {
                        debugTextArea.append("消息摘要对比完成。\n");
                        // 如果是文件则保存到某一路径
                        if (isFile) {
                            String fileName = new String(head, 33, headLen - 33);
                            Files.write(Paths.get(System.getProperty("user.home") + "\\Downloads\\" + fileName), msg);
                            debugTextArea.append("文件已保存到系统的下载目录\n\n");
                        } else {
                            msgTextArea.append("接收：" + new String(msg) + "\n");
                        }
                    }
                    debugTextArea.append("===========接收结束=============\n\n");
                } catch (IOException e) {
                    break;
                }
            }
        }).start();
    }

    // 选择要发送的文件
    protected void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();     // 文件选择器
        fileChooser.setDialogTitle("请选择要发送的文件");      // 设置对话框标题
        // 设置选择模式：仅允许选择文件
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // 显示文件选择对话框，并获取用户操作返回值
        int result = fileChooser.showOpenDialog(this);
        // 判断用户是否点击“确定”
        if (result == JFileChooser.APPROVE_OPTION) {
            // 获取用户选择的文件
            file = fileChooser.getSelectedFile();
            // 在文本框中显示所选文件的绝对路径
            fileTextField.setText(file.getAbsolutePath());
        }
    }

    // 自动生成初始向量 IV
    protected void genIV() {
        byte[] IV = new byte[keyLen];     // 初始向量的长度在 DES 和 AES-128 中正好等于密钥长度（也是分组长度）
        SecureRandom rnd = new SecureRandom();
        rnd.nextBytes(IV);
        String IVStr = Convert.bytesToHexStr(IV);
        vectorTextField.setText(IVStr);
        debugTextArea.append("生成初始向量（只用于本次加密）：" + IVStr + "\n\n");
    }

    // 关闭窗口时释放各资源
    protected void closeWindow() {
        try {
            // 关闭服务器端口
            if (serverSocket != null) {
                serverSocket.close();
            }
            // 关闭客户端端口
            if (clientSocket != null) {
                clientSocket.shutdownOutput();
                clientSocket.close();
            }
            // 关闭输入流
            if (in != null)
                in.close();
            // 关闭输出流
            if (out != null)
                out.close();
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(this, "窗口关闭时出现错误");
        } finally {
            parentBtn.setEnabled(true);
            dispose(); // 释放窗口资源
        }
    }

    protected void handleException(Exception e) {

    }
}
