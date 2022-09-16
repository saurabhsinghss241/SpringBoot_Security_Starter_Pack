# SpringBoot_Security_Starter_Pack<br><br>

# JWT Implementation<br>

## Theory <br>

What is JWT?<br>
Json Web Token is a way to authorize requests without managing states or sessions.<br><br>

How we achieve this?<br>
We achieve this stateless behaviour by providing a token to the user containing all the information.<br>
User can send this token in subsquent request and based on this token we will authorize the user.<br>

Problem -<br>
Anyone can view the content of these token 😭 using a base64 decoder.<br>
So how can we ensure that the user that is sending the token with the given information is the real user.<br>

Solution -<br>
All these token are signed by a Secret Key which only the server knows that is issuing these tokens.<br>
Suppose someone finds out the data from the token and trie's to send another token with same content but then he will have to encode the information using a Secret Key which will not with our server key.<br>
So our server will know that this token is tampered.<br><br>

JWT is used for authorization and you need to handle authentication on your.<br><br>

Workflow<br>
Authentication - Will be done using username and password.<br>
Authorization - Will be done using JWT.<br>

Authenticate a user using username password and return a JWT.<br>
Subsquent request will send this Token in Authorization Header and based on this token requests will be authorized.<br>

Implementation<br>
First we will handle Authentication.<br>
We will create a controller with endpoint "/api/auth/token" this will authenticate the user based on username and password provided by user.<br>
Then Authorization for this we will create a filter that will intercept all incomming request and check if the Authorization header contains a valid token or not.<br><br>

Lets Implement Authentication<br>
We discussed earlier the Authentication workflow in spring security.<br>
Key Components <br>
Authentication obj<br>
AuthenticationManager<br>
AuthenticationProvider<br>
UserDetailsService<br><br>

We have already implemented UserDetailsService, AuthenticationProvider and in default flow we only need to provide these two and internally Spring uses AuthenticationManager to authenticate a user.<br>
But now we have to manually do it.<br>
So we will need AuthenticationManager and Authentication.<br><br>

How to get AuthenticationManager?<br>
We can get this from WebSecurityConfigurerAdapter class we have to override an existing method authenticationManagerBean this will return AuthenticationManager.<br>
Make sure to annotate this method with @Bean because we need this in our token generator controller.<br>

```sh
@Override
@Bean
public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
}
```
<br>
Ok now we have AuthenticationManager now we need an instance Authentication because to authenticate AuthenticationManager needs an instance of Authentication containing user credentials.<br><br>

Authentication is an interface so we have to use an implementation of this.<br>
We are going to use UsernamePasswordAuthenticationToken.<br>
Create an instance of UsernamePasswordAuthenticationToken
and add credentials in it.<br>

```sh
new UsernamePasswordAuthenticationToken(
        jwtRequest.getUsername(),
        jwtRequest.getPassword()
)
```
<br>
jwtRequest is a DTO for capturing username, password from the client.<br>

Now we will pass this instance of Authentication to AuthenticationManager.<br>

```sh
authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
                jwtRequest.getUsername(),
                jwtRequest.getPassword()
        )
);
```
<br>
We have already provided the AuthenticationProvider and UserDetailsService in our SecurityConfiguration.<br>

AuthenticationManager will use those to authenticate our user.<br>
If credentials are invalid an exception of type BadCredentialsException<br>
will be thrown we need to handle this.<br>
```sh
catch (BadCredentialsException ex){
    // This will be triggered if authentication fails.
    // Username or Password is incorrect.
    throw new Exception("User Credentials are incorrect.");
}
```
<br>
If cred are correct user is authenticated now we need to create a token for this user so that he for subsquent request he can use the token to authorize.<br>

Let's do this <br>
To generate token we will use JWTUtil class containg methods to generate and validate JWT.<br><br>

We will use generate method of this which takes UserDetails.<br>
Get UserDetails using username that we recieved from user in requeset body.<br>
We will get this token send this back to user.<br>

Authentication Done.<br>

## Now we have to handle authorization.

But we have already implemented this in BasicJPA flow using HttpSecurity filter and antMatchers.<br>
But there is an issue.<br>
To authorize incoming request we are checking the roles of currently logged in user.<br>
And this authority(roles or permissions) info is saved in Authentication object stored in SecurityContext.<br>
In BasicJPA flow after authenticaiton AuthenticationManager saves prinipal info in Authentication Object and saves it into SecurityContext.<br>
And because of this SecurityContext subsquent request don't need to authenticate because Authentication obj is already present.<br>

But JWT is stateless means we don't maintain session so as soon as we returned the token info after authentication Authentication Object is cleared from SecurityContext.<br>
```sh
.and()
.sessionManagement()
.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
```
When we send next request we again have to authenticate.<br>
To solve this issue we will check the token validity can populate the SecurityContext with an Authentication object.<br><br>

**WorkFlow**<br>
- Create a JWTFilter.<br>
- Extract JWT from incomming request.<br>
- Validate<br>
- Create an Authentication obj using the user info from token and UserDetailsService.<br>
- Save this Authentication obj with principal into SecurityContext.<br>

## Step 1 - Create a filter to intercept JWT in incomming request.

- We will create JWTFilter class that extends OncePerRequestFilter class and override doFilterInternal() method.<br>
- Now inside doFilterInternal() check for a token in authorization header and extract it.<br>
```sh
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            token = authorizationHeader.substring(7);
            username = jwtUtility.getUsernameFromToken(token);
        }

```
- Check if token is valid or not using JWTUtility validateToken() method.<br>
- But this validateToken method needs token and UserDetails as paramter.<br>
- Get the UserDetails using username extracted from token and UserDetailsService.<br>
- Validate.<br>
- If successful.<br>
- Now we have to save an object of Authentication with principal information.<br>
(In BasicAuth flow this was done by Spring internally after user is successfully authenticated.)<br>
- In our case we know that user is authenticated based on token validity so don't need to authenticate again.<br>
- We will directly set Authentication Obj with Princaipal in SecurityContext.<br>

- Authentication is an interface so we will need an implementation to create an object.<br>
- We will use UsernamePasswordAuthenticationToken class this is the default implementation of Authentication interface.<br>
- UsernamePasswordAuthenticationToken has 3 Parameters principal, credentials and authorities.<br>
- Prinipal = UserDetails<br>
- Credentials = null because we have already authenticated the user so we don't need the credentials.<br>
- Authorities = this we can get from UserDetails getAuthorities() method.<br>
- Authentication object created.<br>
```sh
  UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
          new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

  // Storing additional information related to this request.
  // Like ip,session-info etc. which we can refer in future based on our needs.
  // Without this also everything will work fine.
  usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

```
Now save this in SecurityContext this can be done using SecurityContextHolder.<br>
```sh
SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
```
Get current Context and set Authentication Object.<br><br>

Our Custom Filter job is done now we want this request to continue in the filterchain so that HttpSecurity filter intercept this and authorize the request based on authority of currently authenticated user (which will be fetched from SecurityContext).<br>
To send this request down the filterchain.<br>
```sh
filterChain.doFilter(request,response);
```

Almost Done.<br><br>

Only 1 issue remains we have to make sure our custom filter intercept the request before the UsernamePasswordAuthenticationFilter in HttpSecurity so that we can set the SecurityContext and then UsernamePasswordAuthenticationFilter authorize our request.<br>

To solve this issue go to HttpSecurity
```sh
http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```
UsernamePasswordAuthenticationFilter - is the filter that does the authorization.






