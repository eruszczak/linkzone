package pl.reryk.linkzone;

import pl.reryk.linkzone.dto.AccountCreate;
import pl.reryk.linkzone.exception.NotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import pl.reryk.linkzone.model.*;
import pl.reryk.linkzone.service.*;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ModelsCascadingTest {

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected PostService postService;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected CommentService commentService;

    @Autowired
    protected GroupMembershipService groupMembershipService;

    private Account account;
    private Group group;
    private Post post;
    private Comment comment;
    private GroupMembership groupMembership;

    @Before
    public void setup() {
        AccountCreate dto = new AccountCreate();
        dto.setUsername("username");
        dto.setPassword("password");
        dto.setEmail("dsadaeqw23@gmail.com");
        dto.setPasswordConfirm("password");
//        account.setPasswordConfirm("password");
        this.account = accountService.create(dto);

        Group group = new Group();
        group.setName("name");
        group.setCreator(this.account);
        this.group = groupService.save(group);

        Post post = new Post();
        post.setContent("content");
        post.setAccount(this.account);
        post.setTitle("qweweq");
//        this.group.addPost(post);
        post.setGroup(this.group);
        this.post = postService.save(post);

        Comment comment = new Comment();
        comment.setContent("content");
        comment.setAccount(this.account);
        comment.setPost(this.post);
        this.comment = commentService.save(comment);

        GroupMembership groupMembership = new GroupMembership();
        groupMembership.setAccount(this.account);
        groupMembership.setGroup(this.group);
        // TODO: unique
        this.groupMembership = groupMembershipService.save(groupMembership);

        System.out.println("before");
        System.out.println("posts: " + postService.findAll());
//        System.out.println("group.getPosts: " + this.group.getPosts());
        System.out.println("comments:" + commentService.findAll());
    }

    @After
    public void cleanup() {
        postService.deleteAll();
        groupMembershipService.deleteAll();
        groupService.deleteAll();
        accountService.deleteAll();
    }

    @Test
    public void postRemoved_removesItsComments() throws Exception {
        List<Comment> comments = commentService.findByPostId(post.getId());  // IF NOT USING BIDIRECTIONAL RELATIONSHIP
//        Set<Comment> comments = post.getComments();  // DOES NOT WORK
        postService.deleteNoPerm(post);
        boolean thrown = false;
        try {
            postService.findById(post.getId());
        } catch (NotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
        for (Comment comment : comments) {
            System.out.println("removed:" + comment);
            thrown = false;
            try {
                commentService.findById(comment.getId());
            } catch (NotFoundException e) {
                thrown = true;
            }
            Assert.assertTrue(thrown);
        }
    }

    @Test
    public void groupRemoved_removeItsPosts() throws Exception {
        List<Post> posts = postService.findByGroupName(group.getName());
        groupService.deleteNoPerm(group);
        for (Post post : posts) {
            System.out.println("removed: " + post);
            boolean thrown = false;
            try {
                postService.findById(post.getId());
            } catch (NotFoundException e) {
                thrown = true;
            }
            Assert.assertTrue(thrown);
        }
    }

    @Test
    public void groupRemoved_removeItsGroupMemberships() throws Exception {
        groupMembershipService.findByAccountUsernameAndGroupName(
                account.getUsername(), group.getName());
        groupService.deleteNoPerm(group);
        boolean thrown = false;
        try {
            groupMembershipService.findByAccountUsernameAndGroupName(
                    account.getUsername(), group.getName());
        } catch (NotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void accountRemoved_removeItsGroupMemberships() throws Exception {
        assertNotNull(groupMembershipService.findByAccountUsernameAndGroupName(
                account.getUsername(), group.getName()));
        accountService.deleteById(account.getId());
        boolean thrown = false;
        try {
            groupMembershipService.findByAccountUsernameAndGroupName(
                    account.getUsername(), group.getName());
        } catch (NotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void accountRemoved_postAccountsAreNull() throws Exception {

    }

    @Test
    public void accountRemoved_commentAccountsAreNull() throws Exception {

    }

    @Test
    public void commentRemoved_removeItsReplies() throws Exception {

    }

    @Test
    public void accountRemoved_WHAT_HAPPENS_WITH_CREATED_GROUPS() throws Exception {

    }

    @Test
    public void groupRemoved_removePosts() throws Exception {

    }
}
