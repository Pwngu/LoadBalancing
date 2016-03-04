package at.tgm.ablkreim.common.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Connection {

    private static final Logger LOGGER = LogManager.getLogger("at.tgm.ablkreim.common.Connection");


    private final Lock readLock = new ReentrantLock();


    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Socket socket;

    private String name;

    private volatile boolean isClosed;

    /**
     * Creates a new connection with the given socket and identified by the given name.
     *
     * @param socket the socket of the connection
     * @param name name to identify this connection
     */
    public Connection(Socket socket, String name) {

        try {

            this.socket = socket;

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            this.name = name;
        } catch(Exception ex) {

            throw new RuntimeException("Couldn't instantiate Connection", ex);
        }
    }

    @SuppressWarnings("unchecked")
    /**
     * Receives a Object.
     *
     * If the received object cannot be cast, null is returned.
     * @return the received Object correctly casted
     */
    public <T> T receive() {

        LOGGER.debug(this.toString() + " started receiving");

        readLock.lock();
        try {
            Object obj = in.readObject();
            LOGGER.debug("{} received \"{}\"", this, obj);

            try {

                return (T) obj;
            } catch(ClassCastException ex) {

                LOGGER.debug("{} received wrong object: {}", this, obj.getClass());
                return null;
            }
        } catch(EOFException ex) {

            LOGGER.debug("{} receiving ended with EOF-Ex", this);
            return null;
        } catch(SocketException ex) {

            LOGGER.debug("{} receiving ended with Socket-Ex", this);
            return null;
        } catch(Exception ex) {

            LOGGER.error("Exception in {} whilst receiving", this, ex);
            return null;
        } finally {

            readLock.unlock();
        }
    }

    /**
     * Sends the given Object.
     *
     * @param obj Object to send
     * @return whether the sending was successful
     */
    public boolean send(Serializable obj) {

        LOGGER.debug("{} sending \"{}\"", this, obj);

        try {

            out.writeObject(obj);
            out.flush();
            return true;
        } catch(Exception ex) {

            return false;
        }
    }

    /**
     * Send the given byte.
     *
     * @param data the byte to send
     * @return whether the sending was successful
     */
    public boolean send(byte data) {

        LOGGER.debug("{} sending Byte \"{}\"", data);

        try {

            out.writeByte(data);
            out.flush();
            return true;
        } catch(Exception ex) {

            LOGGER.debug("{} while sending byte");
            return false;
        }
    }

    /**
     * Closes this connection.
     */
    public void close() {

        if(isClosed) return;

        LOGGER.info("{} closing...", this);

        isClosed = true;

        try {

            out.close();
            in.close();
            socket.close();
        } catch(Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    /**
     * Returns a Future object for the received data.
     *
     * @return a Future object for the received data
     */
    public <T> Future<T> getAcknowledge() {

        return Executors.newCachedThreadPool().submit(() -> {

            Object obj = receive();
            try {

                return (T) obj;
            } catch(ClassCastException ex) {

                throw new Exception(ex);
            }
        });
    }

    public String toString() {

        return "Connection" + (name == null ? "" : " " + name) + ": " +
                socket.getLocalAddress().toString() + "<>" + socket.getInetAddress().toString();
    }

    /**
     * Returns whether this Connection is closed.
     *
     * @return true when Connection is closed.
     */
    public boolean isClosed() {

        return isClosed;
    }

    /**
     * Returns the socket of this Connection.
     *
     * @return the socket of this Connection
     */
    public Socket getSocket() {

        return socket;
    }

    /**
     * Returns the local address of this Connection.
     *
     * @return the local ip address.
     */
    public InetAddress getLocalAddress() {

        return socket.getLocalAddress();
    }

    /**
     * Returns the remote address of this Connection.
     *
     * @return the rmeote ip address
     */
    public InetAddress getRemoteAddress() {

        return socket.getInetAddress();
    }

    /**
     * Returns the name of this Connection.
     *
     * @return the name of this Connection
     */
    public String getName() {

        return name;
    }

}