package com.openmhealth.common.idal;

import com.openmhealth.common.model.Request;
import com.openmhealth.common.model.Response;

/**
 *
 */
public interface CommonController {

    Response handleRequest(Request request);
}
