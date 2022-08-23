package account.utils;

import org.slf4j.Marker;

public class MarkerFactory {
    Marker 	CREATE_USER = org.slf4j.MarkerFactory.getMarker("CREATE_USER");
    Marker CHANGE_PASSWORD = org.slf4j.MarkerFactory.getMarker("CHANGE_PASSWORD");
    Marker ACCESS_DENIED = org.slf4j.MarkerFactory.getMarker("ACCESS_DENIED");
    Marker 	LOGIN_FAILED = org.slf4j.MarkerFactory.getMarker("LOGIN_FAILED");
    Marker 	GRANT_ROLE = org.slf4j.MarkerFactory.getMarker("GRANT_ROLE");
    Marker 	REMOVE_ROLE = org.slf4j.MarkerFactory.getMarker("REMOVE_ROLE");
    Marker 	LOCK_USER = org.slf4j.MarkerFactory.getMarker("LOCK_USER");
    Marker UNLOCK_USER = org.slf4j.MarkerFactory.getMarker("UNLOCK_USER");
    Marker DELETE_USER = org.slf4j.MarkerFactory.getMarker("DELETE_USER");
    Marker BRUTE_FORCE = org.slf4j.MarkerFactory.getMarker("BRUTE_FORCE");



}
