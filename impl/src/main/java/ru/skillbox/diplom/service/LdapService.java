package ru.skillbox.diplom.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.model.RefreshToken;
import ru.skillbox.diplom.model.User;
import ru.skillbox.diplom.repository.UserRepository;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Properties;

@Service
public class LdapService {

    private static DirContext connection;

    @Autowired
    private UserRepository userRepository;

    public LdapService(UserRepository userRepository) {
        this.userRepository = userRepository;
        newConnection();
    }

    @Value("${ldap.contextFactory}")
    private String contextFactory;
    @Value("${ldap.url}")
    private String url;
    @Value("${ldap.securityPrincipal}")
    private String securityPrincipal;
    @Value("${ldap.password}")
    private String password;

    public static void newConnection() {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:389");
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin, ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        try {
            connection = new InitialDirContext(env);
        } catch (AuthenticationException ex) {
            System.out.println(ex.getMessage());
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addUser(String email, String password) {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("inetOrgPerson");

        attributes.put(attribute);
        attributes.put("sn", "ab8232ea-d100-4205-b112-1515adc78ee6");
        attributes.put("userPassword", password);
        attributes.put("description", "1990-06-14T20:16:28.280425Z");
        try {
            connection.createSubcontext("cn=" + email + ",ou=users,dc=diplom,dc=skillbox,dc=ru", attributes);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean authUser(String username, String password) {
        try {
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://localhost:389");
            env.put(Context.SECURITY_PRINCIPAL, "cn=" + username + ",ou=users,dc=diplom,dc=skillbox,dc=ru");  //check the DN correctly
            env.put(Context.SECURITY_CREDENTIALS, password);
            DirContext con = new InitialDirContext(env);
            con.close();
            return true;
        } catch (Exception e) {
            System.out.println("failed: " + e.getMessage());
            return false;
        }
    }

    public void deleteUser(String userName) {
        try {
            connection.destroySubcontext("cn=" + userName + ",ou=users,dc=diplom,dc=skillbox,dc=ru");
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateUserField(String username, String fieldName, String fieldValue) {
        try {
            String dnBase = ",ou=users,dc=diplom,dc=skillbox,dc=ru";
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(fieldName, fieldValue));// if you want, then you can delete the old password and after that you can replace with new password
            connection.modifyAttributes("cn=" + username + dnBase, mods);//try to form DN dynamically
        } catch (Exception e) {
            System.out.println("failed: " + e.getMessage());
        }
    }


    public boolean searchUserField(String fieldName, String fieldValue) throws NamingException {
        String searchFilter = "(" + fieldName + "=" + fieldValue + ")";
        String[] reqAtt = {"cn", "sn", "uid"};
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        NamingEnumeration users = connection.search("ou=users,dc=diplom,dc=skillbox,dc=ru", searchFilter, controls);

        SearchResult result = null;
        result = (SearchResult) users.next();
        Attributes attr = result.getAttributes();
        String name = attr.get(fieldName).get(0).toString();
        if (name.equals(fieldValue)) {
            return true;
        } else return false;
    }

    public RefreshToken searchRefreshToken(String youAreLookingForToken) throws NamingException {
        newConnection();
        RefreshToken refreshToken = new RefreshToken();
        String searchFilter = "(sn=" + youAreLookingForToken + ")";
        String[] reqAtt = {"cn", "sn", "description"};
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        NamingEnumeration users = connection.search("ou=users,dc=diplom,dc=skillbox,dc=ru",
                searchFilter, controls);

        SearchResult result = null;

        result = (SearchResult) users.next();
        Attributes attr = result.getAttributes();
        String name = attr.get("cn").get(0).toString();

        refreshToken.setUser(userRepository.findByEmail(name).get());
        refreshToken.setUser(refreshToken.getUser());
        refreshToken.setToken(attr.get("sn").get().toString());
        refreshToken.setExpiryDate(Instant.parse(attr.get("description").get().toString()));

        return refreshToken;
    }
}