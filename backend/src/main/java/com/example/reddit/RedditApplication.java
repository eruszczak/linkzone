package com.example.reddit;

import com.example.reddit.config.FileStorageProperties;
import com.example.reddit.controller.post.PostType;
import com.example.reddit.dto.AccountCreate;
import com.example.reddit.dto.CommentCreate;
import com.example.reddit.model.*;
import com.example.reddit.permissions.RoleName;
import com.example.reddit.repository.AccountRepository;
import com.example.reddit.security.JwtTokenProvider;
import com.example.reddit.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class RedditApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedditApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner init(GroupService groupService,
						   AccountService accountService,
						   GroupMembershipService groupMembershipService,
						   PostService postService,
						   CommentService commentService,
						   JwtTokenProvider jwtTokenProvider,
						   AccountRepository accountRepository) {
		return (evt) -> {
			if (true) {
				Account user1 = accountService.create(getAccountDto("admin1"));
				Account user2 = accountService.create(getAccountDto("admin2"));
				Account user3 = accountService.create(getAccountDto("mod1"));
				Account user4 = accountService.create(getAccountDto("mod2"));
				Account userNormal = accountService.create(getAccountDto("user1"));
				Account userNormal2 = accountService.create(getAccountDto("user2"));
				System.out.println(user1.getUsername() + ": " + jwtTokenProvider.generateToken(user1));
				System.out.println(user2.getUsername() + ": " + jwtTokenProvider.generateToken(user2));
//				System.out.println(accountService.findAll());
//				System.out.println(accountRepository.countUsers());
//				System.out.println(accountRepository.findByUsername("admin1"));
//				accountService.findByUsernameOrEmail("Admin11", "Ds@gmail.com");
//				Account admin2 = accountService.save(new Account("ADMIN1", "password"));
//				Account a = accountService.findByUsernameIgnoreCase("admin");

				Group g;
				List<Group> groups = new ArrayList<>();
				for (int i = 0; i < 52; i += 1) {
					g = new Group("groupName" + i, "group description: " + i);
					g.setCreator(user1);
					if (i % 2 == 0) {
						g.addAdministrator(user1);
						g.addModerator(user3);
						g.addModerator(user4);
					} else {
						g.addModerator(user3);
						g.addAdministrator(user2);
					}
					groupService.save(g);
					groups.add(g);
					if (i < 3) {
						for (int j = 0; j < 52; j += 1) {
							Post p = new Post();
							// TODO type is required
							p.setTitle("postTitle " + j);
							p.setContent("description " + j);
							p.setGroup(g);
							p.setPostType(PostType.POST);
							if (j < 11) {
								p.setAccount(userNormal);
							} else {
								p.setAccount(userNormal2);
							}
							Post newPost = postService.save(p);

							if (j < 5) {
								for (int x = 0; x < 10; x += 1) {
									CommentCreate comment = new CommentCreate();
									comment.setContent("comment" + x);
									commentService.create(comment, newPost, user1);
								}
							}
						}
					}
				}

// //				System.out.println(g.getAdministrators());
// 				g = groupService.findByNameFetchEager("news");
// 				System.out.println(g.getAdministrators().size());
// 				g.addAdministrator(user2);
// 				g.removeAdministrator(user1);
// 				groupService.save(g);

// 				g = groupService.findByNameFetchEager("news");
// 				System.out.println(g.getAdministrators().size());

// 				System.out.println(g.getModerators());
// 				g.addModerator(user1);
// 				groupService.save(g);
// 				System.out.println("moderators: " + g.getModerators());
// 				g.removeModerator(user1);
// 				groupService.save(g);
// 				System.out.println("moderators: " + g.getModerators());

				for (int i = 0; i < 10; i += 1) {
					GroupMembership gm = new GroupMembership(groups.get(i), user1);
					groupMembershipService.save(gm);
				}
//
// 				Post p = new Post();
// 				p.setTitle("dasdas");
// 				p.setGroup(g);
// 				p.setAccount(user2);
// 				System.out.println(p.getId());
// 				postService.save(p);
// 				System.out.println(p.getId());
// 				System.out.println(p.getGroup().getAdministrators());

// 				// TODO check if new HashSet does anything
// 				System.out.println(user1.getRoles());
// 				System.out.println(user1.getAuthorities());
// //				System.out.println(Optional.of(user1.getAuthorities())
// //						.filter(grantedAuthorities -> grantedAuthorities.contains(new SimpleGrantedAuthority(RoleName.MODERATOR.name())))
// //						.isPresent());
// 				boolean result = user1.getAuthorities().stream()
// 						.map(GrantedAuthority::getAuthority)
// 						.anyMatch(role -> role.equals(RoleName.USER.name()) || role.equals(RoleName.ADMIN.name()));
// 				System.out.println(result);
// 				Comment c = new Comment(p, user1, "commenting");
// 				Comment c2 = new Comment(p, user2, "replying to comment");
// 				c2.setParent(c);
// 				commentService.save(c);
// 				commentService.save(c2);
//                 boolean isOwner = Optional.ofNullable(c.getParent())
//                         .filter(com -> com.getAccount().equals(user1)).isPresent();
//                 System.out.println(isOwner);
// 				Random ran = new Random();
// 				int x = ran.nextInt(100000);
// 				System.out.println(x);
//				Set<Account> admins = new HashSet<>();
//				admins.add(admin1);
//				admins.add(admin2);
//				Set<Account> mods = new HashSet<>();
//				mods.add(mod1);
//				mods.add(mod2);

//			g.setAdministrators(admins);
//			g.setModerators(mods);

				// create group membership then query account or group to see if it works
			}
		};
//				Arrays.asList(
//				"jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
//				.forEach(
//						a -> {
//							Account account = accountRepository.save(new Account(a,
//									"password"));
//							bookmarkRepository.save(new Bookmark(account,
//									"http://bookmark.com/1/" + a, "A description"));
//							bookmarkRepository.save(new Bookmark(account,
//									"http://bookmark.com/2/" + a, "A description"));
//						});
	}

	private AccountCreate getAccountDto(String username) {
		String password = "password";
		AccountCreate dto = new AccountCreate();
		dto.setUsername(username);
		dto.setPassword(password);
		dto.setPasswordConfirm(password);
		dto.setEmail(username + "@gmail.com");
		return dto;
	}
}
