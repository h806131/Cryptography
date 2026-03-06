package gui;

import algorithm.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;

public class ClientWindow extends BaseWindow {
    private final String IPAddress;

    // 窗口构造方法
    public ClientWindow(int x, int y, int encrypt, int mode, int hash, String IP, int port, JButton parentBtn) {
        super(x, y, port, parentBtn);
        this.setTitle("客户端");
        this.encrypt = encrypt;
        this.mode = mode;
        this.hash = hash;
        this.IPAddress = IP;
        // 根据对称加密算法设置密钥长度
        if (encrypt == 0) {
            keyLen = 8;
        } else if (encrypt == 1) {
            keyLen = 16;
        }
        if (hash == 0) {
            hashLen = 16;
        } else if (hash == 1) {
            hashLen = 32;
        }
        // CBC 模式启用初始向量
        if (this.mode == 1) {
            this.vectorTextField.setEnabled(true);
            this.genIVBtn.setEnabled(true);
        }
        setVisible(true);
        operation();
    }

    // 服务器初始化
    public void operation() {
        new Thread(() -> {
            try {
                debugTextArea.append("正在连接服务器……\n");

                clientSocket = new Socket(IPAddress, port); // 连接到服务端
                in = clientSocket.getInputStream();
                out = clientSocket.getOutputStream();
                debugTextArea.append("已连接到服务器" + IPAddress + ":" + port + "\n");


                myKeyPair = RSA.genKeyPair(); // 非对称密钥生成
                debugTextArea.append("生成自己的非对称密钥：\nn：" + myKeyPair[0] + "\n\n" + "d：" + myKeyPair[1] + "\n" + "e："
                        + myKeyPair[2] + "\n\n");

                // 连接初始化，主要包括公钥的交换，以及对称加密算法、对称加密工作模式、Hash算法的选择
                byte[] bytesN = myKeyPair[0].toByteArray();
                byte[] bytesE = myKeyPair[2].toByteArray();
                byte[] initMsg = new byte[7 + bytesN.length + bytesE.length];
                initMsg[0] = (byte) encrypt; // 对称加密算法
                initMsg[1] = (byte) mode;    // 对称加密工作模式
                initMsg[2] = (byte) hash;    // Hash 算法
                initMsg[3] = (byte) ((bytesN.length >> 8) & 0xFF);
                initMsg[4] = (byte) (bytesN.length & 0xFF);    // 3~4 两字节为公钥中 n 的长度
                System.arraycopy(bytesN, 0, initMsg, 5, bytesN.length); // 拷贝 n
                initMsg[5 + bytesN.length] = (byte) ((bytesE.length >> 8) & 0xFF);
                initMsg[6 + bytesN.length] = (byte) (bytesE.length & 0xFF);      // 这两字节是公钥中 e 的长度
                // 拷贝 e
                System.arraycopy(bytesE, 0, initMsg, 7 + bytesN.length, bytesE.length);

                out.write(initMsg); // 将上述初始化信息发送给客户端
                out.flush();

                byte[] response = new byte[2048];
                in.read(response);  // 等待接收客户端的公钥
                int nLen = ((response[0] & 0xFF) << 8) | (response[1] & 0xFF);  // 从 0~1 字节计算n的长度
                n = new BigInteger(response, 2, nLen);                      // n 从字节转换为大整数
                // 同上，对 e 进行计算
                int eLen = ((response[2 + nLen] & 0xFF) << 8) | (response[3 + nLen] & 0xFF);
                e = new BigInteger(response, 4 + nLen, eLen);
                debugTextArea.append("接收到对方公钥：\nn：" + n + "\n\n" + "e：" + e + "\n");
                debugTextArea.append("已完成，现在可以执行接收，或者输入种子并生成密钥以执行发送。\n\n");
                receive();
            } catch (IOException e) {
                int opt = myMadelDialog();
                if (opt == JOptionPane.YES_OPTION) {
                    handleException(e);
                } else {
                    closeWindow();
                }
            }
        }).start();
    }

    // 连接断开时重新初始化
    @Override
    protected void handleException(Exception e) {
        int opt = JOptionPane.showConfirmDialog(this, "连接已断开，是否重新连接？", "重新连接", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            operation();
        }
    }

    // 只阻塞客户端窗口的模态对话框（主窗口仍可以点击）
    private int myMadelDialog() {
        // 创建 JOptionPane
        JOptionPane optionPane = new JOptionPane(
                "连接服务端失败！请确认服务端已启动。\n是否重新连接？\n选择否将关闭客户端窗口。",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION
        );

        // 创建对话框，并设置为只阻塞当前窗口
        JDialog dialog = optionPane.createDialog(this, "重新连接");
        dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 显示并等待关闭
        dialog.setVisible(true);

        // 获取返回值
        Object selectedValue = optionPane.getValue();
        if (selectedValue == null)
            return JOptionPane.CLOSED_OPTION;
        if (selectedValue instanceof Integer)
            return (Integer) selectedValue;
        return JOptionPane.CLOSED_OPTION;
    }
}
