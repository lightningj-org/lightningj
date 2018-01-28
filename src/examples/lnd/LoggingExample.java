/************************************************************************
 *                                                                       *
 *  LightningJ                                                           *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU General Public License          *
 *  License as published by the Free Software Foundation; either         *
 *  version 3 of the License, or any later version.                      *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package lnd;

import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.message.ChannelBalanceResponse;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by philip on 2018-01-19.
 */
public class LoggingExample extends ExampleUtils{

    public static void main(String[] args) throws Exception{

        // This code sets log level programmatically, this should in a real world application
        // Be done in an external log configuration. This is only to show request and response debug logging
        // when turned on.
        Handler[] handlers = Logger.getLogger(LoggingExample.class.getName()).getParent().getHandlers();
        for (Handler handler : handlers) {
            handler.setLevel(Level.FINE);
        }
        Logger logger = Logger.getLogger("org.lightningj.lnd.wrapper.API");//API.class.getName());
        logger.setLevel(Level.FINE);


        SynchronousLndAPI synchronousLndAPI = getSynchronousLndAPI();

        // Generate a request and output is written to stderr.
        ChannelBalanceResponse channelBalanceResponse = synchronousLndAPI.channelBalance();
        System.out.println("Exiting");

    }
}
