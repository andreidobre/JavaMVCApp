package ro.z2h;

import ro.z2h.annotation.MyController;
import ro.z2h.annotation.MyRequestMethod;
import ro.z2h.fmk.AnnotationScanUtils;
import ro.z2h.fmk.MethodAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * Created by Dumitru on 11.11.2014.
 */
public class MyDispatcherServlet extends HttpServlet {

    HashMap<String, MethodAttributes> containerControlers = new HashMap<String, MethodAttributes>();

    @Override
    public void init() throws ServletException {
        /* Initialize controller pool. */
        try {
            Iterable<Class> classes = AnnotationScanUtils.getClasses("ro.z2h.controller");
            System.out.println(classes.toString());




            for (Class aClass : classes) {
                if (aClass.isAnnotationPresent(MyController.class)) {
                    MyController mc = (MyController) aClass.getAnnotation(MyController.class);
                    System.out.println(mc.urlPath());

                    Method[] methods1 = aClass.getMethods();
                    for (Method method : methods1) {
                        if (method.isAnnotationPresent(MyRequestMethod.class)) {
                            MyRequestMethod rm = (MyRequestMethod) method.getAnnotation(MyRequestMethod.class);

                            System.out.println("url pe care il poate accesa o metoda: " + rm.urlPath());
                            System.out.println("tipul metodei: " + rm.methodType());               // url.Path si rm.methodType le iau din annotation pentru requestMethod
                            String urlKey = mc.urlPath() + rm.urlPath();

                            MethodAttributes valori = new MethodAttributes();
                            valori.setControllerClass(aClass.getName());
                            valori.setMethodName(method.getName());
                            valori.setMethodType(rm.methodType());

                            containerControlers.put(urlKey, valori);

                            System.out.println("hashmap-ul: " + containerControlers);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatchReply("GET", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatchReply("POST", req, resp);
    }

    private void dispatchReply(String httpMethod, HttpServletRequest req, HttpServletResponse resp) {
        //TODO
        /*Dispatch*/
        Object objDispatch = dispatch(httpMethod, req, resp);

        /*Reply*/
        try {
            reply(objDispatch, req, resp);
        } catch (IOException e) {
            e.printStackTrace();
            sendException(e, req, resp);
        }

        /*Catch errors*/
        Exception ex = null;

    }

    /* Where an application controller should be called. */
    private Object dispatch(String httpMethod, HttpServletRequest req, HttpServletResponse resp) {
        //TODO
        /* pentru /test = Hello! */
        /* pentru /employee = allEmployees de la app controller */

        String pathInfo = req.getPathInfo();


//        if(pathInfo.startsWith("/employees")) {
//            EmployeeController ec = new EmployeeController();
//           return ec.getAllEmployees();
//        } else if(pathInfo.startsWith("/department")) {
//            DepartmentController dc = new DepartmentController();
//            return dc.getAllDepartments();
//        }

        MethodAttributes val = containerControlers.get(pathInfo);
        if(val != null){
            try {
                Class<?> controllerClass = Class.forName(val.getControllerClass());
                Object controllerInstance = controllerClass.newInstance();
                Method method = controllerClass.getMethod(val.getMethodName());
                Object responseProcesare = method.invoke(controllerInstance);
                return responseProcesare;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return "  - ";

    }

    /* Used to send the view to the client. */
    private void reply(Object objDispatch, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //TODO
        PrintWriter writer = resp.getWriter();
        writer.printf(objDispatch.toString());
    }

    /* Used to send an exception to the client. */
    private void sendException(Exception ex, HttpServletRequest req, HttpServletResponse resp) {
        //TODO
        ex.printStackTrace();
        System.out.println(ex.getMessage() );//"There was an exception!");
    }
}
