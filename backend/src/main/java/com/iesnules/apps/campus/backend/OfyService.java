package com.iesnules.apps.campus.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.iesnules.apps.campus.backend.model.RegistrationRecord;
import com.iesnules.apps.campus.backend.model.UserRecord;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 *
 */
public class OfyService {

    static {
        ObjectifyService.register(RegistrationRecord.class);
        ObjectifyService.register(UserRecord.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
