# 双向加密通信系统
一个基于经典密码学算法的双向加密通信应用。

本程序由java语言编写，应用Socket进行通信，并提供GUI界面。

用户可以自行选择对称加密、非对称加密和哈希算法，可以传输消息或者文件。

通信过程中生成双方各自的公私钥，将传输的文件或消息生成哈希值，使用自己的私钥签名；将消息或文件与签名拼接，使用对称密钥进行加密并发送，并且使用对方的公钥加密对称密钥也进行发送。接收方使用自己的私钥解密出对称密钥，使用对称密钥解密数据，拆分出消息或文件、签名，最后使用对方公钥进行验签。

应用将通信过程中传输数据的哈希值生成、对称加密和对称与非对称密钥等以十六进制显示在界面中以供查看。

# Bidirectional Encrypted Communication System

A bidirectional encrypted communication application based on classical cryptographic algorithms.

This program is implemented in Java, leverages Socket for network communication, and provides a graphical user interface (GUI).

Users can independently select symmetric encryption algorithms, asymmetric encryption algorithms, and hash functions, with support for both message and file transmission.

During communication, each party generates their own public-private key pair. The transmitted file or message is hashed, and the hash is signed using the sender's private key. The message or file is then concatenated with the signature, encrypted using a symmetric key, and transmitted. The symmetric key itself is separately encrypted with the recipient's public key and sent alongside. Upon receipt, the receiver decrypts the symmetric key using their own private key, uses the symmetric key to decrypt the payload, extracts the message or file and the accompanying signature, and finally verifies the signature using the sender's public key.

The application displays, in hexadecimal format, the hash values of transmitted data, the symmetric encryption details, and both the symmetric and asymmetric keys generated throughout the communication process, providing full visibility for inspection.
