package com.gmail.gm.jcant.JLinkManagement.JPA.User;

import java.util.List;

public interface JUserService {
	
	List<JUser> getAllUsers();
	List<JUser> getUsersByRole(JUserRole role);
    JUser getUserById (long id) throws JUserException;
	JUser getUserByLogin(String login) throws JUserException;
    
	boolean existsByLogin(String login);
    boolean existsById(long id);
    
    void addUser(JUser user);
    
    void updateUser(JUser user);

    void deleteUser(long id);
}
