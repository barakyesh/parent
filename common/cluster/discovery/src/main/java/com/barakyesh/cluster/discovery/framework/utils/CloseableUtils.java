package com.barakyesh.cluster.discovery.framework.utils;

import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Barak Yeshoua.
 */
public class CloseableUtils{
    private static final Logger log = LoggerFactory.getLogger(CloseableUtils.class);

    public static void closeQuietly(Closeable closeable) {
        try {
            Closeables.close(closeable,true);
        } catch (IOException e) {
            log.error("IOException while trying to close resource quietly", e);
        }
    }
}
