package org.osgp.adapter.protocol.dlms.exceptions;

public class ImageTransferException extends Exception {

    private static final long serialVersionUID = -3723899623610781058L;

    public ImageTransferException() {
        super();
    }

    public ImageTransferException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ImageTransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageTransferException(String message) {
        super(message);
    }

    public ImageTransferException(Throwable cause) {
        super(cause);
    }

}
