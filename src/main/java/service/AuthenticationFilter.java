package service;


import java.lang.reflect.Method;
import java.util.List;
 
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
 
import org.glassfish.jersey.server.mvc.Viewable;

 
/**
 * This filter verify the access permissions for a user
 * based on username and passowrd provided in request
 * */
@Provider
public class AuthenticationFilter implements ContainerRequestFilter
{
     
    @Context
    private ResourceInfo resourceInfo;
     
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
                                                        .entity("You cannot access this resource").build();
    private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
                                                        .entity("Access blocked for all users !!").build();
      
    @Override
    public void filter(ContainerRequestContext requestContext)
    {
  	
    	System.out.println(this.getClass().getName() + ":: filtering ...");
    	System.out.println(this.getClass().getName() + ":: " + requestContext.getMethod().toString() + 
    														" " + requestContext.getUriInfo().getRequestUri());
    	Method method = resourceInfo.getResourceMethod();
    	    	
        //Access allowed for all
        if( ! method.isAnnotationPresent(PermitAll.class))
        {
            //Access denied for all
            if(method.isAnnotationPresent(DenyAll.class))
            {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }
            
            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
              
            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            
            //If no authorization information present; block access
            if(method.getName().equals("login") && (authorization == null || authorization.isEmpty()))
            {
            	System.out.println(this.getClass().getName() + ":: authorization is null or Empty");
            	requestContext.abortWith(ACCESS_DENIED);
                return;
            }
            
            boolean found = true;
            
            for (Cookie c : requestContext.getCookies().values()) 
            {
                if (c.getName().equals("tokenG5")) {
                	found = true;
                    break;
                }
            }
            
        	if(method.getName().equals("index") && !found)
        	{
        		System.out.println(this.getClass().getName() + ":: Return to login page");
        		Viewable view = new Viewable("/jsp/logon");
        		
        		requestContext.abortWith(Response.ok(view).build());
        		return;
        	}       
                   
            
//            if(!found) {
//            	System.out.println(this.getClass().getName() + ":: Return to login page");
//        		requestContext.abortWith(Response.ok(new Viewable("/jsp/logon", null)).build());
//        		return;
//            }
                             	   
   
//            if(method.getName().equals("index"))
//    		{
//            	System.out.println(this.getClass().getName() + ":: index");
//            	
//            	// TODO: Guardar Usuario / Token en un DAO
//            	
//            	//Get encoded username and password
//                final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
//                  
//                //Decode username and password
//                String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));;
//      
//                //Split username and password tokens
//                final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
//                final String username = tokenizer.nextToken();
//                final String password = tokenizer.nextToken();
//                  
//                //Verifying Username and password
//                System.out.println(this.getClass().getName() + ":: username = " + username);
//                System.out.println(this.getClass().getName() + ":: password = " + password);
//                  
//                //Verify user access
//                if(method.isAnnotationPresent(RolesAllowed.class))
//                {
//                	System.out.println(this.getClass().getName() + ":: RolesAllowed present");
//                	
//                	RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
//                    Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
//                      
//                    //Is user valid?
//                    if( ! LoginService.isUserAllowed(username, password, rolesSet))
//                    {
//                        requestContext.abortWith(ACCESS_DENIED);
//                        return;
//                    }
//                }
//    	
//    		}
            
        }
    }

}