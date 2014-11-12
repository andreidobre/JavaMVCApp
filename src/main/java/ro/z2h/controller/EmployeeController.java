package ro.z2h.controller;

import ro.z2h.annotation.MyController;
import ro.z2h.annotation.MyRequestMethod;

/**
 * Created by Dumitru on 11.11.2014.
 */
@MyController(urlPath = "/employee")
public class EmployeeController {

    //TODO
    @MyRequestMethod(urlPath = "/all")
    public String getAllEmployees() {

        return "allEmployees";
    }

    @MyRequestMethod(urlPath = "/one")
    public String getOneEmployee() {

        return "oneRandomEmployee";
    }
}