package ru.skillbox.diplom.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.model.RefreshToken;
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
    private static final String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String url = "ldap://localhost:389";
    private static final String securityPrincipal = "cn=admin,dc=diplom,dc=skillbox,dc=ru";
    private static final String usersPrincipal = "ou=users,dc=diplom,dc=skillbox,dc=ru";
    private static final String password = "group19";
    private static final String ldapEmailFieldName = "cn";
    private static final String ldapPasswordFieldName = "userPassword";
    private static final String ldapRefreshTokenFieldName = "refreshToken";
    private static final String ldapRefreshTokenExpiryDateFieldName = "expiryDate";

    @Autowired
    private UserRepository userRepository;

    public LdapService(UserRepository userRepository) {
        this.userRepository = userRepository;
        newConnection();
    }

    public static void newConnection() {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, password);
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
        attribute.add("Skillbox");

        attributes.put(attribute);
        attributes.put(ldapRefreshTokenExpiryDateFieldName, "1990-06-14T20:16:28.280425Z");
        attributes.put("sn", email); //Нет возоможности не создавать это поле, не позволяет ldap. Поэтому создается в качестве заглушки.
        attributes.put(ldapRefreshTokenFieldName, "999999999");
        attributes.put(ldapPasswordFieldName, password);
        try {
            connection.createSubcontext(ldapEmailFieldName + "=" + email + "," + usersPrincipal, attributes);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean authUser(String username, String password) {
        try {
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
            env.put(Context.PROVIDER_URL, url);
            env.put(Context.SECURITY_PRINCIPAL, ldapEmailFieldName + "=" + username + "," + usersPrincipal);
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
            connection.destroySubcontext(ldapEmailFieldName + "=" + userName + "," + usersPrincipal);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateUserField(String username, String fieldName, String fieldValue) {
        try {
            String dnBase = "," + usersPrincipal;
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(fieldName, fieldValue));
            connection.modifyAttributes(ldapEmailFieldName + "=" + username + dnBase, mods);
        } catch (Exception e) {
            System.out.println("failed: " + e.getMessage());
        }
    }


    public boolean searchUserField(String fieldName, String fieldValue) throws NamingException {
        String searchFilter = "(" + fieldName + "=" + fieldValue + ")";
        String[] reqAtt = {ldapEmailFieldName, ldapPasswordFieldName,
                ldapRefreshTokenFieldName, ldapRefreshTokenExpiryDateFieldName};
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        NamingEnumeration users = connection.search(usersPrincipal, searchFilter, controls);

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
        String searchFilter = "(" + ldapRefreshTokenFieldName + "=" + youAreLookingForToken + ")";
        String[] reqAtt = {ldapEmailFieldName, ldapRefreshTokenFieldName, ldapRefreshTokenExpiryDateFieldName};
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        NamingEnumeration users = connection.search(usersPrincipal, searchFilter, controls);

        SearchResult result = null;

        result = (SearchResult) users.next();
        Attributes attr = result.getAttributes();
        String name = attr.get(ldapEmailFieldName).get(0).toString();

        refreshToken.setUser(userRepository.findByEmail(name).get());
        refreshToken.setUser(refreshToken.getUser());
        refreshToken.setToken(attr.get(ldapRefreshTokenFieldName).get().toString());
        refreshToken.setExpiryDate(Instant.parse(attr.get(ldapRefreshTokenExpiryDateFieldName).get().toString()));

        return refreshToken;
    }
}