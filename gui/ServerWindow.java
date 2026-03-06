package gui;

import algorithm.*;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import javax.swing.*;

public class ServerWindow extends BaseWindow {

    // 窗口构造方法
    public ServerWindow(int x, int y, int port, JButton parentBtn) {
        super(x, y, port, parentBtn);
        this.setTitle("服务端");
        setVisible(true);    // 使窗口可见
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "服务器启动失败，端口可能被占用！");
            this.closeWindow();
        }
        operation();
    }

    // 服务端初始化
    private void operation() {
        new Thread(() -> {
            debugTextArea.append("等待客户端连接……\n");
            while (true) {
                // 当没有客户端连接时如果关闭服务器窗口，会出现异常，这里进行处理
                try {
                    if (serverSocket != null) {
                        clientSocket = serverSocket.accept();
                    }
                } catch (IOException ex) {
                    System.out.println("Socket已关闭");
                    break;
                }
                try {
                    if (clientSocket != null) {
                        in = clientSocket.getInputStream();
                        out = clientSocket.getOutputStream();
                        debugTextArea.append("客户端已连接\n");

                        byte[] response = new byte[2048];
                        in.read(response);     // 接收服务端的初始化消息
                        if (response[0] != encrypt) {
                            key = null;
                            encrypt = response[0]; // 获取对称加密算法
                        }
                        mode = response[1];    // 获取对称加密的加密模式
                        hash = response[2];    // 获取 Hash 算法


                        int nLen = ((response[3] & 0xFF) << 8) | (response[4] & 0xFF);
                        n = new BigInteger(response, 5, nLen); // 获取公钥的 n
                        int eLen = ((response[5 + nLen] & 0xFF) << 8) | (response[6 + nLen] & 0xFF);
                        e = new BigInteger(response, 7 + nLen, eLen); // 获取公钥的 e

                        debugTextArea.append("接收到对方公钥：\nn：" + n + "\n\n" + "e：" + e + "\n\n");

                        myKeyPair = RSA.genKeyPair(); // 非对称密钥生成
                        debugTextArea.append("生成自己的非对称密钥：\nn：" + myKeyPair[0] + "\n\n" + "d：" + myKeyPair[1] + "\n\n" + "e："
                                + myKeyPair[2] + "\n\n");
                        byte[] bytesN = myKeyPair[0].toByteArray();
                        byte[] bytesE = myKeyPair[2].toByteArray();
                        byte[] initMsg = new byte[4 + bytesN.length + bytesE.length];  // 初始化消息包含自己的公钥

                        initMsg[0] = (byte) ((bytesN.length >> 8) & 0xFF);
                        initMsg[1] = (byte) (bytesN.length & 0xFF); // 0~1 两个字节为公钥中 n 的长度
                        System.arraycopy(bytesN, 0, initMsg, 2, bytesN.length); // 拷贝 n
                        initMsg[2 + bytesN.length] = (byte) ((bytesE.length >> 8) & 0xFF);
                        initMsg[3 + bytesN.length] = (byte) (bytesE.length & 0xFF); // 这两个字节是公钥中 e 的长度
                        System.arraycopy(bytesE, 0, initMsg, 4 + bytesN.length, bytesE.length); // 拷贝 e


                        out.write(initMsg);   // 发送自己的公钥n，e；无需发送算法（因为这里算法由服务器选定）
                        out.flush();
                        // 判断对称加密算法并设置密钥长度
                        if (encrypt == 0) {
                            keyLen = 8;
                        } else if (encrypt == 1) {
                            keyLen = 16;
                        }
                        // 判断 Hash 算法并设置摘要长度
                        if (hash == 0) {
                            hashLen = 16;
                        } else if (hash == 1) {
                            hashLen = 32;
                        }
                        // CBC 模式启用初始向量
                        if (mode == 1) {
                            vectorTextField.setEnabled(true);
                            genIVBtn.setEnabled(true);
                        } else {
                            vectorTextField.setEnabled(false);
                            genIVBtn.setEnabled(false);
                        }
                        debugTextArea.append("已完成，现在可以执行接收，或者输入种子并生成密钥以执行发送。\n\n");
                        receive();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "客户端连接失败！");
                }
            }
        }).start();
    }

    // 连接断开时重新初始化
    @Override
    protected void handleException(Exception e) {
        JOptionPane.showMessageDialog(this, "连接已断开");
        debugTextArea.append("等待客户端连接……\n");
    }
}
