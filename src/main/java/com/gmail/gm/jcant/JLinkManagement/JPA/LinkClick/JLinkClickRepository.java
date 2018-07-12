package com.gmail.gm.jcant.JLinkManagement.JPA.LinkClick;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gmail.gm.jcant.JLinkManagement.JPA.Link.JLink;

public interface JLinkClickRepository extends JpaRepository<JLinkClick, Long> {
    
    @Query("SELECT count(lc) FROM JLinkClick lc WHERE lc.link = :link AND lc.date>=:date")
    int countLinkToDate(@Param("link") JLink link, @Param("date") Date date);
    
    @Query("SELECT count(lc) FROM JLinkClick lc WHERE lc.link = :link")
    int countLink(@Param("link") JLink link);
    
    @Query("DELETE FROM JLinkClick lc WHERE lc.link.id = :link.id")
    void deleteByLink(@Param("link") JLink link);
    
}
