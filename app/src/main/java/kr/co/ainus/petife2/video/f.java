package kr.co.ainus.petife2.video;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

class f {
    DatagramSocket a;

    f(int var1) {
        try {
            this.a = new DatagramSocket(var1);
        } catch (SocketException var3) {
        }

    }

    boolean a(String var1, int var2) {
        boolean var3 = false;

        try {
            InetSocketAddress var4 = new InetSocketAddress(var1, var2);
            this.a.connect(var4);
            var3 = true;
        } catch (SocketException var5) {
        }

        return var3;
    }

    boolean a(String var1, byte[] var2, int var3) {
        boolean var4 = false;
        DatagramPacket var5 = new DatagramPacket(var2, var2.length);

        try {
            var5.setAddress(InetAddress.getByName(var1));
            var5.setPort(var3);
            this.a.send(var5);
            var4 = true;
        } catch (IOException var7) {
        }

        return var4;
    }

    boolean a(byte[] var1, int var2) {
        boolean var3 = false;
        DatagramPacket var4 = new DatagramPacket(var1, var2);

        try {
            this.a.send(var4);
            var3 = true;
        } catch (IOException var6) {
        }

        return var3;
    }

    boolean a(int var1) {
        boolean var2 = false;

        try {
            this.a.setSoTimeout(var1);
            var2 = true;
        } catch (SocketException var4) {
        }

        return var2;
    }

    byte[] a() {
        byte[] var1 = null;
        DatagramPacket var2 = new DatagramPacket(new byte[1200], 1200);

        try {
            this.a.receive(var2);
            if (var2.getLength() > 0) {
                var1 = new byte[var2.getLength()];
                System.arraycopy(var2.getData(), 0, var1, 0, var1.length);
            }
        } catch (IOException var4) {
        }

        return var1;
    }

    void b() {
        if (this.a != null && !this.a.isClosed()) {
            this.a.close();
        }

    }
}
