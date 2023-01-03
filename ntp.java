import java.io.*;
import java.net.*;
import java.time.OffsetDateTime;

import time.Time;

class NTPClient {
    private DatagramSocket socket;          // No static attributes, each instance has its own socket
    String ntp_server;
    int listen_port;

    private static final int NTP_UDP_PORT = 123;

    NTPClient(){};
    NTPClient(String ntp_server, int listen_port)
    {
        this.ntp_server = ntp_server;
        this.listen_port = listen_port;
    }

    void init()
    {
        try {
            this.socket = new DatagramSocket(this.listen_port);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    void close() {
        this.socket.close();
    }

    private void sendPacket()
    {
        byte[] buffer = new byte[48];
        buffer[0] = 35;

        try {
            InetAddress ntp_server_ip = InetAddress.getByName(this.ntp_server);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ntp_server_ip, NTP_UDP_PORT);
            this.socket.send(packet);

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private byte[] getPacket()
    {
        byte[] buffer = new byte[48];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            this.socket.receive(packet);
        } catch (Exception e) {
            System.err.println(e);
        }

        return buffer;
    }

    private long extractTimeSecondsFromPacket(byte[] ntp_packet)
    {
        long time_seconds = 0;

        for(int i = 0; i < 4; i++)
            time_seconds |= (ntp_packet[40 + i] & 0xffL) << 24 - 8 * i;

        return time_seconds;
    }

    long getUtcTime()
    {
        this.sendPacket();
        byte[] ntp_packet = this.getPacket();

        return this.extractTimeSecondsFromPacket(ntp_packet);
    }

    long getLocalTime()
    {
        long time_utc = this.getUtcTime();
        int timezone_offset = OffsetDateTime.now().getOffset().getTotalSeconds();
        return time_utc + timezone_offset;
    }

    public static void main(String[] args)
    {
        NTPClient ntp_client = new NTPClient("es.pool.ntp.org", 25000);
        ntp_client.init();

        long local_time = ntp_client.getLocalTime();

        ntp_client.close();

        byte [] local_time_hms = Time.secondsToHMS(local_time);

        System.out.printf("%02d:%02d:%02d\n", local_time_hms[0], local_time_hms[1], local_time_hms[2]);
    }
}
