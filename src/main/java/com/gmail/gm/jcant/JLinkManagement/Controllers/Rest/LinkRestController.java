package com.gmail.gm.jcant.JLinkManagement.Controllers.Rest;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.naming.LinkException;

import com.gmail.gm.jcant.JLinkManagement.Helpers.JRoleHelper;
import com.gmail.gm.jcant.JLinkManagement.Helpers.JSymbolsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gmail.gm.jcant.JDate;
import com.gmail.gm.jcant.JLinkManagement.DomainRouting.JDomain;
import com.gmail.gm.jcant.JLinkManagement.JPA.JOperationInfo;
import com.gmail.gm.jcant.JLinkManagement.JPA.Link.JLink;
import com.gmail.gm.jcant.JLinkManagement.JPA.Link.JLinkException;
import com.gmail.gm.jcant.JLinkManagement.JPA.Link.JLinkService;
import com.gmail.gm.jcant.JLinkManagement.JPA.RootLink.JRootLink;
import com.gmail.gm.jcant.JLinkManagement.JPA.RootLink.JRootLinkService;
import com.gmail.gm.jcant.JLinkManagement.JPA.User.JUser;
import com.gmail.gm.jcant.JLinkManagement.JPA.User.JUserException;
import com.gmail.gm.jcant.JLinkManagement.JPA.User.JUserService;

@RestController
public class LinkRestController {

	@Autowired
	private JLinkService linkService;
	@Autowired
	private JRootLinkService rootLinkService;
	@Autowired
	private JUserService userService;

	@Value("${urlRandomStringLenght}")
	int randomStringLenght;

	// get user Links (Free/Paid/All)
	@RequestMapping(value = "/link/{user}/{type}")
	@JDomain(property = "frontend.domains")
	public List<JLink> getLinks(@PathVariable(value = "user") String user, @PathVariable(value = "type") String type,
								@RequestParam boolean archive) throws JUserException {

		JUser dbUser = userService.getUserByLogin(user);
		User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if ((!JRoleHelper.isHasRole("ROLE_ADMIN", authUser.getAuthorities())) && (!authUser.getUsername().equals(user))) {
			throw new JUserException("Access deny to get links for another user");
		}
		Date date = null;
		if (!archive){
			date = new Date();
		}
		List<JLink> result = null;
		switch (type) {
			case "free":
				result = linkService.findByUserFree(dbUser, date);
				break;
			case "paid":
				result = linkService.findByUserPaid(dbUser, date);
				break;
			case "all":
				result = linkService.findByUserAll(dbUser, date);
				break;
		}

		return result;
	}

	// update Link "target"
	@RequestMapping(value = "/link/{id}", method = RequestMethod.POST)
	@JDomain(property = "frontend.domains")
	public ResponseEntity<Void> updateLink(@PathVariable(value = "id") long id, @RequestParam String target,
			@RequestParam boolean enabled) throws JUserException, JLinkException {

		User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		JLink link = linkService.findById(id);

		if ((!JRoleHelper.isHasRole("ROLE_ADMIN", authUser.getAuthorities()))
				&& (!authUser.getUsername().equals(link.getUser().getLogin()))) {
			throw new JUserException("Access deny to change link of another user");
		}

		link.setTarget(target);
		link.setEnabled(enabled);
		linkService.updateLink(link);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	// check is Link free
	@RequestMapping(value = "/link/check", method = RequestMethod.POST)
	@JDomain(property = "frontend.domains")
	public boolean isFree(@RequestParam String url) {
		return linkService.isFreeByUrl(url);
	}

	// add new Link
	@RequestMapping(value = "/link/addfree", method = RequestMethod.POST)
	@JDomain(property = "frontend.domains")
	public JOperationInfo<JLink> addLink(@RequestParam String rootUrl, @RequestParam String target, Principal principal)
			throws LinkException, JUserException {
		JRootLink rootLink = rootLinkService.getRootLinkByUrl(rootUrl);
		if (rootLink == null) {
			throw new LinkException("Can't create new Link: base URL is invalid!");
		}
		if (JSymbolsHelper.isContainSpecialSymbols(target, false)) {
			throw new LinkException("Can't create new Link: selected parameters contain special symbols!");
		}

		JLink link = new JLink(null, target, userService.getUserByLogin(principal.getName()), new Date(), null);

		link.setUrl(generateRandLink(rootUrl));
		link.setFree(true);
		link.setFinishDate(JDate.incMonth(link.getStartDate(), 1));

		if (!linkService.isFreeByUrl(link.getUrl())) {
			throw new LinkException("Can't create new Link: this URL is busy!");
		}

		linkService.addLink(link);
		return new JOperationInfo<JLink>("FreeLink add success!", true);
	}
	
	@RequestMapping(value = "/link/{id}", method = RequestMethod.DELETE)
    @JDomain(property = "frontend.domains")
    public JOperationInfo<JLink> deleteLink(@PathVariable(value = "id") long id) throws JLinkException {
		linkService.deleteById(id);
        return new JOperationInfo<JLink>("Link delete success!", true);
    }

	private String generateRandLink(String rootUrl) {
		String result = "";
		do {
			result = "http://" + rootUrl + "/" + getRandomString(randomStringLenght);
		} while (!linkService.isFreeByUrl(result));

		return result;
	}

	private String getRandomString(int lenght) {
		StringBuilder sb = new StringBuilder();
		Random rd = new Random();

		for (int x = 0; x < lenght; x++) {
			char ch = (char) (0x61 + rd.nextInt(26));
			sb.append(ch);
		}

		return sb.toString();
	}

}
