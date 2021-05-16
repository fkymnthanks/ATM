package com.atm.pay.services;

import com.atm.pay.model.Cash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.atm.pay.services.CashBox.cashBox;
import static javax.ws.rs.core.Response.Status;

@Path("/monitor")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class);

    @GET
    @Path("/money")
    public List<Cash> money() {
        LOGGER.info("Retrieving available money...");
        try {
            return cashBox().getMoney();
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
    }
}

