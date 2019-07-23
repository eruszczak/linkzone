package pl.reryk.linkzone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.reryk.linkzone.config.FileStorageProperties;
import pl.reryk.linkzone.dto.AccountCreate;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.permissions.RoleName;
import pl.reryk.linkzone.repository.AccountRepository;
import pl.reryk.linkzone.repository.CommentUpvoteRepository;
import pl.reryk.linkzone.repository.PostUpvoteRepository;
import pl.reryk.linkzone.security.JwtTokenProvider;
import pl.reryk.linkzone.service.*;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
                           AccountRepository accountRepository,
                           PostUpvoteRepository postUpvoteRepository,
                           CommentUpvoteRepository commentUpvoteRepository,
                           @Value("${spring.jpa.hibernate.ddl-auto:none}") String update) {
        return (evt) -> {
            boolean runInit = !update.equals("update");
            if (runInit) {
                Account global = accountService.create(getAccountDto("admin"), RoleName.ADMIN);

//                Account group1admin = accountService.create(getAccountDto("group1admin"));
//                Account group1mod = accountService.create(getAccountDto("group1mod"));
//                Account group1creator = accountService.create(getAccountDto("group1creator"));
////                Account group1postcreator = accountService.create(getAccountDto("group1postcreator"));
////                Account postcommenter = accountService.create(getAccountDto("postcommenter"));
////                Account reply = accountService.create(getAccountDto("reply"));
//                Account user1 = accountService.create(getAccountDto("user1"));
//
//                Group g2 = new Group("groupxxx", "group description");
//                Group g3 = new Group("group3", "group description");
//                Group g4 = new Group("random", "group description");
//                g2.setCreator(group1admin);
//                g2.addAdministrator(group1mod);
//                g2.addAdministrator(group1creator);
//                g2.addModerator(user1);
//                g3.setCreator(group1admin);
//                g4.setCreator(group1admin);
//                g3.setDefault(true);
//                groupService.save(g2);
//                groupService.save(g3);
//                groupService.save(g4);
//
//                Post p2 = new Post();
//                p2.setAccount(group1admin);
//                p2.setPostType(PostType.POST);
//                p2.setTitle("postTitle " + g2.getName());
//                p2.setContent("description ");
//                p2.setGroup(g2);
//                p2 = postService.save(p2);
//
//                Post p3 = new Post();
//                p3.setAccount(group1admin);
//                p3.setPostType(PostType.POST);
//                p3.setTitle("xxxxxxx " + g2.getName());
//                p3.setContent("yyyyy ");
//                p3.setGroup(g3);
//                p3 = postService.save(p3);
//
//                Post p4 = new Post();
//                p4.setAccount(group1admin);
//                p4.setPostType(PostType.POST);
//                p4.setTitle("321 " + g2.getName());
//                p4.setContent("321213 ");
//                p4.setGroup(g2);
//                p4 = postService.save(p4);
//
//                Post random = new Post();
//                random.setAccount(group1admin);
//                random.setPostType(PostType.POST);
//                random.setTitle("random " + g2.getName());
//                random.setContent("321213 ");
//                random.setGroup(g4);
//                random = postService.save(random);
//
//                PostUpvote postUpvote = new PostUpvote();
//                postUpvote.setPost(p2);
//                postUpvote.setIsUpvote(1);
//                postUpvote.setAccount(group1admin);
//                postUpvoteRepository.save(postUpvote);
//
//                PostUpvote x = new PostUpvote();
//                x.setPost(p2);
//                x.setIsUpvote(1);
//                x.setAccount(group1creator);
//                postUpvoteRepository.save(x);
//
//                CommentCreate xd = new CommentCreate();
//                xd.setContent("comment");
//                Comment c = commentService.create(xd, p2, group1admin);
//
//                CommentCreate commentCreate = new CommentCreate();
//                commentCreate.setContent("reply");
//                commentService.createReply(c, commentCreate, group1admin);
//
//                CommentUpvote cm = new CommentUpvote();
//                cm.setAccount(user1);
//                cm.setComment(c);
//                cm.setIsUpvote(1);
//                commentUpvoteRepository.save(cm);
//
//                GroupMembership gm = new GroupMembership(g2, group1admin);
//                groupMembershipService.save(gm);

//                for (int i = 0; i < 30; i++) {
//                    Group g = new Group("group1" + i, "group description");
//                    g.setCreator(group1creator);
//                    g.addAdministrator(group1admin);
//                    g.addModerator(group1mod);
//                    g.addModerator(group1admin);
//                    g.setCreator(group1creator);
//                    groupService.save(g);
//
//                    for (int j = 0; j < 30; j++) {
//                        Post p = new Post();
//                        p.setTitle("postTitle " + g.getName());
//                        p.setContent("description ");
//                        p.setGroup(g);
//                        p.setPostType(PostType.POST);
//                        p.setAccount(group1postcreator);
//
//                        Post newPost = postService.save(p);
//
//
//                        for (int x = 0; x < 10; x += 1) {
//                            CommentCreate comment = new CommentCreate();
//                            comment.setContent("postcommenter" + x);
//                            Comment comment1 = commentService.create(comment, newPost, postcommenter);
//                            CommentCreate commentCreate = new CommentCreate();
//                            commentCreate.setContent("reply");
//                            commentService.createReply(comment1, commentCreate, reply);
//                        }
//                    }
//                }








//				Account user1 = accountService.create(getAccountDto("admin1"));
//				Account user2 = accountService.create(getAccountDto("admin2"));
//				Account user3 = accountService.create(getAccountDto("mod1"));
//				Account user4 = accountService.create(getAccountDto("mod2"));
//				Account userNormal = accountService.create(getAccountDto("user1"));
//				Account userNormal2 = accountService.create(getAccountDto("user2"));
//				System.out.println(user1.getUsername() + ": " + jwtTokenProvider.generateToken(user1));
//				System.out.println(user2.getUsername() + ": " + jwtTokenProvider.generateToken(user2));
////				System.out.println(accountService.findAll());
////				System.out.println(accountRepository.countUsers());
////				System.out.println(accountRepository.findByUsername("admin1"));
////				accountService.findByUsernameOrEmail("Admin11", "Ds@gmail.com");
////				Account admin2 = accountService.save(new Account("ADMIN1", "password"));
////				Account a = accountService.findByUsernameIgnoreCase("admin");
//
//				Group g;
//				List<Group> groups = new ArrayList<>();
//				for (int i = 0; i < 52; i += 1) {

//					if (i % 2 == 0) {
//						g.addAdministrator(user1);
//						g.addModerator(user3);
//						g.addModerator(user4);
//					} else {
//						g.addModerator(user3);
//						g.addAdministrator(user2);
//					}
//					groupService.save(g);
//					groups.add(g);
//					if (i < 3) {
//						for (int j = 0; j < 52; j += 1) {
//							Post p = new Post();
//							// TODO type is required
//							p.setTitle("postTitle " + j);
//							p.setContent("description " + j);
//							p.setGroup(g);
//							p.setPostType(PostType.POST);
//							if (j < 11) {
//								p.setAccount(userNormal);
//							} else {
//								p.setAccount(userNormal2);
//							}
//							Post newPost = postService.save(p);
//
//							if (j < 5) {
//								for (int x = 0; x < 10; x += 1) {
//									CommentCreate comment = new CommentCreate();
//									comment.setContent("comment" + x);
//									commentService.create(comment, newPost, user1);
//								}
//							}
//						}
//					}
//				}
//
//				for (int i = 0; i < 10; i += 1) {
//					GroupMembership gm = new GroupMembership(groups.get(i), user1);
//					groupMembershipService.save(gm);
//				}

            }
        };
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
