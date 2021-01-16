package codigo;

import java.io.*;
import java.net.*;

class JavaNTP {

    DatagramSocket socket;          // Atributos no estáticos, cada instancia tendra su propio socket (conexión)
    String dst_address;
    int src_port;
    int dst_port;

    JavaNTP(){};
    JavaNTP(String dst_address, int src_port, int dst_port)
    {
        this.dst_address = dst_address;
        this.src_port = src_port;
        this.dst_port = dst_port;
    }

    void InicializaSocket()
    {
        try {
            socket = new DatagramSocket(src_port);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    void CierraSocket() { socket.close(); }

    void enviarPaqueteNTP()
    {
        byte[] buffer = new byte[48];
        buffer[0] = 35;

        try {
            InetAddress ip_dst_address = InetAddress.getByName(dst_address);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ip_dst_address, dst_port);
            socket.send(packet);

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    byte[] recibirPaqueteNTP()
    {
        byte[] buffer = new byte[48];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(packet);
        } catch (Exception e) {
            System.err.println(e);
        }

        return buffer;
    }

    static long deArrayCuatroBytesALong(byte[] vector)
    {
        long tiempo = 0;

        for(int i = 0; i < 4; i++)
            tiempo |= (vector[40 + i] & 0xffL) << 24 - 8 * i;
        
        return tiempo;
    }

    static byte[] deTLongAHMS(long t, short UTC)
    {
        byte[] hms = new byte[3];
        t += UTC * 3600; 
        hms[0] = (byte)(((t%86400)/3600));
        hms[1] = (byte)((t%3600)/60);
        hms[2] = (byte)(t%60);

        return hms;
    }

    public static void main(String[] args)
    {
        short UTC = 1;
        JavaNTP clienteNTP = new JavaNTP("130.206.3.166", 1500, 123); // Creamos nuestra instancia.
        clienteNTP.InicializaSocket();
        clienteNTP.enviarPaqueteNTP();
        byte[] message = clienteNTP.recibirPaqueteNTP();

        long tiempo = deArrayCuatroBytesALong(message);
        byte [] hms = deTLongAHMS(tiempo, UTC);

        /* Mostramos por pantalla. */
        System.out.printf("%02d:%02d:%02d\n", hms[0] & 0xFF, hms[1] & 0xFF, hms[2] & 0xFF);
        clienteNTP.CierraSocket();
    }

}
