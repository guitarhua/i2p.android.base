package net.i2p.httptunnel.filter;

/**
 * A generic filtering interface.
 */
public interface Filter {

    /**
     * An empty byte array.
     */
    public static final byte[] EMPTY = new byte[0];

    /**
     * Filter some data. Not all filtered data need to be returned.
     */
    public byte[] filter(byte[] toFilter);

    /**
     * Data stream has finished. Return all of the rest data.
     */
    public byte[] finish();
}
